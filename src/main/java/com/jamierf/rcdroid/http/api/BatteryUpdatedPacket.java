package com.jamierf.rcdroid.http.api;

import com.google.common.collect.ImmutableMap;
import com.jamierf.rcdroid.input.api.BatteryStatus;

public class BatteryUpdatedPacket extends Packet {

    public BatteryUpdatedPacket(BatteryStatus battery, long timestamp) {
        super(Type.BATTERY_UPDATED, ImmutableMap.<String, Object>of(
                "charging", battery.isCharging(),
                "level", battery.getLevel(),
                "temperature", battery.getTemperature(),
                "voltage", battery.getVoltage()
        ), timestamp);
    }
}
