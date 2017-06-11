package com.fooock.ticket.opencv;

import org.opencv.core.Point;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Sort a point array. The first entry will be top-left, the second top-right, the third
 * bottom-right and the fourth will be the bottom-left.
 * <p>
 * The top-left point will have the smallest sum, whereas the bottom-right point will
 * have the largest sum.
 * <p>
 * To calculate the top-right and the bottom-left we need to compute the difference
 * between the points. The top-right will have the smallest difference, whereas the
 * bottom-left will have the largest difference.
 */
final class SortPointArray {

    private final Point[] mPoints;

    SortPointArray(Point[] points) {
        mPoints = points;
    }

    /**
     * This comparator is used to calculate the top-left and bottom-right points
     */
    private final Comparator<Point> sum = new Comparator<Point>() {
        @Override
        public int compare(Point o1, Point o2) {
            final double p1 = o1.x + o1.y;
            final double p2 = o2.x + o2.y;
            return Double.valueOf(p1).compareTo(p2);
        }
    };

    /**
     * This comparator is used to calculate the top-right and the bottom-left points
     */
    private final Comparator<Point> diff = new Comparator<Point>() {
        @Override
        public int compare(Point o1, Point o2) {
            final double p1 = o1.y - o1.x;
            final double p2 = o2.y - o2.x;
            return Double.valueOf(p1).compareTo(p2);
        }
    };

    Point[] sort() {
        final Point[] result = new Point[mPoints.length];
        final List<Point> pointList = Arrays.asList(mPoints);

        // top-left corner
        result[0] = Collections.min(pointList, sum);
        // top-right
        result[1] = Collections.min(pointList, diff);
        // bottom-right
        result[2] = Collections.max(pointList, sum);
        // bottom-left
        result[3] = Collections.max(pointList, diff);

        return result;
    }
}
