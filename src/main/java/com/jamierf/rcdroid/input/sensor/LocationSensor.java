package com.jamierf.rcdroid.input.sensor;

import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import com.google.common.base.Optional;

public class LocationSensor extends AbstractSensor<Location> {

    private Location location;
    private boolean available;

    public LocationSensor(LocationManager manager) {
        location = null;
        available = false;

        accuracy = SensorManager.SENSOR_STATUS_ACCURACY_HIGH;

        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                LocationSensor.this.location = location;
                setValueChanged();

                timestamp = location.getTime();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                available = (status == LocationProvider.AVAILABLE);
            }

            @Override
            public void onProviderEnabled(String provider) { }

            @Override
            public void onProviderDisabled(String provider) { }
        });
    }

    @Override
    public Optional<Location> getValue() {
        if (!available)
            return Optional.absent();

        return Optional.fromNullable(location);
    }
}
