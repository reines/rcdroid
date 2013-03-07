package com.jamierf.rcdroid.input;

import android.content.Context;
import android.hardware.SensorManager;
import android.location.LocationManager;
import com.jamierf.rcdroid.input.sensor.AccelerationSensor;
import com.jamierf.rcdroid.input.sensor.BatterySensor;
import com.jamierf.rcdroid.input.sensor.LocationSensor;
import com.jamierf.rcdroid.input.sensor.RotationSensor;
import com.jamierf.rcdroid.input.sensor.listener.SensorListener;

public class SensorController {

    private final LocationSensor location;
    private final AccelerationSensor acceleration;
    private final RotationSensor rotation;
    private final BatterySensor battery;

    public SensorController(final Context context) {
        final SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        location = new LocationSensor(locationManager);
        acceleration = new AccelerationSensor(sensorManager);
        rotation = new RotationSensor(sensorManager);
        battery = new BatterySensor(context);
    }

    public void addListener(SensorListener listener) {
        location.addListener(listener);
        acceleration.addListener(listener);
        rotation.addListener(listener);
        battery.addListener(listener);
    }

    public void forceUpdate() {
        location.forceUpdate();
        acceleration.forceUpdate();
        rotation.forceUpdate();
        battery.forceUpdate();
    }

    public void close() {
        location.close();
        acceleration.close();
        rotation.close();
        battery.close();
    }
}
