package com.jamierf.rcdroid.config;

import org.codehaus.jackson.annotate.JsonProperty;

public class CarConfig {

    @JsonProperty
    private ServoConfig servos = new ServoConfig();

    @JsonProperty
    private WebUIConfig web = new WebUIConfig();

    public ServoConfig getServos() {
        return servos;
    }

    public WebUIConfig getWeb() {
        return web;
    }
}
