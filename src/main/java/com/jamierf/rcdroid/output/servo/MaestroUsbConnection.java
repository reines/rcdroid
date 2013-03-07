package com.jamierf.rcdroid.output.servo;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import com.jamierf.rcdroid.output.servo.api.Request;
import com.jamierf.rcdroid.output.servo.api.RequestType;

import java.io.Closeable;
import java.nio.ByteBuffer;

public class MaestroUsbConnection implements Closeable {

    private final UsbManager manager;
    private final UsbDevice device;
    private final int timeout;

    private transient UsbDeviceConnection conn;

    public MaestroUsbConnection(UsbManager manager, UsbDevice device, int timeout) {
        this.manager = manager;
        this.device = device;
        this.timeout = timeout;
    }

    private synchronized UsbDeviceConnection getConnection() {
        if (conn == null)
            conn = manager.openDevice(device);

        return conn;
    }

    public String getSerialNumber() {
        final UsbDeviceConnection conn = this.getConnection();
        return conn.getSerial();
    }

    public String getFirmwareVersion() {
        final ByteBuffer payload = this.request(Request.GET_FIRMWARE_VERSION, 0x0100, 0x0000, 14);

        final byte major = (byte)((payload.get(12) & 0xF) + ((payload.get(12) >> 4 & 0xF) * 10));
        final byte minor = (byte)((payload.get(13) & 0xF) + ((payload.get(13) >> 4 & 0xF) * 10));

        return String.format("%d.%d", major, minor);
    }

    public ByteBuffer request(Request request, int length) {
        return this.request(request, 0x0000, 0x0000, length);
    }

    public ByteBuffer request(Request request, int value, int index, int length) {
        final byte[] buffer = new byte[length];
        final UsbDeviceConnection conn = this.getConnection();

        final RequestType type = request.getType();
        conn.controlTransfer(type.getCode(), request.getCode(), value, index, buffer, buffer.length, timeout);

        return ByteBuffer.wrap(buffer);
    }

    public void send(Request request) {
        this.send(request, 0x0000, 0x0000);
    }

    public void send(Request request, int value, int index) {
        final UsbDeviceConnection conn = this.getConnection();

        final RequestType type = request.getType();
        conn.controlTransfer(type.getCode(), request.getCode(), value, index, null, 0, timeout);
    }

    public synchronized void close() {
        if (conn == null)
            return;

        conn.close();
        conn = null;
    }
}
