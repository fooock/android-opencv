package com.fooock.ticket.opencv;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

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

        final Mat lines = new Mat();
        Imgproc.HoughLinesP(cannedMat, lines, 1, Math.PI / 180, 50);

        for (int i = 0; i < lines.rows(); i++) {
            final double[] values = lines.get(i, 0);
            final Point pointStart = new Point(values[0], values[1]);
            final Point pointEnd = new Point(values[2], values[3]);

            // paint lines
            Imgproc.line(original, pointStart, pointEnd, mScalarGreen, 3);
        }

        // release not needed mat
        lines.release();
        cannedMat.release();
        grayMat.release();
    }
}
