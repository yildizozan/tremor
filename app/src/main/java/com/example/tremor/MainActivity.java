package com.example.tremor;

import static org.opencv.core.CvType.CV_8UC1;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    static {
        if (OpenCVLoader.initDebug()) {
            Log.d(TAG, "OpenCV installed successfully!");
        } else {
            Log.d(TAG, "OpenCV not installed!");
        }
    }

    //    private final Stack<Point> userMovementsStack = new Stack<>();
    int spiralLengthRegular = 0;
    private final ArrayList<Point> userMovementsRegular = new ArrayList<>();
    private final ArrayList<Point> userMovementsList = new ArrayList<>();

    private static int sceneSize;
    private ImageView imageView;
    private Button buttonDone;

    private Mat bg;
    private Mat scene;

    private Point translate(Point point) {
        double x = point.x + sceneSize / 2;
        double y = point.y + sceneSize / 2;
        return new Point(x, y);
    }

    public Point pol2cart(double rho, double phi) {
        int x = (int) (rho * Math.cos(phi));
        int y = (int) (rho * Math.sin(phi));
        return new Point(x, y);
    }

    public void drawRegularSpiral(Mat img, int a, int b, double step, int loops) {
        double theta = 0.0;
        double r = a;

        int prev_x = (int) (r * cos(theta));
        int prev_y = (int) (r * sin(theta));
        Point prev = new Point(prev_x, prev_y);

        while (theta < 2 * loops * Math.PI) {
            theta += step / r;
            r = a + b * theta;

            int x = (int) (r * cos(theta));
            int y = (int) (r * sin(theta));

            Point next = new Point(x, y);

            // Calc distance regular spiral
            spiralLengthRegular += sqrt(pow(next.x - prev.x, 2) + pow(next.y - prev.y, 2));

            Point start_ptr = translate(prev);
            Point end_ptr = translate(next);

            Scalar lineColor = new Scalar(0);
            int lineWidth = 3;

            Imgproc.line(img, start_ptr, end_ptr, lineColor, lineWidth);

            prev = new Point(x, y);
        }
    }

    void drawUserSpiral(Point newPoint) {
        Point prev;
        if (userMovementsList.isEmpty()) {
            prev = new Point(sceneSize / 2, sceneSize / 2);
        } else {
            prev = userMovementsList.get(userMovementsList.size() - 1);
        }

        Scalar lineColor = new Scalar(0);
        int lineWidth = 3;
        Imgproc.line(scene, prev, newPoint, lineColor, lineWidth);

//        Bitmap bmp = convertMat2Bitmap(scene);
//
//        imageView.setImageBitmap(bmp);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        Log.i("OnTouchEvent", x + " " + y);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP: {
                Point newPoint = new Point(x, y);
                drawUserSpiral(newPoint);
                userMovementsList.add(newPoint);
                imageView.invalidate();
                return true;
            }
        }

        return false;
    }

    public int calcDistance(ArrayList<Point> points) {
        int sum = 0;
        Point prev = userMovementsList.get(0);
        for (int i = 1; i < points.size(); i++) {
            // Calc
            Point next = userMovementsList.get(i);
            double x = next.x - prev.x;
            double y = next.y - prev.y;
            double d = sqrt(pow(x, 2) + pow(y, 2));
            sum += d;
            // Set new prev
            prev = userMovementsList.get(i);
        }
        return sum;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        sceneSize = Math.min(displayMetrics.heightPixels, displayMetrics.widthPixels);

        Log.d("Dims", displayMetrics.heightPixels + " " + displayMetrics.widthPixels);

        imageView = findViewById(R.id.imageView);
        buttonDone = findViewById(R.id.buttonDone);

        scene = new Mat(sceneSize, sceneSize, CvType.CV_8U, new Scalar(255));
        bg = new Mat(sceneSize, sceneSize, CvType.CV_8U, new Scalar(255));
        drawRegularSpiral(bg, 10, 16, 0.1, 5);
        Log.d("Regular Spiral Distance", String.valueOf(spiralLengthRegular));

        //mat is the Mat object to be converted
//        Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2RGBA);

        Mat rChannel = new Mat(sceneSize, sceneSize, CvType.CV_8U, new Scalar(255));

        Mat dst = new Mat(sceneSize, sceneSize, CvType.CV_32FC3);
        Core.merge(new ArrayList<>(Arrays.asList(bg, scene, rChannel)), dst);

        //Create Bitmap object
        Bitmap bmp = convertMat2Bitmap(dst);
        imageView.setImageBitmap(bmp);
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int x = (int) event.getX();
                int y = (int) event.getY();

//                Log.i("OnTouchEvent", x + " " + y);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                    case MotionEvent.ACTION_UP: {
                        Point newPoint = new Point(x, y);

                        //
                        drawUserSpiral(newPoint);

                        // Add user movement
                        userMovementsList.add(newPoint);

                        // Merge bg and user input
                        Bitmap result = prepareMats();

                        imageView.setImageBitmap(result);

                        // Force refresh imageview
                        imageView.invalidate();
                        return true;
                    }
                }

                return false;
            }
        });

        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int spiralLengthUser = calcDistance(userMovementsList);
                Log.d("SPIRAL_LENGTH_USER", String.valueOf(spiralLengthUser));

                Context context = v.getContext();
                Intent intent = new Intent(context, ResultActivity.class);

                // Calculation polar coordinates
                CalculateDiff polarCoords = new CalculateDiff(userMovementsList);
                polarCoords.invoke();
                intent.putExtra("PolarCordinates", (Serializable) polarCoords.getPolarCoordinates());


                intent.putExtra("SPIRAL_LENGTH_REGULAR", String.valueOf(spiralLengthRegular));
                intent.putExtra("SPIRAL_LENGTH_USER", String.valueOf(spiralLengthUser));
                context.startActivity(intent);


            }
        });
    }



    private Bitmap prepareMats() {
        Mat result = new Mat();
        List<Mat> mats = new ArrayList<Mat>();
        mats.add(bg);
        mats.add(scene);
        mats.add(new Mat(bg.rows(), bg.cols(), CV_8UC1));
        Core.merge(mats, result);

        Bitmap bmp = convertMat2Bitmap(result);

        return bmp;
    }

    public Bitmap convertMat2Bitmap(Mat mat) {
        Bitmap bmp = Bitmap.createBitmap(mat.rows(), mat.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bmp);
        return bmp;
    }
}