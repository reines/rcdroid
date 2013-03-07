package com.jamierf.rcdroid.http.api;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Map;

public class Packet {

    public static enum Type {
        LOCATION_UPDATED, ACCELERATION_UPDATED, ROTATION_UPDATED, BATTERY_UPDATED;
    }

    @JsonProperty
    private final Type type;

    @JsonProperty
    private final Map<String, Object> data;

    @JsonProperty
    private final long timestamp;

    @JsonCreator
    public Packet(@JsonProperty("type") Type type, @JsonProperty("data") Map<String, Object> data, @JsonProperty("timestamp") long timestamp) {
        this.type = type;
        this.data = data;
        this.timestamp = timestamp;
    }

    public Type getType() {
        return type;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Optional<Object> get(String key) {
        return Optional.fromNullable(data.get(key));
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("type", type)
                .add("data", data)
                .add("timestamp", timestamp)
                .toString();
    }
}
