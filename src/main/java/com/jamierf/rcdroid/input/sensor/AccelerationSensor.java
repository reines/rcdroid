package com.jamierf.rcdroid.input.sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import com.google.common.base.Optional;
import com.jamierf.rcdroid.input.api.Coordinate;

public class AccelerationSensor extends AbstractSensor<Coordinate> {

    private final Sensor sensor;

    private Coordinate lastValue;
    private Coordinate changeValue;

    public AccelerationSensor(SensorManager manager) {
        sensor = manager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        if (sensor == null)
            throw new RuntimeException("No accelerometer found");

        changeValue = new Coordinate(0, 0, 0);

        manager.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                final float x = event.values[0];
                final float y = event.values[1];
                final float z = event.values[2];

                synchronized (AccelerationSensor.this) {
                    if (lastValue == null)
                        lastValue = new Coordinate(x, y, z); // First value received, initialise last to current
                    else
                        lastValue.add(changeValue); // Update the last

                    // Set the change to the current minus the last
                    changeValue.set(x - lastValue.getX(), y - lastValue.getY(), z - lastValue.getZ());
                    accuracy = event.accuracy;

                    setValueChanged(event.timestamp);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) { }
        }, sensor, sensor.getMinDelay());
    }

    @Override
    public synchronized Optional<Coordinate> getValue() {
        if (this.isNoAccuracy())
            return Optional.absent();

        return Optional.of(changeValue);
    }
}
