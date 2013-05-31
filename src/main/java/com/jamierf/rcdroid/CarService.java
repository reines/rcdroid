package com.jamierf.rcdroid;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.util.Log;
import com.jamierf.rcdroid.http.Config;
import com.jamierf.rcdroid.http.WebController;
import com.jamierf.rcdroid.input.SensorController;
import com.jamierf.rcdroid.output.ServoController;

public class CarService extends Service {

    private static final int NOTIFICATION_ID = R.string.app_name;

    class CarServiceBinder extends Binder {
        CarService getService() {
            return CarService.this;
        }
    }

    private NotificationManager notificationManager;

    private ServoController servo;
    private SensorController sensors;
    private WebController web;

    @Override
    public CarServiceBinder onBind(Intent intent) {
        return new CarServiceBinder();
    }

    @Override
    public void onCreate() {
        Log.i(MainActivity.TAG, "Created car service");

        // Set a notification in the tray
        notificationManager = (NotificationManager) super.getSystemService(Service.NOTIFICATION_SERVICE);
        this.setNotification(android.R.drawable.sym_def_app_icon, R.string.app_name);

        // Connect to the servo controller
        servo = new ServoController(this);

        // Connect to the sensors
        sensors = new SensorController(this);

        // Create and start a web server for user input
        web = new WebController(this, sensors, servo);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    private void setNotification(int iconId, int textId) {
        final CharSequence text = super.getText(textId);

        // Create a notification with icon and text
        final Notification notification = new Notification(iconId, text, System.currentTimeMillis());

        // Set what to do when it's clicked
        final PendingIntent intent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
        notification.setLatestEventInfo(this, text, text, intent);

        // Send the notification
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    @Override
    public void onDestroy() {
        // Stop the servo controller
        if (servo != null) {
            servo.stop();
            servo = null;
        }

        // TODO: Stop the sensors

        // Stop the web server
        if (web != null) {
            web.stop();
            web = null;
        }
    }
}
