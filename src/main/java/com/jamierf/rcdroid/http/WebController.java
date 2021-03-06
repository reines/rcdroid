package com.jamierf.rcdroid.http;

import android.content.res.AssetManager;
import com.google.common.collect.Lists;
import com.jamierf.rcdroid.config.WebUIConfig;
import com.jamierf.rcdroid.http.handler.AssetResourceHandler;
import com.jamierf.rcdroid.http.handler.ControlProtocolHandler;
import com.jamierf.rcdroid.http.handler.JsonPOJOHandler;
import com.jamierf.rcdroid.http.listener.ClientListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webbitserver.WebServer;
import org.webbitserver.WebServers;
import org.webbitserver.WebSocketConnection;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebController extends ControlProtocolHandler {

    private static final Logger LOG = LoggerFactory.getLogger(WebController.class);

    private final ExecutorService executor;
    private final WebServer server;
    private final Collection<ClientListener> listeners;

    public WebController(final AssetManager assets, final WebUIConfig config) throws ExecutionException, InterruptedException {
        executor = Executors.newSingleThreadExecutor();
        server = executor.submit(new Callable<WebServer>() {
            @Override
            public WebServer call() throws Exception {
                return WebServers.createWebServer(config.getPort())
                        .add("/control", WebController.this)
                        .add("/config", new JsonPOJOHandler(config))
                        .add(new AssetResourceHandler(assets))
                        .start().get();
            }
        }).get();

        listeners = Lists.newLinkedList();
    }

    public void addListener(ClientListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    public void removeListener(ClientListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    public void stop() {
        server.stop();
    }

    @Override
    protected void onConnect(final WebSocketConnection client) {
        LOG.info("Connected to client: {}", client);

        synchronized (listeners) {
            for (ClientListener listener : listeners) {
                listener.onClientConnect(client);
            }
        }
    }

    @Override
    protected void onPacket(WebSocketConnection client, Packet packet) {
        synchronized (listeners) {
            for (ClientListener listener : listeners) {
                listener.onPacket(client, packet);
            }
        }
    }
}
