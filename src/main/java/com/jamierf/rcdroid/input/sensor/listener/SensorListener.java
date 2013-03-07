package com.jamierf.rcdroid.input.sensor.listener;

import com.jamierf.rcdroid.input.sensor.AbstractSensor;

public interface SensorListener<T extends AbstractSensor> {

    public void onValueChanged(T sensor);
}
