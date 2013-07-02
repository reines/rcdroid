package com.jamierf.rcdroid.output;

import android.content.Context;
import com.jamierf.maestro.MaestroServoController;
import com.jamierf.maestro.api.Product;
import com.jamierf.maestro.binding.AndroidDriverBinding;
import com.jamierf.maestro.binding.AsyncBindingListener;
import com.jamierf.maestro.settings.Settings;

public class ServoController {

    private MaestroServoController servos;

    public ServoController(final Context context) {
        final Settings settings = Settings.builder()
                .setNeverSuspend(true)
                .build();

        AndroidDriverBinding.bindToDevice(context, Product.MICRO6, new AsyncBindingListener<AndroidDriverBinding>() {
            @Override
            public void onBind(int vendorId, int productId, AndroidDriverBinding driver) {
                servos = new MaestroServoController(driver, settings);
                servos.setTarget(0, 1000 * 4); // Test
            }

            @Override
            public void onException(Throwable throwable) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });
    }

    public synchronized void stop() {
        if (servos == null)
            return;

        servos.close();
        servos = null;
    }
}
