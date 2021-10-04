package com.example.tremor;

import org.opencv.core.Point;

import java.util.ArrayList;

public class Comperator {
    private final ArrayList<Point> regualarCoodinates;
    private final ArrayList<Point> userCoodinates;

    private int ptrRegular;
    private int ptrUser;

    private final ArrayList<ComperatorResultEntry> result;



    /**
     * @param cartesianCoordinates ArrayList<Point>
     * @param regualarCoodinates
     * @param userCoodinates
     */
    Comperator(ArrayList<Point> cartesianCoordinates, ArrayList<Point> regualarCoodinates, ArrayList<Point> userCoodinates) {
        this.regualarCoodinates = regualarCoodinates;
        this.userCoodinates = userCoodinates;
        this.ptrRegular = 0;
        this.ptrUser = 0;

        // Result
        result = new ArrayList<>();
    }

}
