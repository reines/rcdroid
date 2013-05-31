var socket;
var config;
var map, marker, cube;

$(document).ready(function() {
    window.socket = new WebSocket('ws://' + document.location.host + '/control');
    console.log('Connecting...');

    socket.onopen = function() {
        console.log('Connected!');
    };

    socket.onclose = function() {
        console.log('Closed!');
    };

    socket.onmessage = function(event) {
        var packet = $.evalJSON(event.data);
        car.update(packet);
    };

    $.getJSON('http://' + document.location.host + '/config', function(config) {
        window.config = config;

        // Default position for the map
        var position = new google.maps.LatLng(0, 0);

        // Load the google maps canvas
        $('#map-canvas').gmap({
            zoom: 0,
            center: position,
            mapTypeId: google.maps.MapTypeId.SATELLITE,
            disableDefaultUI: true,
            disableDoubleClickZoom: !config.isAllowMapZoom,
            draggable: false
        });

        // Add an invisible marker for the car
        $('#map-canvas').gmap('addMarker', {
            position: position,
            clickable: false,
            visible: false
        }, function(map, marker) {
            console.log('Loaded Google Map and placed invisible marker');

            window.map = map;
            window.marker = marker;
        });

        window.cube = $('#car-canvas').cube({
            color: '#000000'
        });
    });
});
