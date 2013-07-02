package com.jamierf.rcdroid.input.sensor;

import android.hardware.SensorManager;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.jamierf.rcdroid.input.sensor.listener.SensorListener;

import java.util.Collection;

public abstract class AbstractSensor<T> {

    private final Collection<SensorListener> listeners;

    protected long timestamp;
    protected int accuracy;

    protected AbstractSensor() {
        listeners = Lists.newLinkedList();

        timestamp = 0;
        accuracy = 0;
    }

    public void addListener(SensorListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    public void removeListener(SensorListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    protected void setValueChanged(long timestamp) {
        synchronized (listeners) {
            this.timestamp = timestamp;

            for (SensorListener listener : listeners) {
                listener.onSensorChanged(this);
            }
        }
    }

    public void forceUpdate() {
        this.setValueChanged(System.currentTimeMillis());
    }

    public abstract Optional<T> getValue();

    public synchronized long getTimestamp() {
        return timestamp;
    }

    public synchronized int getAccuracy() {
        return accuracy;
    }

    public synchronized boolean isHighAccuracy() {
        return accuracy == SensorManager.SENSOR_STATUS_ACCURACY_HIGH;
    }

    public synchronized boolean isMediumAccuracy() {
        return accuracy == SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM;
    }

    public synchronized boolean isLowAccuracy() {
        return accuracy == SensorManager.SENSOR_STATUS_ACCURACY_LOW;
    }

    public synchronized boolean isNoAccuracy() {
        return accuracy == 0;
    }

    public void close() {
        // No-op
    }
}
