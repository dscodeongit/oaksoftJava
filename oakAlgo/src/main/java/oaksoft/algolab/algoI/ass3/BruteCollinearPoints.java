package oaksoft.algolab.algoI.ass3;

import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BruteCollinearPoints {
    private List<LineSegment> segments = new ArrayList<>();
    public BruteCollinearPoints(Point[] points) {    // finds all line segments containing 4 points

        if(!validate(points)) throw new IllegalArgumentException("Points cannot be null or repeated!");
        if(points.length >= 4) {
            for (int i = 0; i < points.length-3; i++) {
                for (int j = i+1; j < points.length-2; j++) {
                    for (int k = j+1; k < points.length-1; k++) {
                        for (int m = k+1; m < points.length; m++) {
                            if(points[i].slopeTo(points[j]) == points[i].slopeTo(points[k]) && points[i].slopeTo(points[j]) == points[i].slopeTo(points[m])) {
                                Point[] pts = {points[i], points[j], points[k], points[m]};
                                segments.add(new LineSegment(minPoint(pts), maxPoint(pts)));
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean validate(Point[] points) {
        if(points == null) return false;
        for(Point p : points) {
            if(p == null) return false;
        }
        Point[] sortedPoints = Arrays.copyOf(points, points.length);
        Arrays.sort(sortedPoints);
        for(int i = 1; i < sortedPoints.length; i++) {
            if(sortedPoints[i - 1].slopeTo(sortedPoints[i]) == Double.NEGATIVE_INFINITY) return false;
        }
        return true;
    }

    private boolean samePoint(Point a, Point b) {
        return a.compareTo(b) == 0;
    }

    private Point minPoint(Point[] points){
        Point minPoint = points[0];
        for(int i = 1; i < points.length; i++){
            if(points[i].compareTo(minPoint) < 0) {
                minPoint = points[i];
            }
        }
        return minPoint;
    }

    private Point maxPoint(Point[] points){
        Point maxPoint = points[0];
        for(int i = 1; i < points.length; i++){
            if(points[i].compareTo(maxPoint) > 0) {
                maxPoint = points[i];
            }
        }
        return maxPoint;
    }

    public int numberOfSegments() {        // the number of line segments
        return segments.size();
    }
    public LineSegment[] segments() {               // the line segments
        LineSegment[] segArr = new LineSegment[segments.size()];
        return segments.toArray(segArr);
    }

    public static void main(String[] args) {
        Point p1 = new Point(1, 1);
        Point p2 = new Point(2, 2);
        Point p3 = new Point(4, 4);
        Point p4 = new Point(6, 6);
        Point p5 = new Point(3, 9);
        Point p6 = new Point(8, 9);

        Point[] points = {p1, p2, p3, p4, p5, p6};
        BruteCollinearPoints bcp = new BruteCollinearPoints(points);

        StdOut.println(bcp.numberOfSegments());

        StdOut.println(Arrays.toString(bcp.segments()));

    }
}
