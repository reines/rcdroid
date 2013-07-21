package com.jamierf.rcdroid.logic.sensors;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.jamierf.rcdroid.http.WebController;
import com.jamierf.rcdroid.http.Packet;
import com.jamierf.rcdroid.input.api.Coordinate;
import com.jamierf.rcdroid.input.sensor.RotationSensor;
import com.jamierf.rcdroid.input.sensor.listener.SensorListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class RotationSensorHandler implements SensorListener<RotationSensor> {

    private static final Logger LOG = LoggerFactory.getLogger(RotationSensorHandler.class);

    private final WebController web;

    public RotationSensorHandler(WebController web) {
        this.web = web;
    }

    @Override
    public void onSensorChanged(RotationSensor sensor) {
        final Optional<Coordinate> value = sensor.getValue();
        if (!value.isPresent())
            return;

        final Coordinate rotation = value.get();
        final long timestamp = sensor.getTimestamp();

        try {
            web.send(new Packet(Packet.Type.ROTATION_UPDATED, ImmutableMap.<String, Object>of(
                    "x", rotation.getX(),
                    "y", rotation.getY(),
                    "z", rotation.getZ()
            ), timestamp));
        } catch (IOException e) {
            LOG.warn("Failed to send rotation sensor update", e);
        }
    }
}
