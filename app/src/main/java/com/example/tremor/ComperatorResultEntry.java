package com.example.tremor;

class ComperatorResultEntry {
    private final float theta;
    private final float regular;
    private final float user;

    public ComperatorResultEntry(float theta, float regular, float user) {
        this.theta = theta;
        this.regular = regular;
        this.user = user;
    }

    public float getTheta() {
        return theta;
    }

    public float getRegular() {
        return regular;
    }

    public float getUser() {
        return user;
    }
}
