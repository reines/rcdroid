package com.jamierf.rcdroid.config;

import org.codehaus.jackson.annotate.JsonProperty;

public class WebUIConfig {

    @JsonProperty
    private final int port = 8080;

    @JsonProperty
    private int mapZoomLevel = 20;

    @JsonProperty
    private boolean allowMapZoom = true;

    public int getPort() {
        return port;
    }

    public int getMapZoomLevel() {
        return mapZoomLevel;
    }

    public boolean isAllowMapZoom() {
        return allowMapZoom;
    }
}
