package com.fooock.ticket.opencv;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/**
 *
 */
final class PaintLines {

    private final Scalar mScalarGreen;
    private final Mat mOriginal;

    PaintLines(Scalar scalar, Mat original) {
        mScalarGreen = scalar;
        mOriginal = original;
    }

    void paint(Mat processed) {
        final Mat lines = new Mat();
        Imgproc.HoughLinesP(processed, lines, 3, Math.PI / 180, 50);

        for (int i = 0; i < lines.rows(); i++) {
            final double[] values = lines.get(i, 0);
            final Point pointStart = new Point(values[0], values[1]);
            final Point pointEnd = new Point(values[2], values[3]);

            // paint lines
            Imgproc.line(mOriginal, pointStart, pointEnd, mScalarGreen, 2);
        }
        lines.release();
    }
}
