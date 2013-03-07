package com.jamierf.rcdroid.output.servo.api;

public enum Request {
    GET_FIRMWARE_VERSION(0x06, RequestType.GET),

    GET_PARAMETER(0x81, RequestType.BLA),
    SET_PARAMETER(0x82, RequestType.SET),

    GET_VARIABLES(0x83, RequestType.BLA),
    SET_VARIABLE(0x84, RequestType.SET),

    SET_TARGET(0x85, RequestType.SET),

    CLEAR_ERRORS(0x86, RequestType.SET),
    GET_SETTINGS(0x87, RequestType.BLA),

    GET_STACK(0x88, RequestType.BLA),
    GET_CALL_STACK(0x89, RequestType.BLA),
    SET_PWM(0x8A, RequestType.SET);

    private final byte code;
    private final RequestType type;

    private Request(int code, RequestType type) {
        this.code = (byte) code;
        this.type = type;
    }

    public byte getCode() {
        return code;
    }

    public RequestType getType() {
        return type;
    }
}
