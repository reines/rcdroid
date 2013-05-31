package com.jamierf.rcdroid.input.sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import com.google.common.base.Optional;
import com.jamierf.rcdroid.input.api.Coordinate;

public class RotationSensor extends AbstractSensor<Coordinate> {

    private final Sensor sensor;
    private final Coordinate currentValue;

    public RotationSensor(SensorManager manager) {
        sensor = manager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        if (sensor == null)
            throw new RuntimeException("No rotation vector sensor found");

        currentValue = new Coordinate(0, 0, 0);

        manager.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                final float x = event.values[0];
                final float y = event.values[1];
                final float z = event.values[2];

                synchronized (RotationSensor.this) {
                    // TODO: Set the value
                    currentValue.set(x, y, z);
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

        return Optional.of(currentValue);
    }
}
