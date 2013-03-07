package com.jamierf.rcdroid.http;

import android.content.Context;
import android.content.res.AssetManager;
import android.location.Location;
import android.util.Log;
import com.google.common.base.Optional;
import com.jamierf.rcdroid.MainActivity;
import com.jamierf.rcdroid.http.api.*;
import com.jamierf.rcdroid.http.handler.AssetResourceHandler;
import com.jamierf.rcdroid.http.handler.ControlProtocolHandler;
import com.jamierf.rcdroid.input.SensorController;
import com.jamierf.rcdroid.input.api.BatteryStatus;
import com.jamierf.rcdroid.input.api.Coordinate;
import com.jamierf.rcdroid.input.sensor.*;
import com.jamierf.rcdroid.input.sensor.listener.SensorListener;
import com.jamierf.rcdroid.output.ServoController;
import org.webbitserver.WebServer;
import org.webbitserver.WebServers;
import org.webbitserver.WebSocketConnection;

import java.io.IOException;

public class WebController extends ControlProtocolHandler implements SensorListener {

    private static final int PORT = 8080;

    private final SensorController sensors;
    private final ServoController servo;

    private WebServer server;

    public WebController(final Context context, SensorController sensors, ServoController servo) {
        this.sensors = sensors;
        this.servo = servo;

        final AssetManager assets = context.getAssets();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    server = WebServers.createWebServer(WebController.PORT)
                            .add("/control", WebController.this)
                            .add(new AssetResourceHandler(assets))
                            .start().get();

                    Log.i(MainActivity.TAG, "Started websocket server at: " + server.getUri());
                } catch (Exception e) {
                    Log.e(MainActivity.TAG, "Failed to start websocket server", e);
                }
            }
        }).start();

        // Register the web controller to receive sensor updates
        sensors.addListener(this);
    }

    public void stop() {
        if (server == null)
            return;

        server.stop();
        server = null;
    }

    @Override
    protected void onConnect(WebSocketConnection client) {
//        final SocketAddress remoteAddress = client.httpRequest().remoteAddress();
        Log.i(MainActivity.TAG, "Connected to client: " + client);

        sensors.forceUpdate();
    }

    @Override
    protected void onPacket(WebSocketConnection client, Packet packet) {
        // TODO: Handle this packet
    }

    @Override
    public void onValueChanged(AbstractSensor sensor) {
        try {
            if (sensor instanceof AccelerationSensor) {
                final AccelerationSensor acceleration = (AccelerationSensor) sensor;
                this.onAccelerationValueChanged(acceleration.getValue(), acceleration.getTimestamp());
            }
            else if (sensor instanceof LocationSensor) {
                final LocationSensor location = (LocationSensor) sensor;
                this.onLocationValueChanged(location.getValue(), location.getTimestamp());
            }
            else if (sensor instanceof RotationSensor) {
                final RotationSensor rotation = (RotationSensor) sensor;
                this.onRotationValueChanged(rotation.getValue(), rotation.getTimestamp());
            }
            else if (sensor instanceof BatterySensor) {
                final BatterySensor battery = (BatterySensor) sensor;
                this.onBatteryValueChanged(battery.getValue(), battery.getTimestamp());
            }
        }
        catch (IOException e) {
            Log.e(MainActivity.TAG, "Failed to send sensor update", e);
        }
    }

    private void onAccelerationValueChanged(Optional<Coordinate> value, long timestamp) throws IOException {
        // If there is a value then send it
        if (value.isPresent())
            super.send(new AccelerationUpdatedPacket(value.get(), timestamp));
    }

    private void onLocationValueChanged(Optional<Location> value, long timestamp) throws IOException {
        // If there is a value then send it
        if (value.isPresent())
            super.send(new LocationUpdatedPacket(value.get(), timestamp));
    }

    private void onRotationValueChanged(Optional<Coordinate> value, long timestamp) throws IOException {
        // If there is a value then send it
        if (value.isPresent())
            super.send(new RotationUpdatedPacket(value.get(), timestamp));
    }

    private void onBatteryValueChanged(Optional<BatteryStatus> value, long timestamp) throws IOException {
        // If there is a value then send it
        if (value.isPresent())
            super.send(new BatteryUpdatedPacket(value.get(), timestamp));
    }
}
