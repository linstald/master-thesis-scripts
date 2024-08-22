package com.separability;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.separability.geometry.Line;
import com.separability.geometry.Point;
import com.separability.geometry.PointSet;
import com.separability.geometry.PointSetFamily;

import processing.core.PApplet;
import processing.core.PVector;

public class Utils {
    public static float[] linesToShader(ArrayList<Line> lines) {
        float[] shaderLines = new float[2 * lines.size()];
        int index = 0;
        for (Line lne : lines) {
            PVector lneShader = lne.shaderFormat();
            shaderLines[index] = lneShader.x;
            shaderLines[index + 1] = lneShader.y;
            index += 2;
        }
        return shaderLines;
    }

    // returns true if s is in triangle (a,b,c)
    public static boolean inTriangle(PVector s, PVector a, PVector b, PVector c) {
        // adapted from: https://stackoverflow.com/a/9755252
        float as_x = s.x - a.x;
        float as_y = s.y - a.y;

        boolean s_ab = (b.x - a.x) * as_y - (b.y - a.y) * as_x > 0;

        if ((c.x - a.x) * as_y - (c.y - a.y) * as_x > 0 == s_ab)
            return false;
        if ((c.x - b.x) * (s.y - b.y) - (c.y - b.y) * (s.x - b.x) > 0 != s_ab)
            return false;
        return true;
    }

    public static boolean inTriangle(Point s, Point a, Point b, Point c) {
        return inTriangle(new PVector(s.x, s.y), new PVector(a.x, a.y), new PVector(b.x, b.y), new PVector(c.x, c.y));
    }

    // returns the intersection point of the line segments defined by (p0-p1) and
    // (p2-p3)
    // null if no intersection
    public static PVector segmentIntersection(PVector p0, PVector p1, PVector p2, PVector p3) {
        // adapted from: https://stackoverflow.com/a/1968345
        PVector s1, s2;
        s1 = new PVector(p1.x - p0.x, p1.y - p0.y);
        s2 = new PVector(p3.x - p2.x, p3.y - p2.y);

        float s, t;
        s = (-s1.y * (p0.x - p2.x) + s1.x * (p0.y - p2.y)) / (-s2.x * s1.y + s1.x * s2.y);
        t = (s2.x * (p0.y - p2.y) - s2.y * (p0.x - p2.x)) / (-s2.x * s1.y + s1.x * s2.y);

        if (s >= 0 && s <= 1 && t >= 0 && t <= 1) {
            // Collision detected
            return new PVector(p0.x + (t * s1.x), p0.y + (t * s1.y));
        }

        return null; // No collision
    }

    public static Point segmentIntersection(Point p0, Point p1, Point p2, Point p3) {
        PVector i = segmentIntersection(new PVector(p0.x, p0.y), new PVector(p1.x, p1.y), new PVector(p2.x, p2.y),
                new PVector(p3.x, p3.y));
        if (i == null) {
            return null;
        }
        return new Point(p0.getSketch(), i.x, i.y);
    }

