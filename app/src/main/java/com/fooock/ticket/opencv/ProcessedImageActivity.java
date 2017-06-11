package com.fooock.ticket.opencv;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.Arrays;
import java.util.List;

public class ProcessedImageActivity extends AppCompatActivity {
    private static final String TAG = ProcessedImageActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processed_image);

        ImageView mImageProcessed = (ImageView) findViewById(R.id.img_processed_image);

        // Obtain the image from extras
        final Intent intent = getIntent();
        final byte[] imageArray = intent.getByteArrayExtra("image");

        final int width = intent.getIntExtra("width", 0);
        final int height = intent.getIntExtra("height", 0);
        if (width == 0 || height == 0) {
            Log.w(TAG, "Can't determine image size");
            return;
        }
        Log.d(TAG, "Received image (" + imageArray.length + " bytes, w="
                + width + ", h=" + height + ")");

        // Transform the byte array to Mat object
        final Size size = new Size(width, height);
        final Mat matImage = new Mat(size, CvType.CV_8U);
        matImage.put(0, 0, imageArray);
        Log.d(TAG, "Converted image byte array to Mat object");

        // Apply filters
        final Mat grayMat = new Mat();
        Imgproc.cvtColor(matImage, grayMat, Imgproc.COLOR_BayerBG2GRAY);
        Imgproc.GaussianBlur(grayMat, grayMat, new Size(5, 5), 0);

        final Mat cannedMat = new Mat();
        Imgproc.Canny(grayMat, cannedMat, 75, 200);

        // Find contours from the image
        final GetContours getContours = new GetContours(cannedMat);
        final List<MatOfPoint> contours = getContours.contours();
        if (contours.isEmpty()) {
            Log.w(TAG, "No contours found!");
            return;
        }
        // Get the large contour
        final Mat target = new GetTargetContour(contours).target();
        if (target == null) {
            Log.w(TAG, "Can't find target contour, aborting...");
            return;
        }
        Log.d(TAG, "Target contour found!");

        // Sort points
        final Point[] points = new MatOfPoint(target).toArray();
        final Point[] orderedPoints = new SortPointArray(points).sort();
        Log.d(TAG, "Points: " + Arrays.toString(orderedPoints));

        // Now apply perspective transformation
        final TransformPerspective transformPerspective = new TransformPerspective(
                points, matImage);
        final Mat transformed = transformPerspective.transform();

        // With the transformed points, now convert the image to gray scale
        // and threshold it to give it the paper effect
        Imgproc.cvtColor(transformed, transformed, Imgproc.COLOR_BayerBG2GRAY);
        Imgproc.adaptiveThreshold(transformed, transformed, 251,
                Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 15, 15);

        final Size transformedSize = transformed.size();
        final int resultW = (int) transformedSize.width;
        final int resultH = (int) transformedSize.height;

        final Mat result = new Mat(resultH, resultW, CvType.CV_8UC4);
        transformed.convertTo(result, CvType.CV_8UC4);

        final Bitmap bitmap = Bitmap.createBitmap(resultW, resultH, Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(result, bitmap);
        // Release
        transformed.release();
        result.release();
        target.release();

        mImageProcessed.setImageBitmap(bitmap);
    }
}
