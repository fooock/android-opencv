package com.fooock.ticket.opencv;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

/**
 *
 */
final class TransformPerspective {

    private static final int DEFAULT_COLS = 0;
    private static final int DEFAULT_ROWS = 0;

    private final Point[] mPoints;
    private final Mat mImageMat;

    TransformPerspective(Point[] points, Mat imageMat) {
        mPoints = points;
        mImageMat = imageMat;
    }

    Mat transform() {
        final ComputeDimension computeDimension = new ComputeDimension(mPoints);
        final double maxWidth = computeDimension.width();
        final double maxHeight = computeDimension.height();

        // Mat with the dimensions of the new image
        final Mat pointsMat = new Mat(4, 1, CvType.CV_32FC2);
        pointsMat.put(DEFAULT_ROWS, DEFAULT_COLS,
                computeDimension.topLeft().x, computeDimension.topLeft().y,
                computeDimension.topRight().x, computeDimension.topRight().y,
                computeDimension.bottomRight().x, computeDimension.bottomRight().y,
                computeDimension.bottomLeft().x, computeDimension.bottomLeft().y);

        // Math with the set of destination points. The points need to be in the correct
        // order: top-left, top-right, bottom-right and bottom-left
        final Mat dimenMat = new Mat(4, 1, CvType.CV_32FC2);
        dimenMat.put(DEFAULT_ROWS, DEFAULT_COLS,
                0.0, 0.0,
                maxWidth, 0.0,
                maxWidth, maxHeight,
                0.0, maxHeight);

        final Mat perspectiveTransform = Imgproc.getPerspectiveTransform(pointsMat, dimenMat);

        // release mat
        pointsMat.release();
        dimenMat.release();

        final Mat doc = new Mat((int) maxHeight, (int) maxWidth, CvType.CV_8UC4);
        Imgproc.warpPerspective(mImageMat, doc, perspectiveTransform, doc.size());

        // release mat
        perspectiveTransform.release();

        return doc;
    }
}
