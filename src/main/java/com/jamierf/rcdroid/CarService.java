package com.jamierf.rcdroid;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.util.Log;
import com.jamierf.maestro.MaestroServoController;
import com.jamierf.maestro.api.Product;
import com.jamierf.maestro.binding.AndroidDriverBinding;
import com.jamierf.maestro.binding.AsyncBindingListener;
import com.jamierf.maestro.settings.Settings;
import com.jamierf.rcdroid.http.WebController;
import com.jamierf.rcdroid.input.SensorController;
import com.jamierf.rcdroid.logic.CarEngine;

public class CarService extends Service {

    private static final int NOTIFICATION_ID = R.string.app_name;

    private NotificationManager notificationManager;
    private CarEngine engine;

    @Override
    public Binder onBind(Intent intent) {
        return new Binder();
    }

    @Override
    public void onCreate() {
        Log.i(CarActivity.TAG, "Created car service");

        notificationManager = (NotificationManager) this.getSystemService(Service.NOTIFICATION_SERVICE);

        // Connect to the servo controller
        final Settings settings = Settings.builder()
                .setNeverSuspend(true)
                .build();

        AndroidDriverBinding.bindToDevice(this, Product.MICRO6, new AsyncBindingListener<AndroidDriverBinding>() {
            @Override
            public void onBind(int vendorId, int productId, AndroidDriverBinding driver) {
                try {
                    final SensorController sensors = new SensorController(CarService.this);
                    final MaestroServoController servos = new MaestroServoController(driver, settings);
                    final WebController web = new WebController(getAssets());

                    engine = new CarEngine(CarService.this, sensors, servos, web);
                } catch (Exception e) {
                    this.onException(e);
                }
            }

            @Override
            public void onException(Throwable throwable) {
                throwable.printStackTrace(); // TODO
            }
        });
    }

    public void setNotification(int iconId, int textId) {
        final CharSequence text = this.getText(textId);

        // Create a notification with icon and text
        final Notification notification = new Notification(iconId, text, System.currentTimeMillis());

        // Set what to do when it's clicked
        final PendingIntent intent = PendingIntent.getActivity(this, 0, new Intent(this, CarActivity.class), 0);
        notification.setLatestEventInfo(this, text, text, intent);

        // Send the notification
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        // Stop the web server
        if (engine != null) {
            engine.stop();
            engine = null;
        }
    }
}
