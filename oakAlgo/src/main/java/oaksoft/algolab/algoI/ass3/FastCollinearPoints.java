package oaksoft.algolab.algoI.ass3;

import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class FastCollinearPoints {
    private List<LineSegment> lineSegments = new ArrayList<>();
    public FastCollinearPoints(Point[] points) {    // finds all line lineSegments containing 4 points
        if(points == null) throw new IllegalArgumentException("Points cannot be null or repeated!");
        Point[] sortedPoints = Arrays.copyOf(points, points.length);
        for(Point p : sortedPoints){
            if(p == null) throw new IllegalArgumentException("Point cannot be null");
        }
        Arrays.sort(sortedPoints);
        if(!validate(sortedPoints)) throw new IllegalArgumentException("Points cannot be null or repeated!");

        if(sortedPoints.length >= 4) {
            for (int i = 0; i < sortedPoints.length; i++) {

                Point[] others = new Point[sortedPoints.length-1];
                for(int j = 0; j < i; j++) {
                    others[j] = sortedPoints[j];
                }
                for(int j = i+1; j < sortedPoints.length; j++) {
                    others[j-1] = sortedPoints[j];
                }
                Arrays.sort(others, sortedPoints[i].slopeOrder());
                double slope = Double.NEGATIVE_INFINITY;
                int sind = 0;
                for(int j = 0; j < others.length; j++) {
                    double aSlope = sortedPoints[i].slopeTo(others[j]);
                    if(aSlope != slope){
                        if(j - sind >= 3 ) {
                            Point[] pts = new Point[j-sind+1];
                            pts[0] = sortedPoints[i];

                            for (int k = 0; k < pts.length - 1; k++){
                                pts[k+1] = others[sind+k];
                            }
                            Point minPoint = minPoint(pts);
                            if(samePoint(sortedPoints[i], minPoint)) {
                                lineSegments.add(new LineSegment(minPoint, maxPoint(pts)));
                            }
                        }
                        slope = aSlope;
                        sind = j;
                    } else if(j == others.length-1){
                        if(j - sind >= 2 ) {
                            Point[] pts = new Point[j-sind+2];
                            pts[0] = sortedPoints[i];

                            for (int k = 0; k < pts.length - 1; k++){
                                pts[k+1] = others[sind+k];
                            }
                            Point minPoint = minPoint(pts);
                            if(samePoint(sortedPoints[i], minPoint)) {
                                lineSegments.add(new LineSegment(minPoint, maxPoint(pts)));
                            }
                        }
                    }

                }
            }
        }
    }

    private boolean validate(Point[] sortedPoints) {
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

    public int numberOfSegments() {        // the number of line lineSegments
        return lineSegments.size();
    }
    public LineSegment[] segments() {               // the line lineSegments
        return lineSegments.toArray(new LineSegment[lineSegments.size()]);
    }

    public static void main(String[] args) {
        Point p1 = new Point(12102, 4237);
        Point p2 = new Point(12102, 13860);
        Point p3 = new Point(12102, 14674);
        Point p4 = new Point(12102, 18921);
        Point p5 = new Point(3, 3);
        Point p6 = new Point(8, 9);
        Point p7 = new Point(1, 2);
        Point p8 = new Point(2, 4);
        Point p9 = new Point(3, 6);
        Point p10 = new Point(4, 8);
        Point p11 = new Point(1, 3);
        Point p12 = new Point(2, 7);
        Point[] points = {p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12};
        FastCollinearPoints fcp = new FastCollinearPoints(points);

        StdOut.println(fcp.numberOfSegments());

        StdOut.println(Arrays.toString(fcp.segments()));

    }
}
