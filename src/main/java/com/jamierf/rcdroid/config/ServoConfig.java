package com.jamierf.rcdroid.config;

import com.jamierf.maestro.api.Product;
import org.codehaus.jackson.annotate.JsonProperty;

public class ServoConfig {

    @JsonProperty
    private Product product = Product.MICRO6;

    @JsonProperty
    private int speedServoIndex = 0;

    @JsonProperty
    private int rotationServoIndex = 1;

    public Product getProduct() {
        return product;
    }

    public int getSpeedServoIndex() {
        return speedServoIndex;
    }

    public int getRotationServoIndex() {
        return rotationServoIndex;
    }
}
