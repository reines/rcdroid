package com.jamierf.rcdroid.output;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import com.google.common.base.Optional;
import com.jamierf.rcdroid.MainActivity;
import com.jamierf.rcdroid.output.servo.MaestroServoController;
import com.jamierf.rcdroid.output.servo.settings.Settings;

import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

public class ServoController {

    private static final String ACTION_USB_PERMISSION = MainActivity.class.getPackage() + ".USB_PERMISSION";

    private MaestroServoController servos;

    public ServoController(final Context context) {
        final Settings settings = Settings.builder()
                .setNeverSuspend(true)
                .build();

        // Attempt to find the controller
        this.findController(context, settings);
    }

    private void findController(final Context context, final Settings settings) {
        final UsbManager manager = (UsbManager) context.getSystemService(Context.USB_SERVICE);

        // Find any attached Maestro Servo Controllers
        final Collection<UsbDevice> devices = MaestroServoController.findDevices(manager);
        if (devices.isEmpty()) {
            servos = null;
            return;
        }

        // Take the first device we found
        final UsbDevice device = devices.iterator().next();
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (!ACTION_USB_PERMISSION.equals(intent.getAction()))
                    return;

                servos = new MaestroServoController(manager, device, settings);
            }
        }, new IntentFilter(ACTION_USB_PERMISSION));
        manager.requestPermission(device, PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0));
    }

    public boolean hasServos() {
        return servos != null;
    }

    public void stop() {
        if (servos == null)
            return;

        servos.close();
        servos = null;
    }
}
