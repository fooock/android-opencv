package com.fooock.ticket.opencv;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.imgproc.Imgproc;

import java.util.List;

/**
 *
 */
final class GetTargetContour {

    private static final int AREA_THRESHOLD = 700;

    private final List<MatOfPoint> mContours;

    GetTargetContour(List<MatOfPoint> contours) {
        mContours = contours;
    }

    Mat target() {
        final MatOfPoint2f approx = new MatOfPoint2f();

        Mat target = null;
        for (MatOfPoint contour : mContours) {
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
        approx.release();

        return target;
    }
}
