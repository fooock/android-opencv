package com.fooock.ticket.opencv;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.Surface;

import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 *
 */
final class TakePhoto extends AbstractAction {

    private static final String TAG = TakePhoto.class.getName();

    static final int FRONT_CAMERA = Camera.CameraInfo.CAMERA_FACING_FRONT;
    static final int BACK_CAMERA = Camera.CameraInfo.CAMERA_FACING_BACK;

    private final int mCameraId;
    private final Context mContext;
    private final Callback mCallback;
    private final Handler mHandler = new Handler();

    private CameraDevice mCameraDevice;
    private ImageReader mImageReader;

    /**
     * Called when the capture is complete and we can send the result in the main thread.
     * Only called in android versions >= 21
     */
    private final ImageReader.OnImageAvailableListener mImageAvailableListener
            = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            // now save the image
            final Image image = reader.acquireLatestImage();
            // jpeg only has 1 plane
            final Image.Plane plane = image.getPlanes()[0];
            final ByteBuffer buffer = plane.getBuffer();
            byte[] picture = new byte[buffer.remaining()];
            buffer.get(picture);

            // close the image
            image.close();
            Log.d(TAG, "Image available, sending...");
            notifyResult(picture);
        }
    };

    /**
     * Callback for the CameraManager. Only called in android >= 21
     */
    @SuppressLint("NewApi")
    private final CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            Log.d(TAG, "Camera opened");
            mCameraDevice = camera;

            // obtain device screen sizes
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            int height = Resources.getSystem().getDisplayMetrics().heightPixels;

            try {
                mImageReader = ImageReader.newInstance(width, height,
                        ImageFormat.JPEG, 1);
                mImageReader.setOnImageAvailableListener(mImageAvailableListener, null);

                final ArrayList<Surface> surfaces = new ArrayList<>(1);
                surfaces.add(mImageReader.getSurface());

                camera.createCaptureSession(surfaces, mCaptureSessionCallback, null);
                Log.d(TAG, "Created capture session");

            } catch (CameraAccessException e) {
                Log.d(TAG, "Opening camera found an error: %s", e);
                // close the camera device as quickly as possible
                camera.close();
                notifyError();
            }
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            Log.d(TAG, "Camera device disconnected");
            // close the camera device as quickly as possible
            camera.close();
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            Log.d(TAG, "Camera device error found: "
                    + error);
            // close the camera device as quickly as possible
            camera.close();
            notifyError();
        }
    };

    /**
     * Make the capture. Only called in android >= 21
     */
    @SuppressLint("NewApi")
    private final CameraCaptureSession.StateCallback mCaptureSessionCallback
            = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(CameraCaptureSession session) {
            try {
                final CaptureRequest.Builder captureRequest
                        = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
                captureRequest.addTarget(mImageReader.getSurface());
                Log.d(TAG, "Preparing for capture");
                session.capture(captureRequest.build(), mCaptureCallback, null);

            } catch (CameraAccessException e) {
                Log.d(TAG, "Capture session exception: %s", e);
                session.close();
                notifyError();
            }
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession session) {
            Log.d(TAG, "Configuration failed");
            session.close();
        }
    };

    /**
     * Close all resources when the capture is completed. Only called in android >= 21
     */
    @SuppressLint("NewApi")
    private final CameraCaptureSession.CaptureCallback mCaptureCallback
            = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request,
                                       TotalCaptureResult result) {
            Log.d(TAG, "Capture complete, free all resources");
            session.close();
            mCameraDevice.close();
            mCameraDevice = null;
        }
    };

    /**
     * Callback to send the picture. Only called in android versions < 21
     */
    private final Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d(TAG, "Picture taken");
            notifyResult(data);
        }
    };

    /**
     * Create this object
     *
     * @param camera     type of camera in the device, front or back
     * @param context    application context
     * @param executor   implementation of {@link Executor}
     * @param mainThread implementation of {@link MainThread}
     * @param callback   implementation of {@link Callback}
     */
    TakePhoto(int camera, Context context, Executor executor,
              MainThread mainThread, Callback callback) {
        super(executor, mainThread);
        mCameraId = camera;
        mContext = context;
        mCallback = callback;
    }

    /**
     * Create this object. Default camera is the back camera
     *
     * @param context    application context
     * @param executor   implementation of {@link Executor}
     * @param mainThread implementation of {@link MainThread}
     * @param callback   implementation of {@link Callback}
     */
    TakePhoto(Context context, Executor executor,
              MainThread mainThread, Callback callback) {
        this(BACK_CAMERA, context, executor, mainThread, callback);
    }

    @Override
    public void run() {
        Log.d(TAG, "Checking if device has camera");
        boolean hasCamera = mContext.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA);

        if (hasCamera) {
            Log.d(TAG, "Device has camera");

            final CheckPermission checkPermission = new CheckPermission(mContext);
            boolean cameraPerm = checkPermission.isEnabled(Manifest.permission.CAMERA);

            final CheckVersion checkVersion = new CheckVersion();
            boolean android21OrGreater = checkVersion.isEqualOrGreater(
                    Build.VERSION_CODES.LOLLIPOP);

            if (android21OrGreater) {
                final CameraManager cameraManager = (CameraManager) mContext.getSystemService(
                        Context.CAMERA_SERVICE);
                try {
                    String[] cameraIdList = cameraManager.getCameraIdList();
                    String cameraId = cameraIdList[mCameraId];
                    // require camera permission
                    if (cameraPerm) {
                        Log.d(TAG, "Opening camera to take photo");

                        cameraManager.openCamera(cameraId, mStateCallback, mHandler);

                    } else {
                        Log.d(TAG, "No camera permission found");
                    }

                } catch (CameraAccessException e) {
                    Log.d(TAG, "Error opening camera: %s", e);
                    notifyError();
                }

            } else {
                Camera camera = null;
                try {
                    if (cameraPerm) {
                        // require camera permission
                        camera = Camera.open(mCameraId);
                        camera.takePicture(null, null, mPictureCallback);
                    }

                } catch (Exception e) {
                    Log.d(TAG, "Error opening camera: %s", e);
                    notifyError();

                } finally {
                    if (camera != null) {
                        camera.release();
                    }
                }
            }

        } else {
            Log.d(TAG, "Device has not camera");
            notifyNoCameraFound();
        }
    }

    /**
     * Send in the {@link MainThread} that an error occurs
     */
    private void notifyError() {
        mMainThread.execute(new Runnable() {
            @Override
            public void run() {
                mCallback.errorOccurred();
            }
        });
    }

    /**
     * Send in the {@link MainThread} the picture taken
     *
     * @param picture bytes of the picture
     */
    private void notifyResult(final byte[] picture) {
        mMainThread.execute(new Runnable() {
            @Override
            public void run() {
                mCallback.onPictureTaken(picture);
            }
        });
    }

    /**
     * Send in the {@link MainThread} that no camera available in the device
     */
    private void notifyNoCameraFound() {
        mMainThread.execute(new Runnable() {
            @Override
            public void run() {
                mCallback.cameraNotAvailable();
            }
        });
    }

    /**
     * Callback to send the result of this action
     */
    interface Callback {

        /**
         * Called whe the picture is taken
         *
         * @param picture picture bytes
         */
        void onPictureTaken(byte[] picture);

        /**
         * No camera on this device
         */
        void cameraNotAvailable();

        /**
         * Called if an error occurs
         */
        void errorOccurred();
    }
}
