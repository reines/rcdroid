package com.jamierf.rcdroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity {

    public static final String TAG = "RCDroid";

    private static final String ACTION_USB_PERMISSION = MainActivity.class.getPackage() + ".USB_PERMISSION";

//    private MaestroServoController controller;
//
//    private LocationSensor location;
//    private AccelerationSensor acceleration;
//    private RotationSensor rotation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the layout
        this.setContentView(R.layout.main);

        // Start the car service
        this.startService(new Intent(this, CarService.class));

//        final Button testButton = (Button) this.findViewById(R.id.button);
//        final EditText testText = (EditText) this.findViewById(R.id.editText);
//
//        testButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final int target = Integer.parseInt(testText.getText().toString());
//                controller.setTarget(0, target);
//
//                Log.i(MainActivity.TAG, "Servo: " + controller.getStatus().get(0).toString());
//                Log.i(MainActivity.TAG, "Location: " + location.getValue());
//                Log.i(MainActivity.TAG, "Acceleration: " + acceleration.getValue());
//                Log.i(MainActivity.TAG, "Rotation: " + rotation.getValue());
//            }
//        });
//
//        final UsbManager manager = (UsbManager) super.getSystemService(Context.USB_SERVICE);
//
//        // Find any attached Maestro Servo Controllers
//        final Collection<UsbDevice> devices = MaestroServoController.findDevices(manager);
//        if (devices.isEmpty())
//            throw new RuntimeException("No Maestro Servo Controller found");
//
//        // Take the first device we found
//        final UsbDevice device = devices.iterator().next();
//        super.registerReceiver(new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                final String action = intent.getAction();
//                if (ACTION_USB_PERMISSION.equals(action)) {
//                    onReceiveUsbPermission(manager, device);
//                }
//            }
//        }, new IntentFilter(ACTION_USB_PERMISSION));
//        manager.requestPermission(device, PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0));
    }

//    protected void onReceiveUsbPermission(UsbManager manager, UsbDevice device) {
//        final Settings settings = Settings.builder()
//                .setNeverSuspend(true)
//                .build();
//
////        controller = new MaestroServoController(manager, device, settings);
//
//        final SensorManager sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
//        final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
//
//        location = new LocationSensor(locationManager);
//        acceleration = new AccelerationSensor(sensorManager);
//        rotation = new RotationSensor(sensorManager);
//    }
}
