package com.example.tremor;

import java.util.ArrayList;

public class PolarCordinatesUser {

    private static PolarCordinatesUser _instance = null;

    public ArrayList<PolarCoordinate> datas;

    private PolarCordinatesUser() {}

    public static PolarCordinatesUser getInstance() {
        if (_instance == null)
            _instance = new PolarCordinatesUser();

        return _instance;
    }

    public ArrayList<PolarCoordinate> getDatas() {
        return datas;
    }

    public void setDatas(ArrayList<PolarCoordinate> datas) {
        this.datas = datas;
    }
}
