package com.jamierf.rcdroid.logic;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.jamierf.maestro.MaestroServoController;
import com.jamierf.rcdroid.CarService;
import com.jamierf.rcdroid.R;
import com.jamierf.rcdroid.config.CarConfig;
import com.jamierf.rcdroid.http.Packet;
import com.jamierf.rcdroid.http.WebController;
import com.jamierf.rcdroid.http.listener.ClientListener;
import com.jamierf.rcdroid.http.listener.PacketListener;
import com.jamierf.rcdroid.input.SensorController;
import com.jamierf.rcdroid.logic.packets.PlayAlarmRequestHandler;
import com.jamierf.rcdroid.logic.packets.SetRotationRequestHandler;
import com.jamierf.rcdroid.logic.packets.SetSpeedRequestHandler;
import com.jamierf.rcdroid.logic.sensors.AccelerationSensorHandler;
import com.jamierf.rcdroid.logic.sensors.BatterySensorHandler;
import com.jamierf.rcdroid.logic.sensors.LocationSensorHandler;
import com.jamierf.rcdroid.logic.sensors.RotationSensorHandler;
import org.webbitserver.WebSocketConnection;

import java.util.Collection;

public class CarEngine implements ClientListener {

    private final CarService service;
    private final SensorController sensors;
    private final MaestroServoController servos;
    private final WebController web;
    private final CarConfig config;

    private final Multimap<Packet.Type, PacketListener> packetListeners;

    public CarEngine(CarService service, SensorController sensors, MaestroServoController servos, WebController web, CarConfig config) {
        this.service = service;
        this.sensors = sensors;
        this.servos = servos;
        this.web = web;
        this.config = config;

        // Register the sensor handlers
        sensors.addAccelerationListener(new AccelerationSensorHandler(web));
        sensors.addBatteryListener(new BatterySensorHandler(web));
        sensors.addLocationListener(new LocationSensorHandler(web));
        sensors.addRotationListener(new RotationSensorHandler(web));

        packetListeners = HashMultimap.create();

        // Register the packet handlers
        packetListeners.put(Packet.Type.SET_SPEED, new SetSpeedRequestHandler(servos, config.getServos().getSpeedServoIndex()));
        packetListeners.put(Packet.Type.SET_ROTATION, new SetRotationRequestHandler(servos, config.getServos().getRotationServoIndex()));
        packetListeners.put(Packet.Type.PLAY_ALARM, new PlayAlarmRequestHandler(service));

        // Register to receive client messages
        web.addListener(this);

        // Set a notification in the tray
        service.setNotification(android.R.drawable.sym_def_app_icon, R.string.app_name);
    }

    public void stop() {
        sensors.close();
        servos.close();
        web.stop();

        service.removeNotification();
    }

    @Override
    public void onClientConnect(WebSocketConnection client) {
        sensors.forceUpdate();
    }

    @Override
    public void onPacket(WebSocketConnection client, Packet packet) {
        final Collection<PacketListener> listeners = packetListeners.get(packet.getType());
        for (PacketListener listener : listeners) {
            listener.onPacket(packet);
        }
    }
}
