package com.fooock.ticket.opencv;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 */
final class GetContours {

    private final Mat mMat;

    public GetContours(Mat mat) {
        mMat = mat;
    }

    List<MatOfPoint> contours() {
        final Mat hierarchy = new Mat();
        final List<MatOfPoint> matOfPoints = new ArrayList<>();
        Imgproc.findContours(mMat, matOfPoints, hierarchy, Imgproc.RETR_LIST,
                Imgproc.CHAIN_APPROX_SIMPLE);

        // release hierarchy fast
        hierarchy.release();

        // sort the contours
        sort(matOfPoints);
        return matOfPoints;
    }

    private void sort(List<MatOfPoint> points) {
        Collections.sort(points, new Comparator<MatOfPoint>() {
            @Override
            public int compare(MatOfPoint o1, MatOfPoint o2) {
                return Double.valueOf(Imgproc.contourArea(o2)).compareTo(Imgproc.contourArea(o1));
            }
        });
    }
}
