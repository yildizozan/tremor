package com.example.tremor;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class ResultActivity extends AppCompatActivity {
    private static final String TAG = "ResultActivity";

    private final int HEIGHT = 200;
    private ImageView imageViewRegular;
    private ImageView imageViewUser;
    private TextView textViewScore;

    private LineChart mChart;

    private Mat result;
    private int sceneSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        imageViewRegular = findViewById(R.id.imageViewRegular);
        imageViewUser = findViewById(R.id.imageViewUser);
        mChart = findViewById(R.id.chart);
        textViewScore = findViewById(R.id.textViewScore);

        // Set chart parameters
        setChartParams();

        // Get results from MainActivity
        Intent myIntent = getIntent();
        int spiralLengthRegular = Integer.parseInt(myIntent.getStringExtra("SPIRAL_LENGTH_REGULAR"));
        int spiralLengthUser = Integer.parseInt(myIntent.getStringExtra("SPIRAL_LENGTH_USER"));

//        ArrayList<PolarCoordinate> polarCordinatesRegular = (ArrayList<PolarCoordinate>) myIntent.getSerializableExtra("PolarCordinatesRegular");
//        Log.i("PolarCordinatesRegular", String.valueOf(polarCordinatesRegular.size()));

        ArrayList<PolarCoordinate> polarCordinatesRegular = PolarCordinatesRegular.getInstance().getDatas();
        Log.i("PolarCordinatesRegular", String.valueOf(polarCordinatesRegular.size()));

        ArrayList<PolarCoordinate> polarCordinatesUser = PolarCordinatesUser.getInstance().getDatas();
        Log.i("PolarCordinatesUser", String.valueOf(polarCordinatesUser.size()));

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        sceneSize = Math.min(displayMetrics.heightPixels, displayMetrics.widthPixels);

        Mat regular = new Mat(HEIGHT, sceneSize, CvType.CV_8U, new Scalar(255));
        Mat user = new Mat(HEIGHT, sceneSize, CvType.CV_8U, new Scalar(255));

        // Draw %100 bar and determine cofactor
        if (spiralLengthUser < spiralLengthRegular) {
            double cofactor = (double) spiralLengthUser / spiralLengthRegular;
            drawBar(Type.REGULAR, regular, sceneSize);
            drawBar(Type.USER, user, (int) (cofactor * sceneSize));
        } else {
            double cofactor = (double) spiralLengthRegular / spiralLengthUser;
            drawBar(Type.USER, user, sceneSize);
            drawBar(Type.REGULAR, regular, (int) (cofactor * sceneSize));
        }

        Mat empty = new Mat(HEIGHT, sceneSize, CvType.CV_8U, new Scalar(255));

        Mat dist = new Mat(HEIGHT, sceneSize, CvType.CV_32FC3);
        Core.merge(new ArrayList<>(Arrays.asList(regular, empty, empty)), dist);

        // Convert OpenCV Mat to Bitmap and set
        Bitmap bitmapRegular = convertMat2Bitmap(regular);
        imageViewRegular.setImageBitmap(bitmapRegular);
        Bitmap bitmapUser = convertMat2Bitmap(user);
        imageViewUser.setImageBitmap(bitmapUser);

        // Set score
        double cofactor = spiralLengthUser < spiralLengthRegular ?
                (double) spiralLengthUser / spiralLengthRegular :
                (double) spiralLengthRegular / spiralLengthUser;
        int score = (int)Math.round(cofactor * 100);
        textViewScore.setText(String.format("%d/100", score));


        //////////////////////////////////////////////////
        List<Entry> valsRegular = new ArrayList<>();
        List<Entry> valsUser = new ArrayList<>();

        Random rand = new Random();

        for (int i = 0; i < polarCordinatesRegular.size(); i++) {
            PolarCoordinate coordinate = polarCordinatesRegular.get(i);
            final int degree = (int) (coordinate.getTheta() * (180/ Math.PI));
            valsRegular.add(new Entry((float) i,(float) coordinate.getRho()));
//            valsRegular.add(new Entry((float) degree,(float) coordinate.getRho()));
//            valsRegular.add(new Entry((float) coordinate.getRho(),(float) degree));
        }
        for (int i = 0; i < polarCordinatesRegular.size(); i++) {
            PolarCoordinate coordinate = polarCordinatesRegular.get(i);
            final int degree = (int) (coordinate.getTheta() * (180/ Math.PI));
            final float number = -25 + new Random().nextInt( 1 + 50);
            valsUser.add(new Entry((float) i,(float) coordinate.getRho() + number));
//            valsUser.add(new Entry((float) degree,(float) coordinate.getRho()));
//            valsUser.add(new Entry((float) coordinate.getRho(),(float) degree));
        }

        LineDataSet setComp1 = new LineDataSet(valsRegular, "Regular");
        setComp1.setAxisDependency(YAxis.AxisDependency.LEFT);
        setComp1.setColor(Color.BLACK);
        setComp1.setCircleColor(Color.BLACK);
        setComp1.setDrawCircles(false);

        LineDataSet setComp2 = new LineDataSet(valsUser, "User");
        setComp2.setAxisDependency(YAxis.AxisDependency.LEFT);
        setComp2.setColor(Color.RED);
        setComp2.setCircleColor(Color.RED);
        setComp2.setDrawCircles(false);

        // use the interface ILineDataSet
        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(setComp1);
        dataSets.add(setComp2);
        LineData data = new LineData(dataSets);
        mChart.setData(data);
        mChart.invalidate(); // refresh

//        BarData data = new BarData(set);
//        set.setColors(ColorTemplate.COLORFUL_COLORS);
//        chart.setData(data);

    }

    private void setChartParams() {
        YAxis left = mChart.getAxisLeft();
        left.setDrawLabels(false); // no axis labels
        left.setDrawAxisLine(false); // no axis line
        left.setDrawGridLines(false); // no grid lines
        left.setDrawZeroLine(true); // draw a zero line
        mChart.getAxisRight().setEnabled(false); // no right axis

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.RED);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);

        Legend legend = mChart.getLegend();
        legend.setEnabled(false);

        Description description = mChart.getDescription();
        description.setEnabled(false);
    }

    private void drawBar(Type type, Mat mat, int val) {
        Scalar lineColor = new Scalar(0);
        switch (type) {
            case REGULAR:
                lineColor = new Scalar(0, 255, 0);
                break;
            case USER:
                lineColor = new Scalar(0, 0, 255);
                break;
            default:
                lineColor = new Scalar(0);
        }
        int lineWidth = 10;
        Imgproc.line(mat, new Point(0, HEIGHT / 2), new Point(val, HEIGHT / 2), lineColor, lineWidth);
    }

    private Bitmap convertMat2Bitmap(Mat mat) {
        Bitmap bmp = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bmp);
        return bmp;
    }
}