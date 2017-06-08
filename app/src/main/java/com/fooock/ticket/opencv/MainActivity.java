package com.fooock.ticket.opencv;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

/**
 *
 */
public class MainActivity extends AppCompatActivity
        implements CameraBridgeViewBase.CvCameraViewListener2, View.OnLongClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int CAMERA_REQUEST_CODE = 981;

    private static final String[] CAMERA_PERMISSION = {Manifest.permission.CAMERA};

    private JavaCameraView mJavaCameraView;
    private CheckPermission mCheckPermission;

    private final CheckVersion mCheckVersion = new CheckVersion();
    private final RealTimeProcessor mRealTimeProcessor = new RealTimeProcessor();

    /**
     * OpenCV camera loader callback
     */
    private final LoaderCallbackInterface mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case SUCCESS:
                    if (mCheckVersion.isGreaterThan(Build.VERSION_CODES.LOLLIPOP)) {
                        // check if the device has camera permissions
                        checkCameraPermission();
                    } else {
                        mJavaCameraView.enableView();
                    }
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    private void checkCameraPermission() {
        if (!mCheckPermission.isEnabled(Manifest.permission.CAMERA)) {
            requestPermissions(CAMERA_PERMISSION, CAMERA_REQUEST_CODE);
        } else {
            // its safe to enable camera
            mJavaCameraView.enableView();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCheckPermission = new CheckPermission(this);

        mJavaCameraView = (JavaCameraView) findViewById(R.id.open_cv_camera);
        mJavaCameraView.setCvCameraViewListener(this);
        mJavaCameraView.setOnLongClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (OpenCVLoader.initDebug()) {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        } else {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, mLoaderCallback);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mJavaCameraView != null) {
            mJavaCameraView.disableView();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != CAMERA_REQUEST_CODE) {
            return;
        }
        for (int i = 0; i < permissions.length; i++) {
            if (permissions[i].equals(Manifest.permission.CAMERA)
                    && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                mJavaCameraView.enableView();
                break;
            }
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        Log.d(TAG, "Camera started (w=" + width + ", h=" + height + ")");
    }

    @Override
    public void onCameraViewStopped() {
        Log.d(TAG, "Camera stopped");
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        final Mat rgba = inputFrame.rgba();
        mRealTimeProcessor.process(rgba);
        return rgba;
    }

    @Override
    public boolean onLongClick(View v) {
        Log.d(TAG, "Take a photo!");
        return false;
    }
}