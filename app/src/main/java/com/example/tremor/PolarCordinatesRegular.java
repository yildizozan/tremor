package com.example.tremor;

import java.util.ArrayList;


public class PolarCordinatesRegular {

    private static PolarCordinatesRegular _instance = null;

    public ArrayList<PolarCoordinate> datas;

    private PolarCordinatesRegular() {}

    public static PolarCordinatesRegular getInstance() {
        if (_instance == null)
            _instance = new PolarCordinatesRegular();

        return _instance;
    }

    public ArrayList<PolarCoordinate> getDatas() {
        return datas;
    }

    public void setDatas(ArrayList<PolarCoordinate> datas) {
        this.datas = datas;
    }
}
