package com.jamierf.rcdroid.http.handler;

import android.util.Log;
import com.google.common.collect.Lists;
import com.jamierf.rcdroid.MainActivity;
import com.jamierf.rcdroid.http.api.Packet;
import org.codehaus.jackson.map.ObjectMapper;
import org.webbitserver.BaseWebSocketHandler;
import org.webbitserver.WebSocketConnection;

import java.io.IOException;
import java.util.Collection;

public abstract class ControlProtocolHandler extends BaseWebSocketHandler {

    private static final ObjectMapper JSON = new ObjectMapper();

    private final Collection<WebSocketConnection> clients;

    public ControlProtocolHandler() {
        clients = Lists.newLinkedList();
    }

    protected abstract void onConnect(WebSocketConnection client);
    protected abstract void onPacket(WebSocketConnection client, Packet packet) throws Throwable;

    public void send(Packet packet) throws IOException {
        synchronized (clients) {
            if (clients.isEmpty())
                return;

            Log.v(MainActivity.TAG, "Sending " + packet);

            for (WebSocketConnection client : clients)
                this.send(client, packet);
        }
    }

    public void send(WebSocketConnection client, Packet packet) throws IOException {
        try {
            final String json = JSON.writeValueAsString(packet);
            client.send(json);
        }
        catch (Exception e) {
            Log.w(MainActivity.TAG, "Failed sending packet: " + packet, e);
        }
    }

    @Override
    public final void onOpen(WebSocketConnection client) {
        synchronized (clients) {
            clients.add(client);
        }

        this.onConnect(client);
    }

    @Override
    public final void onClose(WebSocketConnection client) {
        synchronized (clients) {
            clients.remove(client);
        }
    }

    @Override
    public final void onMessage(WebSocketConnection client, String json) throws Throwable {
        try {
            final Packet packet = JSON.readValue(json, Packet.class);
            this.onPacket(client, packet);
        }
        catch (Exception e) {
            Log.w(MainActivity.TAG, "Failed handling message: " + json, e);
        }
    }
}
