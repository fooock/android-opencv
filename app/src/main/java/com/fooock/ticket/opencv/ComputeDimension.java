package com.fooock.ticket.opencv;

import org.opencv.core.Point;

/**
 * Class used to compute the width or height based in two points
 */
final class ComputeDimension {

    private final Point topLeft;
    private final Point topRight;
    private final Point bottomRight;
    private final Point bottomLeft;

    ComputeDimension(Point[] points) {
        topLeft = points[0];
        topRight = points[1];
        bottomRight = points[2];
        bottomLeft = points[3];
    }

    /**
     * Compute the width of the new image, which will be the maximum distance
     * between bottom-right and bottom-left x-axis, or the top-right and top-left x-axis
     *
     * @return Max width
     */
    double width() {
        final double w1 = euclideanDistance(bottomRight, bottomLeft);
        final double w2 = euclideanDistance(topRight, topLeft);
        return Math.max(w1, w2);
    }

    private double euclideanDistance(Point p1, Point p2) {
        return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
    }

    /**
     * Compute the height of the new image which will be the maximum distance between the
     * top-right and bottom-right y-axis or the top-left and bottom-left y-axis
     *
     * @return Max height
     */
    double height() {
        final double h1 = euclideanDistance(topRight, bottomRight);
        final double h2 = euclideanDistance(topLeft, bottomLeft);
        return Math.max(h1, h2);
    }

    /**
     * @return Top left point
     */
    Point topLeft() {
        return topLeft;
    }

    /**
     * @return Top right point
     */
    Point topRight() {
        return topRight;
    }

    /**
     * @return Bottom right point
     */
    Point bottomRight() {
        return bottomRight;
    }

    /**
     * @return Bottom left point
     */
    Point bottomLeft() {
        return bottomLeft;
    }
}
