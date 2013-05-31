package com.jamierf.rcdroid.http;

import org.codehaus.jackson.annotate.JsonProperty;

public class Config {

    @JsonProperty
    private int mapZoomLevel = 20;

    @JsonProperty
    private boolean allowMapZoom = true;

    public int getMapZoomLevel() {
        return mapZoomLevel;
    }

    public boolean isAllowMapZoom() {
        return allowMapZoom;
    }
}
