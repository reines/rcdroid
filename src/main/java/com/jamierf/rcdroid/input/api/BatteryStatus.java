package com.jamierf.rcdroid.input.api;

import com.google.common.base.Objects;

public class BatteryStatus {

    private final boolean charging;
    private final float voltage;
    private final float temperature;
    private final float level;

    public BatteryStatus(boolean charging, float voltage, float temperature, float level) {
        this.charging = charging;
        this.voltage = voltage;
        this.temperature = temperature;
        this.level = level;
    }

    public boolean isCharging() {
        return charging;
    }

    public float getVoltage() {
        return voltage;
    }

    public float getTemperature() {
        return temperature;
    }

    public float getLevel() {
        return level;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("charging", charging)
                .add("voltage", voltage)
                .add("temperature", temperature)
                .add("level", level)
                .toString();
    }
}
