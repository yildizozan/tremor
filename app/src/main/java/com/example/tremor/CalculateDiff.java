package com.example.tremor;

import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.Stack;

public class CalculateDiff {
    private final ArrayList<Point> cartesianCoordinates;
    private final Stack<PolarCoordinate> polarCoordinates;

    /**
     *
     * @param cartesianCoordinates ArrayList<Point>
     */
    CalculateDiff(ArrayList<Point> cartesianCoordinates) {
        this.cartesianCoordinates = cartesianCoordinates;
        this.polarCoordinates = new Stack<>();
    }

    /**
     *
     * @return Stack<PolarCoordinate>
     */
    public Stack<PolarCoordinate> invoke() {
        for (int i = 0; i < cartesianCoordinates.size(); i++) {
            this.polarCoordinates.push(polar2cart(cartesianCoordinates.get(i)));
        }
        return polarCoordinates;
    }

    /**
     *
     * @param point
     */
    private PolarCoordinate polar2cart(Point point) {
        double rho = Math.sqrt(Math.pow(point.x, 2) + Math.pow(point.y, 2));
        double theta = Math.atan2((point.y), (point.x));
        return new PolarCoordinate(rho, theta);
    }

    public ArrayList<Point> getCartesianCoordinates() {
        return cartesianCoordinates;
    }

    public Stack<PolarCoordinate> getPolarCoordinates() {
        return polarCoordinates;
    }
}
