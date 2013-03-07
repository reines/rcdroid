package com.jamierf.rcdroid.output.servo.api;

public enum RequestType {
    GET(0x80),
    SET(0x40),
    BLA(0xC0);

    private final byte code;

    private RequestType(int code) {
        this.code = (byte) code;
    }

    public byte getCode() {
        return code;
    }
}
