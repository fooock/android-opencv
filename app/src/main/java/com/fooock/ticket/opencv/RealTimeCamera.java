package com.fooock.ticket.opencv;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;

import org.opencv.android.JavaCameraView;

/**
 * This class extends the {@link JavaCameraView} class to use the camera to take photos
 */
public class RealTimeCamera extends JavaCameraView implements Camera.PictureCallback {
    private static final String TAG = RealTimeCamera.class.getSimpleName();

    private PictureResult mPictureResult;

    public RealTimeCamera(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Take a photo using the OpenCV camera object
     */
    public void takePhoto() {
        Log.d(TAG, "Prepared to take photo...");
        mCamera.setPreviewCallback(null);
        mCamera.takePicture(null, null, this);
    }

    /**
     * Add the {@link PictureResult} callback to receive the result of calling
     * the {@link #takePhoto()} method
     *
     * @param pictureResult Callback to send the result of take a photo
     */
    public void setPictureResult(PictureResult pictureResult) {
        mPictureResult = pictureResult;
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        mCamera.setPreviewCallback(this);
        mCamera.startPreview();

        if (mPictureResult == null) {
            Log.w(TAG, "Can't send the result of take photo to null listener");
            return;
        }
        mPictureResult.onPictureTaken(data);
    }

    /**
     * @return Current picture size
     */
    Camera.Size size() {
        return mCamera.getParameters().getPictureSize();
    }

    /**
     * Callback to send the result of the take photo action
     */
    interface PictureResult {

        /**
         * Called whe the picture is taken
         *
         * @param picture picture bytes
         */
        void onPictureTaken(byte[] picture);
    }
}
