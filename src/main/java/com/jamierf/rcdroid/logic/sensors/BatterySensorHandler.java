package com.jamierf.rcdroid.logic.sensors;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.jamierf.rcdroid.http.WebController;
import com.jamierf.rcdroid.http.Packet;
import com.jamierf.rcdroid.input.api.BatteryStatus;
import com.jamierf.rcdroid.input.sensor.BatterySensor;
import com.jamierf.rcdroid.input.sensor.listener.SensorListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class BatterySensorHandler implements SensorListener<BatterySensor> {

    private static final Logger LOG = LoggerFactory.getLogger(BatterySensorHandler.class);

    private final WebController web;

    public BatterySensorHandler(WebController web) {
        this.web = web;
    }

    @Override
    public void onSensorChanged(BatterySensor sensor) {
        final Optional<BatteryStatus> value = sensor.getValue();
        if (!value.isPresent())
            return;

        final BatteryStatus battery = value.get();
        final long timestamp = sensor.getTimestamp();

        try {
            web.send(new Packet(Packet.Type.BATTERY_UPDATED, ImmutableMap.<String, Object>of(
                    "charging", battery.isCharging(),
                    "level", battery.getLevel(),
                    "temperature", battery.getTemperature(),
                    "voltage", battery.getVoltage()
            ), timestamp));
        } catch (IOException e) {
            LOG.warn("Failed to send battery sensor update", e);
        }
    }
}
