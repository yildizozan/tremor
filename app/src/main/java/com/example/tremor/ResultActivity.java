package com.example.tremor;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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

public class ResultActivity extends AppCompatActivity {
    private static final String TAG = "ResultActivity";

    private final int HEIGHT = 200;
    private ImageView imageViewRegular;
    private ImageView imageViewUser;
    private TextView textViewScore;
    private Mat result;
    private int sceneSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        imageViewRegular = findViewById(R.id.imageViewRegular);
        imageViewUser = findViewById(R.id.imageViewUser);

        textViewScore = findViewById(R.id.textViewScore);

        // Get results from MainActivity
        Intent myIntent = getIntent();
        int spiralLengthRegular = Integer.parseInt(myIntent.getStringExtra("SPIRAL_LENGTH_REGULAR"));
        int spiralLengthUser = Integer.parseInt(myIntent.getStringExtra("SPIRAL_LENGTH_USER"));

        ArrayList<PolarCoordinate> list = (ArrayList<PolarCoordinate>) myIntent.getSerializableExtra("PolarCordinates");
        Log.i("Polar Coordinates ", String.valueOf(list.size()));

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