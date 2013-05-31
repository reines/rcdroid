var Car = Backbone.Model.extend({
    update: function(packet) {
        switch (packet.type) {
            case 'ROTATION_UPDATED': {
                this.set({
                    rotation: packet.data,
                    timestamp: packet.timestamp
                });
                break;
            }

            case 'LOCATION_UPDATED': {
                this.set({
                    location: packet.data,
                    timestamp: packet.timestamp
                });
                break;
            }

            case 'ACCELERATION_UPDATED': {
                this.set({
                    acceleration: packet.data,
                    timestamp: packet.timestamp
                });
                break;
            }

            case 'BATTERY_UPDATED': {
                this.set({
                    battery: packet.data,
                    timestamp: packet.timestamp
                });
                break;
            }
        }
    }

    /* TODO: control functions */
});

// Create a new car
var car = new Car();

car.on('change:timestamp', function(car, timestamp) {
    $('.timestamp-value').text(timestamp).removeClass('unknown');
});

car.on('change:rotation', function(car, rotation) {
    $('.rotation-value').text(rotation.x.toFixed(2) + ', ' + rotation.y.toFixed(2) + ', ' + rotation.z.toFixed(2)).removeClass('unknown');

    // Update the cubes rotation
    if (cube)
        cube.cube('rotate', rotation.x, rotation.y, rotation.z);
});

car.on('change:location', function(car, location) {
    $('.location-position-value').text(location.longitude.toFixed(5) + ', ' + location.latitude.toFixed(5)).removeClass('unknown');
    $('.location-speed-value').text(location.speed + ' m/s').removeClass('unknown');
    $('.location-bearing-value').text(location.bearing + '°').removeClass('unknown');

    // Update our position on the map
    if (map && marker) {
        var position = new google.maps.LatLng(location.latitude, location.longitude);
        map.setZoom(config.mapZoomLevel);
        map.panTo(position);

        marker.setVisible(true);
        marker.setPosition(position);
    }
});

car.on('change:acceleration', function(car, acceleration) {
    $('.acceleration-value').text(acceleration.x.toFixed(2) + ', ' + acceleration.y.toFixed(2) + ', ' + acceleration.z.toFixed(2)).removeClass('unknown');
});

car.on('change:battery', function(car, battery) {
    $('.battery-level-value').text((battery.level * 100).toFixed(0) + '%').removeClass('unknown');
    $('.battery-voltage-value').text(battery.voltage.toFixed(1) + 'v').removeClass('unknown');
    $('.battery-temperature-value').text(battery.temperature.toFixed(1) + '°C').removeClass('unknown');
});
