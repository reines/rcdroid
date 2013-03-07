var socket;

var car = {
    rotation: {},
    location: {},
    acceleration: {},
    battery: {},
    timestamp: 0,

    onTimestampUpdated: function(timestamp) {
        this.timestamp = timestamp;

        $('.timestamp-value').text(timestamp).removeClass('unknown');
    },

    /* TODO: Look in to backbone.js */
    onRotationValueChanged: function(rotation, timestamp) {
        this.rotation = rotation;

        $('.rotation-value').text(rotation.x.toFixed(2) + ', ' + rotation.y.toFixed(2) + ', ' + rotation.z.toFixed(2)).removeClass('unknown');
    },

    onLocationValueChanged: function(location, timestamp) {
        this.location = location;

        $('.location-position-value').text(location.longitude + ', ' + location.latitude).removeClass('unknown');
        $('.location-speed-value').text(location.speed).removeClass('unknown');
        $('.location-bearing-value').text(location.bearing + '°').removeClass('unknown');
    },

    onAccelerationValueChanged: function(acceleration, timestamp) {
        this.acceleration = acceleration;

        $('.acceleration-value').text(acceleration.x.toFixed(2) + ', ' + acceleration.y.toFixed(2) + ', ' + acceleration.z.toFixed(2)).removeClass('unknown');
    },

    onBatteryValueChanged: function(battery, timestamp) {
        this.battery = battery;

        $('.battery-level-value').text((battery.level * 100) + '%').removeClass('unknown');
        $('.battery-voltage-value').text(battery.voltage.toFixed(1) + 'v').removeClass('unknown');
        $('.battery-temperature-value').text(battery.temperature.toFixed(1) + '°C').removeClass('unknown');
    }
};

$(document).ready(function() {
    socket = new WebSocket('ws://' + document.location.host + '/control');
    console.log('Connecting...');

    socket.onopen = function() {
        console.log('Connected!');
    };

    socket.onclose = function() {
        console.log('Closed!');
    };

    socket.onmessage = function(event) {
        var packet = $.evalJSON(event.data);
        onPacket(packet);
    };
});

function sendPacket(data) {
    var json = $.toJSON(data);
    socket.send(json);
}

function onPacket(packet) {
    var data = packet.data;
    var timestamp = packet.timestamp;

    switch (packet.type) {
        case 'ROTATION_UPDATED': {
            car.onRotationValueChanged(data, timestamp);
            break;
        }

        case 'LOCATION_UPDATED': {
            car.onLocationValueChanged(data, timestamp);
            break;
        }

        case 'ACCELERATION_UPDATED': {
            car.onAccelerationValueChanged(data, timestamp);
            break;
        }

        case 'BATTERY_UPDATED': {
            car.onBatteryValueChanged(data, timestamp);
            break;
        }
    }

    car.onTimestampUpdated(timestamp);
}
