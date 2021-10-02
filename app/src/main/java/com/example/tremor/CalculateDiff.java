package com.example.tremor;

import android.util.Log;

import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CalculateDiff {
    private final ArrayList<Point> cartesianCoordinates;
    private ArrayList<PolarCoordinate> polarCoordinates;

    /**
     *
     * @param cartesianCoordinates ArrayList<Point>
     */
    CalculateDiff(ArrayList<Point> cartesianCoordinates) {
        this.cartesianCoordinates = cartesianCoordinates;
        this.polarCoordinates = new ArrayList<>();
    }

    /**
     *
     * @return ArrayList<PolarCoordinate>
     */
    public ArrayList<PolarCoordinate> invoke(boolean isRegular) {
        /*
        Map<String, PolarCoordinate> map = new HashMap<>();
        for (int i = 0; i < cartesianCoordinates.size(); i += 100) {
            final PolarCoordinate coordinate = this.polar2cart(cartesianCoordinates.get(i));
            final int degree = (int) (coordinate.getTheta() * (180/ Math.PI));
            Log.d("THETA", String.valueOf(coordinate.getTheta() ));
            map.put(String.valueOf(degree), coordinate);
        }
         */

        if (isRegular) {
            for (int i = 0; i < cartesianCoordinates.size(); i += 100) {
                final PolarCoordinate coordinate = this.polar2cart(cartesianCoordinates.get(i));
                this.polarCoordinates.add(coordinate);
            }
        } else {
            for (int i = 0; i < cartesianCoordinates.size(); i += 1) {
                final PolarCoordinate coordinate = this.polar2cart(cartesianCoordinates.get(i));
                this.polarCoordinates.add(coordinate);
            }
        }

//        polarCoordinates = new ArrayList<>(map.values());
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

    public ArrayList<PolarCoordinate> getPolarCoordinates() {
        return polarCoordinates;
    }
}
