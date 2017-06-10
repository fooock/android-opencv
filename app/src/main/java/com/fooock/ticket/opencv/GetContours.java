package com.fooock.ticket.opencv;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Class to get contours from {@link Mat} object
 */
final class GetContours {

    private static final int DEFAULT_MAX_LIST_NUMBER = 5;

    private final Mat mMat;

    GetContours(Mat mat) {
        mMat = mat;
    }

    /**
     * Get the largest five contours from the given {@link Mat}
     *
     * @return List of the five largest contours
     */
    List<MatOfPoint> contours() {
        final Mat hierarchy = new Mat();
        final List<MatOfPoint> matOfPoints = new ArrayList<>();
        Imgproc.findContours(mMat, matOfPoints, hierarchy, Imgproc.RETR_LIST,
                Imgproc.CHAIN_APPROX_SIMPLE);

        // release hierarchy fast
        hierarchy.release();

        if (matOfPoints.isEmpty()) {
            return Collections.emptyList();
        }

        // sort the contours
        sort(matOfPoints);

        if (matOfPoints.size() < DEFAULT_MAX_LIST_NUMBER) {
            return matOfPoints;
        }
        return matOfPoints.subList(0, DEFAULT_MAX_LIST_NUMBER);
    }

    /**
     * Sort the given list of points. First the largest area
     *
     * @param points List of points to be sorted
     */
    private void sort(List<MatOfPoint> points) {
        Collections.sort(points, new Comparator<MatOfPoint>() {
            @Override
            public int compare(MatOfPoint o1, MatOfPoint o2) {
                final double area1 = Imgproc.contourArea(o1);
                final double area2 = Imgproc.contourArea(o2);
                return Double.valueOf(area2).compareTo(area1);
            }
        });
    }
}
