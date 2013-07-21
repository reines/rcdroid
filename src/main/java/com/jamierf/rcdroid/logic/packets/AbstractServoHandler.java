package com.jamierf.rcdroid.logic.packets;

import com.jamierf.maestro.MaestroServoController;
import com.jamierf.maestro.settings.ChannelSettings;

public abstract class AbstractServoHandler {

    private final MaestroServoController servos;
    private final int index;

    private final double neutral;
    private final double negativeRange;
    private final double positiveRange;

    public AbstractServoHandler(MaestroServoController servos, int index) {
        this.servos = servos;
        this.index = index;

        final ChannelSettings channel = servos.getSettings().getChannel(index);

        neutral = channel.getNeutral();
        negativeRange = neutral - channel.getMinimum();
        positiveRange = channel.getMaximum() - neutral;
    }

    private int translateTarget(double target) {
        if (target < -100 || target > 100)
            throw new IllegalArgumentException("Set target out of range: " + target);

        if (target < 0) {
            return (int) (neutral + ((target / 100D) * negativeRange));
        }

        if (target > 0) {
            return (int) (neutral + ((target / 100D) * positiveRange));
        }

        return (int) neutral;
    }

    protected void setTarget(int target) {
        servos.setTarget(index, this.translateTarget(target));
    }
}
