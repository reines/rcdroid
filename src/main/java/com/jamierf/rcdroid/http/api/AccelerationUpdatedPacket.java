package com.jamierf.rcdroid.http.api;

import com.google.common.collect.ImmutableMap;
import com.jamierf.rcdroid.input.api.Coordinate;

public class AccelerationUpdatedPacket extends Packet {

    public AccelerationUpdatedPacket(Coordinate acceleration, long timestamp) {
        super(Type.ACCELERATION_UPDATED, ImmutableMap.<String, Object>of(
                "x", acceleration.getX(),
                "y", acceleration.getY(),
                "z", acceleration.getZ()
        ), timestamp);
    }
}
