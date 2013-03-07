package com.jamierf.rcdroid.http.api;

import com.google.common.collect.ImmutableMap;
import com.jamierf.rcdroid.input.api.Coordinate;

public class RotationUpdatedPacket extends Packet {

    public RotationUpdatedPacket(Coordinate rotation, long timestamp) {
        super(Type.ROTATION_UPDATED, ImmutableMap.<String, Object>of(
                "x", rotation.getX(),
                "y", rotation.getY(),
                "z", rotation.getZ()
        ), timestamp);
    }
}
