package com.jamierf.rcdroid.output.servo;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.jamierf.rcdroid.MainActivity;
import com.jamierf.rcdroid.output.servo.api.Product;
import com.jamierf.rcdroid.output.servo.api.Request;
import com.jamierf.rcdroid.output.servo.api.Parameter;
import com.jamierf.rcdroid.output.servo.api.Status;
import com.jamierf.rcdroid.output.servo.settings.ChannelSettings;
import com.jamierf.rcdroid.output.servo.settings.Settings;

import java.io.Closeable;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class MaestroServoController implements Closeable {

    private static final int INSTRUCTION_FREQUENCY = 12000000;
    private static final int REQUEST_TIMEOUT = 5000;

    public static Collection<UsbDevice> findDevices(UsbManager manager) {
        final ImmutableSet.Builder<UsbDevice> controllers = ImmutableSet.builder();

        final Map<String, UsbDevice> devices = manager.getDeviceList();
        Log.i(MainActivity.TAG, "Found " + devices.size() + " USB devices");

        for (UsbDevice device : devices.values()) {
            final Optional<Product> product = Product.fromDevice(device);

            // Skip unrecognised devices
            if (!product.isPresent()) {
                Log.d(MainActivity.TAG, "Skipping unknown device: " + device);
                continue;
            }

            Log.i(MainActivity.TAG, "Found Maestro Servo Controller " + product.get() + ": " + device);
            controllers.add(device);
        }

        return controllers.build();
    }

    private static int convertBpsToSpbrg(int bps) {
        if (bps == 0)
            return 0;

        return (INSTRUCTION_FREQUENCY - bps / 2) / bps;
    }

    private static int channelToPort(int channel) {
        if (channel <= 3)
            return channel;

        if (channel < 6)
            return channel + 2;

        throw new IllegalArgumentException("Invalid channel number " + channel);
    }

    protected final Product product;
    protected final MaestroUsbConnection conn;
    protected final String serialNumber;
    protected final String firmwareVersion;

    private Settings settings;

    public MaestroServoController(UsbManager manager, UsbDevice device, Settings settings) {
        final Optional<Product> product = Product.fromDevice(device);
        if (!product.isPresent())
            throw new RuntimeException("Attempting to connect to unrecognised device");

        this.product = product.get();

        conn = new MaestroUsbConnection(manager, device, REQUEST_TIMEOUT);
        serialNumber = conn.getSerialNumber();
        firmwareVersion = conn.getFirmwareVersion();

        Log.i(MainActivity.TAG, "Connected to Maestro Servo Controller " + serialNumber + " with firmware: " + firmwareVersion);

        // Set the initial settings
        this.updateSettings(settings);
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    private void setParameter(Parameter parameter, int value) {
        final byte code = parameter.getCode();
        final Parameter.Range range = parameter.getRange();

        this.setParameter(code, range, value);
    }

    private void setParameter(Parameter parameter, int port, int value) {
        final byte code = (byte) (parameter.getCode()+ (port * 9));
        final Parameter.Range range = parameter.getRange();

        this.setParameter(code, range, value);
    }

    private void setParameter(byte code, Parameter.Range range, int value) {
        final int index = (range.getBytes() << 8) + code;

        // Ensure we are within the correct byte range
        final int mask = (int) Math.pow(2, range.getBytes()) - 1;
        value &= mask;

        // TODO: Ensure we are within the correct numeric range?

        conn.send(Request.SET_PARAMETER, value, index);
    }

    public void updateSettings(Settings settings) {
        Log.i(MainActivity.TAG, "Updating settings: " + settings);
        this.settings = settings;

        this.setParameter(Parameter.SERIAL_MODE, settings.getSerialMode().getCode());
        this.setParameter(Parameter.SERIAL_FIXED_BAUD_RATE, MaestroServoController.convertBpsToSpbrg(settings.getBaudRate()));
        this.setParameter(Parameter.SERIAL_ENABLE_CRC, settings.isEnableCrc() ? 1 : 0);
        this.setParameter(Parameter.SERIAL_NEVER_SUSPEND, settings.isNeverSuspend() ? 1 : 0);
        this.setParameter(Parameter.SERIAL_DEVICE_NUMBER, settings.getDeviceNumber());

        this.setParameter(Parameter.SERIAL_MINI_SSC_OFFSET, settings.getMiniSscOffset());
        this.setParameter(Parameter.SERIAL_TIMEOUT, settings.getTimeout());
        this.setParameter(Parameter.SCRIPT_DONE, settings.isScriptDone() ? 1 : 0);

        // Maestro Micro 6
        if (product == Product.MICRO6) {
            this.setParameter(Parameter.SERVOS_AVAILABLE, settings.getServosAvailable());
            this.setParameter(Parameter.SERVO_PERIOD, settings.getServoPeriod());
        }
        // Maestro Mini 12, 18, 24
        else {
            // TODO
//            this.setParameter(Settings.Parameter.MINI_MAESTRO_SERVO_PERIOD_L, settings.getMiniMaestroServoPeriod() & 0xFF);
//            this.setParameter(Settings.Parameter.MINI_MAESTRO_SERVO_PERIOD_HU, settings.getMiniMaestroServoPeriod() >> 8);

            // TODO: Multiplier
//            this.setParameter(Settings.Parameter.PARAMETER_SERVO_MULTIPLIER, multiplier); // TODO
        }

        // Maestro Mini 18, 24
        if (product == Product.MINI18 || product == Product.MINI24)
            this.setParameter(Parameter.ENABLE_PULLUPS, settings.isEnablePullups() ? 1 : 0);

        byte ioMask = 0;
        byte outputMask = 0;
        byte[] channelModeBytes = {0, 0, 0, 0, 0, 0};

        for (int port = 0; port < product.getPorts(); port++) {
            final ChannelSettings channel = settings.getChannel(port);

            if (product == Product.MICRO6) {
                // Set the io/output masks
                switch (channel.getChannelMode()) {
                    case OUTPUT:
                        outputMask |= (byte)(1 << MaestroServoController.channelToPort(port));
                    case INPUT:
                        ioMask |= (byte)(1 << MaestroServoController.channelToPort(port));
                }
            }
            else {
//                channelModeBytes[port >> 2] |= (byte)((byte) channel.mode << ((port & 3) << 1)); // TODO
            }

            this.setParameter(Parameter.SERVO_HOME, port, channel.getHome());
            this.setParameter(Parameter.SERVO_MIN, port, channel.getMinimum() / 64);
            this.setParameter(Parameter.SERVO_MAX, port, channel.getMaximum() / 64);
            this.setParameter(Parameter.SERVO_NEUTRAL, port, channel.getNeutral());
            this.setParameter(Parameter.SERVO_RANGE, port, channel.getRange() / 127);
            this.setParameter(Parameter.SERVO_SPEED, port, channel.getExponentialSpeed());
            this.setParameter(Parameter.SERVO_ACCELERATION, port, channel.getAcceleration());
        }

        if (product == Product.MICRO6) {
            this.setParameter(Parameter.IO_MASK_C, ioMask);
            this.setParameter(Parameter.OUTPUT_MASK_C, outputMask);
        }
        else {
            for (int port = 0; port < channelModeBytes.length; port++) {
//                this.setParameter(Settings.Parameter.CHANNEL_MODES_0_3 + port, channelModeBytes[port]); // TODO
            }
        }
    }

    // TODO: different for minis
    public List<Status> getStatus() {
        final int skip = 1 + 1 + 2 + 2 + (2 * 3) + (2 * 32) + (2 * 10) + 1 + 1;

        final int length = skip + (product.getPorts() * Status.BYTE_LENGTH); // TODO
        final ByteBuffer payload = conn.request(Request.GET_VARIABLES, length);

        // TODO: Read the first ? bytes as micro variables
        payload.position(payload.position() + skip);

        final ImmutableList.Builder<Status> channels = ImmutableList.builder();

        for (int servo = 0; servo < product.getPorts(); servo++) {
            final Status status = Status.decode(payload);
            channels.add(status);
        }

        return channels.build();
    }

    public void setTarget(int servo, int value) {
        // value is the pulse width in units of quarter-microseconds
        conn.send(Request.SET_TARGET, value, servo);
    }

    public void setSpeed(int servo, int value) {
        conn.send(Request.SET_VARIABLE, value, servo);
    }

    public void setAcceleration(int servo, int value) {
        // set the high bit of servo to specify acceleration
        conn.send(Request.SET_VARIABLE, value, (servo | 0x80));
    }

    public void clearErrors() {
        conn.send(Request.CLEAR_ERRORS);
    }

    public void close() {
        conn.close();
    }
}
