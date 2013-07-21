package com.jamierf.rcdroid.http.listener;

import com.jamierf.rcdroid.http.Packet;
import org.webbitserver.WebSocketConnection;

public interface ClientListener {

    public void onClientConnect(WebSocketConnection client);
    public void onPacket(WebSocketConnection client, Packet packet);
}
