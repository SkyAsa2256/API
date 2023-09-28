package com.envyful.api.math;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class Vector {

    private double x;
    private double y;
    private double z;

    public Vector() {
    }

    private Vector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public static Vector of(double x, double y, double z) {
        return new Vector(x, y, z);
    }

    public static Vector of(float x, float y, float z) {
        return new Vector(x, y, z);
    }
}