    public static boolean isCaratheodory(PointSet all, PointSet sub) {
        PointSet comp = new PointSet(all);
        for (Point p : sub) {
            comp.remove(p);
        }
        if (sub.size() != 3) {
            PointSet temp = comp;
            comp = sub;
            sub = temp;
        }
        if (sub.size() != 3) {
            return false;
        }
        // check case (3,3,1)
        for (int i = 0; i < 3; i++) {
            Point last = sub.get(i);

            int firstIndex = (i + 1) % 3;
            int secondIndex = (firstIndex + 1) % 3;

            Point first = sub.get(firstIndex);
            Point second = sub.get(secondIndex);

            for (Point p1 : comp) {
                for (Point p2 : comp) {
                    if (p1 == p2) {
                        continue;
                    }
                    PointSet otherTwo = new PointSet(comp);
                    otherTwo.remove(p1);
                    otherTwo.remove(p2);
                    Point q1 = otherTwo.get(0);
                    Point q2 = otherTwo.get(1);

                    // Triangle 1: first, p1, p2
                    // Triangle 2: second, q1, q2

                    if (inTriangle(last, first, p1, p2) && inTriangle(last, second, q1, q2)) {
                        PApplet p = p1.getSketch();
                        p.noFill();
                        p.stroke(255, 0, 0);
                        p.strokeWeight(3);
                        p.circle(first.x, first.y, 30);
                        p.circle(p1.x, p1.y, 30);
                        p.circle(p2.x, p2.y, 30);

                        p.stroke(0, 255, 0);
                        p.circle(second.x, second.y, 30);
                        p.circle(q1.x, q1.y, 30);
                        p.circle(q2.x, q2.y, 30);

                        return true;
                    }
                }
            }
        }

        // check case (3,2,2)
        for (int i = 0; i < 3; i++) {
            Point last = sub.get(i);

            int firstIndex = (i + 1) % 3;
            int secondIndex = (firstIndex + 1) % 3;

            Point first = sub.get(firstIndex);
            Point second = sub.get(secondIndex);

            for (Point p1 : comp) {
                for (Point p2 : comp) {
                    if (p1 == p2) {
                        continue;
                    }
                    PointSet otherTwo = new PointSet(comp);
                    otherTwo.remove(p1);
                    otherTwo.remove(p2);
                    Point q1 = otherTwo.get(0);
                    Point q2 = otherTwo.get(1);

                    // Triangle: last, p1, p2
                    // segment1: first, q1
                    // segment2: second, q2

                    Point isctn = segmentIntersection(first, q1, second, q2);

                    if (isctn != null && inTriangle(isctn, last, p1, p2)) {
                        PApplet p = p1.getSketch();
                        p.noFill();
                        p.stroke(255, 0, 0);
                        p.strokeWeight(3);
                        p.circle(last.x, last.y, 30);
                        p.circle(p1.x, p1.y, 30);
                        p.circle(p2.x, p2.y, 30);

                        p.stroke(0, 255, 0);
                        p.circle(first.x, first.y, 30);
                        p.circle(q1.x, q1.y, 30);

                        p.stroke(255, 255, 0);
                        p.circle(second.x, second.y, 30);
                        p.circle(q2.x, q2.y, 30);

                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static Set<ArrayList<Integer>> computeThreeStabbers(PointSetFamily psf) {
        // Returns sorted triplets (ArrayList<Integer>) of indices
        Set<ArrayList<Integer>> stabbers = new HashSet<ArrayList<Integer>>();
        for (int i = 0; i < psf.size(); i++) {
            for (int j = i + 1; j < psf.size(); j++) {
                PointSet ps1 = psf.get(i);
                PointSet ps2 = psf.get(j);

                // build all lines going through a point in ps1 and one in ps2
                for (Point p1 : ps1) {
                    for (Point p2 : ps2) {
                        if (p1 == p2) {
                            continue; // should actually not happen, but does not hurt
                        }

                        Line possStabber = new Line(p1, p2);
                        // now check if a third pointset is intersected
                        for (int k = 0; k < psf.size(); k++) {
                            if (i == k || j == k) {
                                continue;
                            }
                            PointSet ps3 = psf.get(k);
                            if (ps3.countPointsBelow(possStabber) > 0 && ps3.countPointsAbove(possStabber) > 0) {
                                // actually a stabber, add new sorted list of indices
                                // i<j by for loops, lets see where k is
                                ArrayList<Integer> stabber = new ArrayList<Integer>();
                                if (k < i) {
                                    stabber.add(k);
                                    stabber.add(i);
                                    stabber.add(j);
                                }
                                if (i < k && k < j) {
                                    stabber.add(i);
                                    stabber.add(k);
                                    stabber.add(j);
                                }
                                if (j < k) {
                                    stabber.add(i);
                                    stabber.add(j);
                                    stabber.add(k);
                                }
                                stabbers.add(stabber);
                            }
                        }
                    }
                }
            }
        }
        return stabbers;
    }

    public static boolean isTverberg(PointSet all, PointSet sub) {
        PointSet comp = new PointSet(all);
        for (Point p : sub) {
            comp.remove(p);
        }
        if (sub.size() != 3) {
            PointSet temp = comp;
            comp = sub;
            sub = temp;
        }
        if (sub.size() != 3) {
            return false;
        }
        // check if (3,3,1) partition
        for (int i = 0; i < comp.size(); i++) {
            Point lonely = comp.get(i);

            Point p1 = comp.get((i + 1) % 4);
            Point p2 = comp.get((i + 2) % 4);
            Point p3 = comp.get((i + 3) % 4);

            // 1. triangle given by sub
            // 2. triangle given by p1,p2,p2
            // 3. lonely pointis lonely

            if (inTriangle(lonely, sub.get(0), sub.get(1), sub.get(2)) && inTriangle(lonely, p1, p2, p3)) {
                return true;
            }
        }
        // check (3,2,2) partition
        for (Point p1 : comp) {
            for (Point p2 : comp) {
                if (p1 == p2) {
                    continue;
                }
                PointSet otherTwo = new PointSet(comp);
                otherTwo.remove(p1);
                otherTwo.remove(p2);
                Point q1 = otherTwo.get(0);
                Point q2 = otherTwo.get(1);

                Point isctn = segmentIntersection(p1, p2, q1, q2);
                if (isctn != null && inTriangle(isctn, sub.get(0), sub.get(1), sub.get(2))) {
                    return true;
                }
            }
        }
        return false;
    }

    public static int getColor(float t, PApplet p) {
        if (0.0 <= t && t < 0.25) {
            // red to green
            return p.color(PApplet.map(t, 0, 0.25f, 255, 0), PApplet.map(t, 0, 0.25f, 0, 255), 0);
        }
        if (0.25 <= t && t < 0.5) {
            // green to blue
            return p.color(0, PApplet.map(t, 0.25f, 0.5f, 255, 0), PApplet.map(t, 0.25f, 0.5f, 0, 255));
        }
        if (0.5 <= t && t < 0.75) {
            // blue to purple
            return p.color(PApplet.map(t, 0.5f, 0.75f, 0, 255), 0, 255);
        }
        if (0.75 <= t && t < 1.0) {
            // purple to gray
            return p.color(PApplet.map(t, 0.75f, 1, 255, 128), PApplet.map(t, 0.75f, 1, 0, 128),
                    PApplet.map(t, 0.75f, 1, 255, 128));
        }
        return p.color(0);
    }

    public static int sum(ArrayList<Integer> lst) {
        int res = 0;
        for (int i : lst) {
            res += i;
        }
        return res;
    }

    public static String currName(PApplet sketch) {

        return "" + PApplet.year() + "-" + PApplet.nf(PApplet.month(), 2) + "-" + PApplet.nf(PApplet.day(), 2) + "-"
                + PApplet.nf(PApplet.hour(), 2) + "-" + PApplet.nf(PApplet.minute(), 2) + "-"
                + PApplet.nf(PApplet.second(), 2) + "-" + sketch.millis();
    }

    public static String currName() {

        return "" + PApplet.year() + "-" + PApplet.nf(PApplet.month(), 2) + "-" + PApplet.nf(PApplet.day(), 2) + "-"
                + PApplet.nf(PApplet.hour(), 2) + "-" + PApplet.nf(PApplet.minute(), 2) + "-"
                + PApplet.nf(PApplet.second(), 2);
    }

    public static boolean isZero(float x, float eps) {
        return Math.abs(x) < eps;
    }

    public static float det3x3(float[][] A) {
        if (A.length != 3) {
            return 0f;
        }
        if (A[0].length != 3) {
            return 0f;
        }
        float first = A[0][0] * (A[1][1] * A[2][2] - A[1][2] * A[2][1]);
        float second = A[0][1] * (A[1][0] * A[2][2] - A[1][2] * +A[2][0]);
        float third = A[0][2] * (A[1][0] * A[2][1] - A[1][1] * A[2][0]);
        return first - second + third;
    }

    public static boolean rightTurn(float x1, float x2, float y1, float y2, float z1, float z2, float eps) {
        float det = det3x3(new float[][] { { 1, x1, x2 }, { 1, y1, y2 }, { 1, z1, z2 } });
        if (isZero(det, eps)) {
            return false;
        }
        if (det > 0) {
            return true;
        }
        return false;
    }

    public static boolean leftTurn(float x1, float x2, float y1, float y2, float z1, float z2, float eps) {
        float det = det3x3(new float[][] { { 1, x1, x2 }, { 1, y1, y2 }, { 1, z1, z2 } });
        if (isZero(det, eps)) {
            return false;
        }
        if (det < 0) {
            return true;
        }
        return false;
    }
}
