package com.fooock.ticket.opencv;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.Collections;
import java.util.List;

/**
 *
 */
final class RealTimeProcessor {

    private final Scalar mScalarGreen = new Scalar(0, 255, 0);

    /**
     * Transform in real time the given {@link Mat}. Do not call release in this {@link Mat}
     *
     * @param original Current screen Mat
     */
    void process(Mat original) {
        final Mat grayMat = new Mat();
        Imgproc.cvtColor(original, grayMat, Imgproc.COLOR_RGB2GRAY, 4);
        Imgproc.GaussianBlur(grayMat, grayMat, new Size(5, 5), 0);

        final Mat cannedMat = new Mat();
        Imgproc.Canny(grayMat, cannedMat, 75, 200);

        final GetContours getContours = new GetContours(cannedMat);
        final List<MatOfPoint> contours = getContours.contours();
        // Do nothing if contours is empty
        if (contours.isEmpty()) {
            return;
        }
        // Get the target contour
        final Mat target = new GetTargetContour(contours).target();
        if (target != null) {
            Imgproc.drawContours(original, Collections.singletonList(new MatOfPoint(target)),
                    -1, mScalarGreen, 3);
            target.release();
        }

        // release not needed mat
        cannedMat.release();
        grayMat.release();
    }
}
