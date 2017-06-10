package com.fooock.ticket.opencv;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.Collections;
import java.util.List;

/**
 *
 */
final class RealTimeProcessor {

    private static final int AREA_THRESHOLD = 700;

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
        final MatOfPoint2f approx = new MatOfPoint2f();

        Mat target = null;
        for (MatOfPoint contour : contours) {
            final MatOfPoint2f contour2f = new MatOfPoint2f(contour.toArray());

            // Here we approximate the number of contour points
            final double approxDistance = Imgproc.arcLength(contour2f, true);
            Imgproc.approxPolyDP(contour2f, approx, approxDistance * 0.02, true);

            final MatOfPoint points = new MatOfPoint(approx.toArray());
            final int pointsInt = (int) points.total();
            // Calculate the rectangle area to discard small contours
            final double area = Imgproc.contourArea(points);
            // Now if the approximated contour has four points, we assume that we have
            // found the document
            if (pointsInt == 4 && area > AREA_THRESHOLD) {
                target = points;
                break;
            }
        }

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
