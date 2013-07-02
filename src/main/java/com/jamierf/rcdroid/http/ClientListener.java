package com.jamierf.rcdroid.http;

import com.jamierf.rcdroid.http.api.Packet;
import org.webbitserver.WebSocketConnection;

public interface ClientListener {

    public void onClientConnect(WebSocketConnection client);
    public void onPacket(WebSocketConnection client, Packet packet);
}
