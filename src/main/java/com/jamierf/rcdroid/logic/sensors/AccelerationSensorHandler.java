package com.jamierf.rcdroid.logic.sensors;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.jamierf.rcdroid.http.WebController;
import com.jamierf.rcdroid.http.Packet;
import com.jamierf.rcdroid.input.api.Coordinate;
import com.jamierf.rcdroid.input.sensor.AccelerationSensor;
import com.jamierf.rcdroid.input.sensor.listener.SensorListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class AccelerationSensorHandler implements SensorListener<AccelerationSensor> {

    private static final Logger LOG = LoggerFactory.getLogger(AccelerationSensorHandler.class);

    private final WebController web;

    public AccelerationSensorHandler(WebController web) {
        this.web = web;
    }

    @Override
    public void onSensorChanged(AccelerationSensor sensor) {
        final Optional<Coordinate> value = sensor.getValue();
        if (!value.isPresent())
            return;

        final Coordinate acceleration = value.get();
        final long timestamp = sensor.getTimestamp();

        try {
            web.send(new Packet(Packet.Type.ACCELERATION_UPDATED, ImmutableMap.<String, Object>of(
                    "x", acceleration.getX(),
                    "y", acceleration.getY(),
                    "z", acceleration.getZ()
            ), timestamp));
        } catch (IOException e) {
            LOG.warn("Failed to send acceleration sensor update", e);
        }
    }
}
