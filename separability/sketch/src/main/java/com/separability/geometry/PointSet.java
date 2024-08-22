package com.separability.geometry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import processing.core.PVector;

/**
 * A class to handle point sets in the plane. Supports several queries that are
 * used in separability or alpha cutting checks.
 * Internally the point set is represented as an ArrayList of Point objects.
 * The class implements the List<Point> interface, so all important list methods
 * are exposed.
 */
public class PointSet implements List<Point> {

    private ArrayList<Point> points;

    public PointSet() {
        this.points = new ArrayList<Point>();
    }

    public PointSet(ArrayList<Point> points) {
        this.points = points;
    }

    public PointSet(PointSet other) {
        this.points = new ArrayList<Point>(other.points);
    }

    public PointSet copy() {
        PointSet copied = new PointSet();
        for (Point pnt : points) {
            copied.add(new Point(pnt));
        }
        return copied;
    }

    public float getEpsilon(ArrayList<Point> points) {
        // computes a distance epsilon such that moving a point by epsilon in any
        // direction
        // still preserves the lines i.e. when taking lines to all other points for any
        // such line
        // the relative position of each point is preserved before and after the move.
        // assumes that the distance from any point to any line (defined by two points)
        // is at least
        // a quarter of the minimum distance between any points (this is true for the
        // order type database).
        // first find min distance
        float w = 8000;
        if (points.size() > 0) {
            w = points.get(0).getSketch().width;
        }
        float minDist = w;
        for (Point p1 : points) {
            for (Point p2 : points) {
                if (p1 == p2) {
                    continue;
                }
                minDist = Math.min(minDist, p1.dist(p2));
            }
        }
        // then epsilon is minDist^2 / (8 width)
        return minDist * minDist / (8 * w);
    }

    public boolean isGeneral() {
        ArrayList<Line> allLines = new ArrayList<Line>();
        for (Point p1 : points) {
            for (Point p2 : points) {
                if (p1 == p2) {
                    continue;
                }
                allLines.add(new Line(p1, p2));
            }
        }
        // if there is a line with at least three points on it, we are not in general
        // position
        for (Line lne : allLines) {
            if (this.countPointsOn(lne) > 2) {
                return false;
            }
        }
        return true;
    }

    public void makeGeneral() {
        while (!isGeneral()) {
            for (Point pnt : points) {
                pnt.add(PVector.random2D());
            }
        }
    }

    public ArrayList<Line> getAllLines() {
        // returns all lines that can possibly be separating lines for separability
        // does so by taking all tuples of points and moving them an epsilon in the
        // positive
        // or negative side of the line (resulting in 4 lines per tuple of points)
        float eps = getEpsilon(points);
        ArrayList<Line> separatingLines = new ArrayList<Line>();
        for (int i = 0; i < points.size(); i++) {
            Point p1 = points.get(i);
            for (int j = i + 1; j < points.size(); j++) {
                Point p2 = points.get(j);
                // there are four possible lines:
                // p1+eps,p2+eps p1-eps,p2+eps p1+eps,p2-eps p1-eps,p2-eps
                // where +/- are perpendicular to the line thtough p1,p2

                // get direction and rotate it 90 degrees
                PVector dir = p1.dir(p2);
                PVector normal = dir.rotate((float) (Math.PI / 2.0));

                // calculate points
                Point p1n = new Point(p1);
                Point p1p = new Point(p1);
                Point p2n = new Point(p2);
                Point p2p = new Point(p2);
                p1n.add(PVector.mult(normal, -eps));
                p1p.add(PVector.mult(normal, eps));
                p2n.add(PVector.mult(normal, -eps));
                p2p.add(PVector.mult(normal, eps));

                // and lines
                Line l1 = new Line(p1n, p2n);
                Line l2 = new Line(p1p, p2n);
                Line l3 = new Line(p1n, p2p);
                Line l4 = new Line(p1p, p2p);

                separatingLines.add(l1);
                separatingLines.add(l2);
                separatingLines.add(l3);
                separatingLines.add(l4);
            }
        }
        return separatingLines;
    }

    public ArrayList<ArrayList<Line>> getTwoSeparators() {
        // returns all pairs of lines (that can be used for 2-separability)
        // does so by enumerating all tuples of separating lines (getAllLines()) and
        // creating
        // a new arraylist per tuple. Returns an array list of these array lists.
        ArrayList<Line> allLines = this.getAllLines();
        ArrayList<ArrayList<Line>> separators = new ArrayList<ArrayList<Line>>();
        for (int i = 0; i < allLines.size(); i++) {
            for (int j = i + 1; j < allLines.size(); j++) {
                ArrayList<Line> entry = new ArrayList<Line>();
                entry.add(allLines.get(i));
                entry.add(allLines.get(j));
                separators.add(entry);
            }
        }
        return separators;
    }

