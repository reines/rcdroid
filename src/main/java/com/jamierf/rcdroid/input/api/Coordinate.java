package com.jamierf.rcdroid.input.api;

import com.google.common.base.Objects;

public class Coordinate {

    public static final Coordinate NO_COORDINATES = new Coordinate(0, 0, 0);

    private float x;
    private float y;
    private float z;

    public Coordinate(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Coordinate set(Coordinate coordinate) {
        return this.set(coordinate.x, coordinate.y, coordinate.z);
    }

    public Coordinate set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;

        return this;
    }

    public Coordinate add(Coordinate coordinate) {
        return this.add(coordinate.x, coordinate.y, coordinate.z);
    }

    public Coordinate add(float x, float y, float z) {
        this.x += x;
        this.y += y;
        this.z += z;

        return this;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("x", x)
                .add("y", y)
                .add("z", z)
                .toString();
    }
}
