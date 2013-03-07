package com.jamierf.rcdroid.http.api;

import android.location.Location;
import com.google.common.collect.ImmutableMap;

public class LocationUpdatedPacket extends Packet {

    public LocationUpdatedPacket(Location location, long timestamp) {
        super(Type.LOCATION_UPDATED, ImmutableMap.<String, Object>of(
                "altitude", location.getAltitude(),
                "bearing", location.getBearing(),
                "bearing", location.getSpeed(),
                "latitude", location.getLatitude(),
                "longitude", location.getLongitude()
        ), timestamp);
    }
}
