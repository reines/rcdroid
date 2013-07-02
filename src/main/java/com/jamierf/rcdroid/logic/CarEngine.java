package com.jamierf.rcdroid.logic;

import android.location.Location;
import android.util.Log;
import com.jamierf.maestro.MaestroServoController;
import com.jamierf.rcdroid.CarActivity;
import com.jamierf.rcdroid.CarService;
import com.jamierf.rcdroid.R;
import com.jamierf.rcdroid.http.ClientListener;
import com.jamierf.rcdroid.http.WebController;
import com.jamierf.rcdroid.http.api.*;
import com.jamierf.rcdroid.input.SensorController;
import com.jamierf.rcdroid.input.api.BatteryStatus;
import com.jamierf.rcdroid.input.api.Coordinate;
import com.jamierf.rcdroid.input.sensor.*;
import com.jamierf.rcdroid.input.sensor.listener.SensorListener;
import org.webbitserver.WebSocketConnection;

import java.io.IOException;

public class CarEngine implements SensorListener, ClientListener {

    private final CarService service;
    private final SensorController sensors;
    private final MaestroServoController servos;
    private final WebController web;

    public CarEngine(CarService service, SensorController sensors, MaestroServoController servos, WebController web) {
        this.service = service;
        this.sensors = sensors;
        this.servos = servos;
        this.web = web;

        sensors.addListener(this); // Register to receive sensor updates
        web.addListener(this); // Register to receive client messages

        // Set a notification in the tray
        service.setNotification(android.R.drawable.sym_def_app_icon, R.string.app_name);
    }

    @Override
    public void onSensorChanged(AbstractSensor sensor) {
        if (!sensor.getValue().isPresent())
            return;

        final long timestamp = sensor.getTimestamp();

        try {
            if (sensor instanceof AccelerationSensor) {
                final AccelerationSensor acceleration = (AccelerationSensor) sensor;

                final Coordinate value = acceleration.getValue().get();
                web.send(new AccelerationUpdatedPacket(value, timestamp));
            }
            else if (sensor instanceof LocationSensor) {
                final LocationSensor location = (LocationSensor) sensor;

                final Location value = location.getValue().get();
                web.send(new LocationUpdatedPacket(value, timestamp));
            }
            else if (sensor instanceof RotationSensor) {
                final RotationSensor rotation = (RotationSensor) sensor;

                final Coordinate value = rotation.getValue().get();
                web.send(new RotationUpdatedPacket(value, timestamp));
            }
            else if (sensor instanceof BatterySensor) {
                final BatterySensor battery = (BatterySensor) sensor;

                final BatteryStatus value = battery.getValue().get();
                web.send(new BatteryUpdatedPacket(value, timestamp));
            }
        }
        catch (IOException e) {
            Log.e(CarActivity.TAG, "Failed to send sensor update", e);
        }
    }

    public void stop() {
        sensors.close();
        servos.close();
        web.stop();
    }

    @Override
    public void onClientConnect(WebSocketConnection client) {
        sensors.forceUpdate();
    }

    @Override
    public void onPacket(WebSocketConnection client, Packet packet) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
