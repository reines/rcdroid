package com.jamierf.rcdroid.output;

import android.content.Context;
import com.jamierf.maestro.MaestroServoController;
import com.jamierf.maestro.api.Product;
import com.jamierf.maestro.binding.AndroidDriverBinding;
import com.jamierf.maestro.settings.Settings;

public class ServoController {

    private final MaestroServoController servos;

    public ServoController(final Context context) {
        final Settings settings = Settings.builder()
                .setNeverSuspend(true)
                .build();

        final AndroidDriverBinding driver = AndroidDriverBinding.bindToDevice(context, Product.MICRO6);
        servos = new MaestroServoController(driver, settings);
    }

    public void setTarget(int servo, int value) {
        servos.setTarget(servo, value);
    }

    public void stop() {
        servos.close();
    }
}
