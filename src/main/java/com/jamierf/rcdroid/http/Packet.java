package com.jamierf.rcdroid.http;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Map;

public class Packet {

    public static enum Type {
        LOCATION_UPDATED, ACCELERATION_UPDATED, ROTATION_UPDATED, BATTERY_UPDATED, // sensors
        SET_SPEED, SET_ROTATION, // servo control
        PLAY_ALARM; // other control
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

    public <T> Optional<T> get(String key) {
        return Optional.fromNullable((T) data.get(key));
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
