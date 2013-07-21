package com.jamierf.rcdroid.logic.packets;

import com.google.common.base.Optional;
import com.jamierf.maestro.MaestroServoController;
import com.jamierf.rcdroid.http.Packet;
import com.jamierf.rcdroid.http.listener.PacketListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SetSpeedRequestHandler extends AbstractServoHandler implements PacketListener {

    private static final Logger LOG = LoggerFactory.getLogger(SetSpeedRequestHandler.class);

    public SetSpeedRequestHandler(MaestroServoController servos, int speedControllingServo) {
        super (servos, speedControllingServo);
    }

    @Override
    public void onPacket(Packet packet) {
        final Optional<Integer> value = packet.get("target");
        if (!value.isPresent())
            throw new IllegalArgumentException("Set speed request with no target speed.");

        final int target = value.get();
        LOG.trace("Setting speed to {}", target);

        super.setTarget(target);
    }
}