    public boolean separate(PointSet subset) {
        // returns true if the given subset can be separated by two lines
        // here a subset can be separated if the two lines separate the subset from its
        // complement
        // where the ground set is this PointSet object.
        for (ArrayList<Line> lines : getTwoSeparators()) {
            int selectedBelow = subset.countPointsBelow(lines);
            int totalBelow = this.countPointsBelow(lines);
            boolean valid = selectedBelow == subset.size() && totalBelow == selectedBelow;
            valid = valid || (selectedBelow == 0 && totalBelow == points.size() - subset.size());
            if (valid) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<PointSet> getNonTwoSeparable() {
        // returns an array list holding the non-2-separable subsets
        ArrayList<PointSet> nonSep = new ArrayList<PointSet>();
        ArrayList<PointSet> subsets = genSubsets();
        for (PointSet set : subsets) {
            if (!separate(set)) {
                nonSep.add(set);
            }
        }
        return nonSep;
    }

    public ArrayList<PointSet> genSubsets() {
        // generates all possible subsets of this PointSet object
        // does so only if there are not too many points (not more than 18)
        ArrayList<PointSet> subsets = new ArrayList<PointSet>();
        if (points.size() > 18) {
            return subsets; // emptyset if too large
        }
        subsets.add(new PointSet());
        for (Point pnt : points) {
            // to this timepoint subsets contains all subsets of points up to pnt (excluding
            // pnt)
            ArrayList<PointSet> newSubsets = new ArrayList<PointSet>();
            // to calculate all subsets up to pnt (including pnt), we just iterate over the
            // previous subsets
            // replace this subset by two new subsets one is equal to the previous, the
            // other additionally contains pnt
            for (PointSet set : subsets) {
                PointSet set1 = new PointSet(set);
                PointSet set2 = new PointSet(set);
                set2.add(pnt);
                newSubsets.add(set1);
                newSubsets.add(set2);
            }
            subsets = newSubsets;

        }
        return subsets;
    }

    public int countPointsBelow(ArrayList<Line> lines) {
        // counts the points from this PointSet object below a line arrangement in lines
        // a point is below a line arrangement if it is either on the line or below an
        // even number of lines
        int total = 0;
        for (Point pnt : points) {
            int belowCount = 0;
            boolean broke = false;
            for (Line lne : lines) {
                if (lne.isOn(pnt)) {
                    total += 1;
                    broke = true;
                    break;
                }
                if (lne.isBelow(pnt)) {
                    belowCount += 1;
                }
            }
            if (!broke && belowCount % 2 == 0) {
                total += 1;
            }
        }
        return total;
    }

    public int countPointsAbove(ArrayList<Line> lines) {
        // counts the points from this PointSet object above a line arrangement in lines
        // a point is below a line arrangement if it is on a line or below and even
        // number of lines
        int total = 0;
        for (Point pnt : points) {
            int belowCount = 0;
            boolean broke = false;
            for (Line lne : lines) {
                if (lne.isOn(pnt)) {
                    total += 1;
                    broke = true;
                    break;
                }
                if (lne.isBelow(pnt)) {
                    belowCount += 1;
                }
            }
            if (!broke && belowCount % 2 == 1) {
                total += 1;
            }
        }
        return total;
    }

    public int countPointsOn(ArrayList<Line> lines) {
        // counts the points from this PointSet object on a line arrangement in lines
        // a point is on a line arrangement if it is on a at least one line
        int total = 0;
        for (Point pnt : points) {
            for (Line lne : lines) {
                if (lne.isOn(pnt)) {
                    total += 1;
                    break;
                }
            }
        }
        return total;
    }

    public int countPointsBelow(Line line) {
        // counts the points from this PointSet object below a line
        // a point is below a line if it is either on the line or below
        int total = 0;
        for (Point pnt : points) {
            if (line.isBelow(pnt) || line.isOn(pnt)) {
                total += 1;
            }
        }
        return total;
    }

    public int countPointsAbove(Line line) {
        // counts the points from this PointSet object above a line
        // a point is below a line if it is on a line or above
        int total = 0;
        for (Point pnt : points) {
            if (line.isOn(pnt) || line.isAbove(pnt)) {
                total += 1;
            }
        }
        return total;
    }

    public int countPointsOn(Line line) {
        // counts the points from this PointSet object on a line
        int total = 0;
        for (Point pnt : points) {
            if (line.isOn(pnt)) {
                total += 1;
            }
        }
        return total;
    }

    @Override
    public boolean add(Point arg0) {
        return this.points.add(arg0);
    }

    @Override
    public void add(int arg0, Point arg1) {
        this.points.add(arg0, arg1);
    }

    @Override
    public boolean addAll(Collection<? extends Point> c) {
        return this.points.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends Point> c) {
        return this.points.addAll(index, c);
    }

    @Override
    public void clear() {
        this.points.clear();
    }

    @Override
    public boolean contains(Object o) {
        return this.points.contains(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return this.points.containsAll(c);
    }

    @Override
    public Point get(int index) {
        return this.points.get(index);
    }

    @Override
    public int indexOf(Object o) {
        return this.points.indexOf(o);
    }

    @Override
    public boolean isEmpty() {
        return this.points.isEmpty();
    }

    @Override
    public Iterator<Point> iterator() {
        return this.points.iterator();
    }

    @Override
    public int lastIndexOf(Object o) {
        return this.points.lastIndexOf(o);
    }

    @Override
    public ListIterator<Point> listIterator() {
        return this.points.listIterator();
    }

    @Override
    public ListIterator<Point> listIterator(int index) {
        return this.points.listIterator(index);
    }

    @Override
    public boolean remove(Object o) {
        return this.points.remove(o);
    }

    @Override
    public Point remove(int index) {
        return this.points.remove(index);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return this.points.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return this.points.retainAll(c);
    }

    @Override
    public Point set(int arg0, Point arg1) {
        return this.points.set(arg0, arg1);
    }

    @Override
    public int size() {
        return this.points.size();
    }

    @Override
    public List<Point> subList(int fromIndex, int toIndex) {
        return this.points.subList(fromIndex, toIndex);
    }

    @Override
    public Object[] toArray() {
        return this.points.toArray();
    }

    @Override
    public <T> T[] toArray(T[] arg0) {
        return this.points.toArray(arg0);
    }

}
