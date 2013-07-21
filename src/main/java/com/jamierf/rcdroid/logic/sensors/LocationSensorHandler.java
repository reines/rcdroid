package com.jamierf.rcdroid.logic.sensors;

import android.location.Location;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.jamierf.rcdroid.http.WebController;
import com.jamierf.rcdroid.http.Packet;
import com.jamierf.rcdroid.input.sensor.LocationSensor;
import com.jamierf.rcdroid.input.sensor.listener.SensorListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class LocationSensorHandler implements SensorListener<LocationSensor> {

    private static final Logger LOG = LoggerFactory.getLogger(LocationSensorHandler.class);

    private final WebController web;

    public LocationSensorHandler(WebController web) {
        this.web = web;
    }

    @Override
    public void onSensorChanged(LocationSensor sensor) {
        final Optional<Location> value = sensor.getValue();
        if (!value.isPresent())
            return;

        final Location location = value.get();
        final long timestamp = sensor.getTimestamp();

        try {
            web.send(new Packet(Packet.Type.LOCATION_UPDATED, ImmutableMap.<String, Object>of(
                    "altitude", location.getAltitude(),
                    "bearing", location.getBearing(),
                    "speed", location.getSpeed(),
                    "latitude", location.getLatitude(),
                    "longitude", location.getLongitude()
            ), timestamp));
        } catch (IOException e) {
            LOG.warn("Failed to send location sensor update", e);
        }
    }
}
