package com.example.tremor;

import java.io.Serializable;

public class PolarCoordinate implements Serializable {
    private final double rho;
    private final double theta;

    public PolarCoordinate(double rho, double theta) {
        this.rho = rho;
        this.theta = theta;
    }

    public double getRho() {
        return rho;
    }

    public double getTheta() {
        return theta;
    }

}
