package com.jamierf.rcdroid.logic.packets;

import com.google.common.base.Optional;
import com.jamierf.maestro.MaestroServoController;
import com.jamierf.rcdroid.http.Packet;
import com.jamierf.rcdroid.http.listener.PacketListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SetRotationRequestHandler extends AbstractServoHandler implements PacketListener {

    private static final Logger LOG = LoggerFactory.getLogger(SetRotationRequestHandler.class);

    public SetRotationRequestHandler(MaestroServoController servos, int rotationControllingServo) {
        super (servos, rotationControllingServo);
    }

    @Override
    public void onPacket(Packet packet) {
        final Optional<Integer> value = packet.get("target");
        if (!value.isPresent())
            throw new IllegalArgumentException("Set rotation request with no target rotation.");

        final int target = value.get();
        LOG.trace("Setting rotation to {}", target);

        super.setTarget(target);
    }
}
