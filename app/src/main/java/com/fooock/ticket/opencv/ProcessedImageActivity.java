package com.fooock.ticket.opencv;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Size;

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

        // Find contours from the image
        final GetContours getContours = new GetContours(matImage);
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

        // Now apply perspective transformation
    }
}
