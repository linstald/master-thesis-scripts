package com.separability.geometry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import com.separability.Utils;

import processing.core.PApplet;

public class PointSetFamily implements List<PointSet> {

    public static final int CUT_LINES = 0; // any two lines can be used in alpha cut
    public static final int CUT_LINES_TWO_COL = 1; // any two lines, where a line goes through different colours
    public static final int COLOURFUL_LINES = 2; // any two lines defined by exactly 1 point of each colour
    public static final int COLOURFUL_LINES_FIXED = 3; // any two lines defined by exactly 1 point of each colour, first
                                                       // line always through first two colours (using consistent
                                                       // counting)
    public static final int COLOURFUL_LINES_CONSISTENT = 4; // any two lines defined by exactly 1 point of each colour
                                                            // using consistent counting

    private ArrayList<PointSet> pointSets;
    private int cutDefinition = CUT_LINES;

    public PointSetFamily() {
        this.pointSets = new ArrayList<PointSet>();
    }

    public PointSetFamily(ArrayList<PointSet> pointSets) {
        this.pointSets = pointSets;
    }

    public PointSetFamily(PointSetFamily other) {
        this.pointSets = new ArrayList<PointSet>(other.pointSets);
    }

    public void setCutDefinition(int def) {
        this.cutDefinition = def;
    }

    public PointSetFamily copy() {
        PointSetFamily copied = new PointSetFamily();
        for (PointSet pntSet : pointSets) {
            copied.add(pntSet.copy());
        }
        return copied;
    }

    public boolean isGeneral() {
        PointSet allPoints = new PointSet();
        for (PointSet pntSet : pointSets) {
            allPoints.addAll(pntSet);
        }
        return allPoints.isGeneral();
    }

    public void makeGeneral() {
        while (!isGeneral()) {
            for (PointSet pntSet : pointSets) {
                pntSet.makeGeneral();
            }
        }
    }

    public ArrayList<Line> cutLinesTwoCol() {
        // returns a list of lines obtained by all tuples of different coloured points
        ArrayList<Line> ctlns = new ArrayList<Line>();

        for (PointSet pntSet : pointSets) {
            // collect all other points coming from different point sets
            PointSet allOther = new PointSet();
            for (PointSet otherSet : pointSets) {
                if (pntSet == otherSet) {
                    continue;
                }
                for (Point pnt : otherSet) {
                    allOther.add(pnt);
                }
            }

            for (Point p1 : pntSet) {
                for (Point p2 : allOther) {
                    ctlns.add(new Line(p1, p2));
                }
            }
        }
        return ctlns;
    }

    public ArrayList<Line> cutLines() {
        // returns a list of lines obtained by all tuples of points (possibly of the
        // same colour)
        ArrayList<Line> ctlns = new ArrayList<Line>();
        PointSet allPoints = new PointSet();
        for (PointSet pntSet : pointSets) {
            for (Point pnt : pntSet) {
                allPoints.add(pnt);
            }
        }
        for (Point p1 : allPoints) {
            for (Point p2 : allPoints) {
                if (p1 == p2) {
                    continue;
                }
                ctlns.add(new Line(p1, p2));
            }
        }
        return ctlns;
    }

    public ArrayList<PointSet> colourfulPointSets() {
        // returns all colourful point sets as an ArrayList
        ArrayList<PointSet> clfl = new ArrayList<PointSet>();
        if (pointSets.size() == 0) {
            return clfl;
        }
        int[] indices = new int[pointSets.size()]; // initialized with all 0
        while (true) {
            // indices now defines a colourful point set
            PointSet colful = new PointSet();
            for (int i = 0; i < indices.length; i++) {
                if (pointSets.get(i).size() == 0) {
                    continue;
                }
                colful.add(pointSets.get(i).get(indices[i]));
            }
            clfl.add(colful);

            // get next index set
            boolean broke = false;
            for (int i = 0; i < indices.length; i++) {
                if (indices[i] < pointSets.get(i).size() - 1) {
                    indices[i] = indices[i] + 1;
                    broke = true;
                    break;
                } else {
                    indices[i] = 0;
                }
            }
            // no more indices to increase, terminate while loop
            if (!broke) {
                break;
            }
        }

        return clfl;
    }

    public ArrayList<ArrayList<Line>> lineArrangements() {
        // returns all possible line arrangements under the current cut definition
        if (cutDefinition == CUT_LINES || cutDefinition == CUT_LINES_TWO_COL) {
            // if cut lines, we need to enumerate all cutLines, depending whether we need
            // two coloured or arbitrary cut lines
            // the line arrangements are then given by all possible tuples of the cut lines
            boolean twoColoured = cutDefinition == CUT_LINES_TWO_COL;
            // twoColoured is set, the lines are all defined by different coloured points
            ArrayList<Line> ctlns = twoColoured ? cutLinesTwoCol() : cutLines();

            ArrayList<ArrayList<Line>> lneArrangements = new ArrayList<ArrayList<Line>>();
            for (Line l1 : ctlns) {
                for (Line l2 : ctlns) {
                    ArrayList<Line> lnes = new ArrayList<Line>();
                    lnes.add(l1);
                    lnes.add(l2);
                    lneArrangements.add(lnes);
                }
            }
            return lneArrangements;
        }
        if (cutDefinition == COLOURFUL_LINES || cutDefinition == COLOURFUL_LINES_CONSISTENT
                || cutDefinition == COLOURFUL_LINES_FIXED) {
            // for colourful point sets, the line arrangements are defined by, well,
            // colourful point sets, if the colours are fixed only one arrangement per set
            // is added, otherwise all three possible arrangements per set
            ArrayList<PointSet> clfl = colourfulPointSets();
            boolean fixedColours = cutDefinition == COLOURFUL_LINES_FIXED;
            ArrayList<ArrayList<Line>> lneArrangements = new ArrayList<ArrayList<Line>>();
            for (PointSet ps : clfl) {
                if (ps.size() != 4) {
                    // colourful lines only defined for 4 colours, return empty list
                    return new ArrayList<ArrayList<Line>>();
                }
                Point p1 = ps.get(0);
                Point p2 = ps.get(1);
                Point p3 = ps.get(2);
                Point p4 = ps.get(3);
                ArrayList<Line> lnes = new ArrayList<Line>();

                // p1-p2, p3-p4
                lnes.add(new Line(p1, p2));
                lnes.add(new Line(p3, p4));
                lneArrangements.add(lnes);
                // fixedColours: the lines are always given by the first line through the first
                // two colours and the second line through the second two colours
                if (fixedColours) {
                    continue;
                }
                // p1-p3, p2-p4
                lnes = new ArrayList<Line>();
                lnes.add(new Line(p1, p3));
                lnes.add(new Line(p2, p4));
                lneArrangements.add(lnes);

                // p1-p4, p2-p3
                lnes = new ArrayList<Line>();
                lnes.add(new Line(p1, p4));
                lnes.add(new Line(p2, p3));
                lneArrangements.add(lnes);
            }
            return lneArrangements;
        }
        return new ArrayList<ArrayList<Line>>();
    }

    public boolean hasAlpha(ArrayList<Integer> alpha, ArrayList<Line> lines) {
        // returns true if the there is an alpha cut for given alpha vector
        // if lines is not null, will replace lines with the corresponding line
        // arrangement
        ArrayList<ArrayList<Line>> lneArrangements = lineArrangements();
        for (ArrayList<Line> lnes : lneArrangements) {
            if (matchesAlpha(lnes, alpha)) {
                if (lines != null) {
                    lines.clear();
                    lines.addAll(lnes);
                }
                return true;
            }

        }
        return false;
    }

    public ArrayList<Integer> getAlpha(boolean neg, ArrayList<Line> lines) {
        // returns the alpha vector for the given line arrangement and side to count
        // (neg = true: count negative side), neg is ignored if
        // cutDefinition == COLOURFUL_LINES_FIXED || cutDefinition ==
        // COLOURFUL_LINES_CONSISTENT
        ArrayList<Integer> alpha = new ArrayList<Integer>();

        if (cutDefinition == COLOURFUL_LINES_FIXED || cutDefinition == COLOURFUL_LINES_CONSISTENT) {

            // if cutDef is fixed colourful pointset, we have a consistent notion of + and -
            // we may assume that lines.get(0) passes through pointSets.get(0),
            // pointSets.get(1)
            // and lines.get(1) passes through points.get(2), points.get(3)
            // the consistent notion is given by the order the lines pass through

            // we can also get a consistent notion of +/- for colourful lines
            // both lines are going through exactly two colours, and every colour is
            // hit. So we can orient the lines to be directed in the direction such that
            // first the smaller colour and then the larger colour is traversed.
            // this matches the definition when we consider the lines to be in fixed
            // colours.

            Line l1 = lines.get(0);
            Line l2 = lines.get(1);

            // determine orientation of l1
            Point first = l1.getStartPoint();
            Point second = l1.getEndPoint();

            int firstIndex = 0;
            int secondIndex = 0;

            for (int i = 0; i < pointSets.size(); i++) {
                if (pointSets.get(i).contains(first)) {
                    firstIndex = i;
                }
                if (pointSets.get(i).contains(second)) {
                    secondIndex = i;
                }
            }
            boolean firstPos = firstIndex < secondIndex;
            // determine orientation of l2

            first = l2.getStartPoint();
            second = l2.getEndPoint();

            firstIndex = 0;
            secondIndex = 0;

            for (int i = 0; i < pointSets.size(); i++) {
                if (pointSets.get(i).contains(first)) {
                    firstIndex = i;
                }
                if (pointSets.get(i).contains(second)) {
                    secondIndex = i;
                }
            }
            boolean secondPos = firstIndex < secondIndex;
            if (firstPos && secondPos || !firstPos && !secondPos) {
                neg = false;
            } else {
                neg = true;
            }
        }

        for (PointSet pntSet : pointSets) {
            if (neg) {
                alpha.add(pntSet.countPointsAbove(lines));
            } else {
                alpha.add(pntSet.countPointsBelow(lines));
            }
        }
        return alpha;
    }

    public Set<ArrayList<Integer>> getAllAlpha() {
        // returns a set of all alpha vectors having an alpha cut under current cut
        // definition
        ArrayList<ArrayList<Line>> lneArrangements = lineArrangements();
        Set<ArrayList<Integer>> allAlpha = new HashSet<ArrayList<Integer>>();
        for (ArrayList<Line> lnes : lneArrangements) {
            allAlpha.add(getAlpha(false, lnes));
            allAlpha.add(getAlpha(true, lnes));
        }
        return allAlpha;
    }

    public Set<ArrayList<Integer>> getImpossibleAlpha() {
        // returns a set of all alpha vectors that do not have an alpha cut under
        // current cut definition, implemented by computing all alpha vectors from a
        // line arrangement (of current cut def) and generating all alpha vectors and
        // storing these that arent from a line arrangement, this way there may be some
        // vectors containing 0 that are listed as impossible (which is actually true)
        // but these can be viewed as having alpha cut if the vector replacing 0 with 1
        // has cut.
        Set<ArrayList<Integer>> imposs = new HashSet<ArrayList<Integer>>();
        Set<ArrayList<Integer>> allAlpha = getAllAlpha();

        ArrayList<Integer> currAlpha = new ArrayList<Integer>();
        for (int i = 0; i < pointSets.size(); i++) {
            currAlpha.add(0);
        }

        do {
            if (!allAlpha.contains(currAlpha)) {
                imposs.add(currAlpha);
            }
            // generate next alpha
            ArrayList<Integer> newAlpha = new ArrayList<Integer>(currAlpha);
            for (int i = 0; i < newAlpha.size(); i++) {
                // check if ith entry can be increased, if yes, do so otherwise set to 1
                if (newAlpha.get(i) < pointSets.get(i).size()) {
                    newAlpha.set(i, newAlpha.get(i) + 1);
                    break;
                } else {
                    newAlpha.set(i, 0);
                }
            }
            currAlpha = newAlpha;
        } while (Utils.sum(currAlpha) != 0);
        return imposs;
    }

    public boolean matchesAlpha(ArrayList<Line> lines, ArrayList<Integer> alpha) {
        // returns true if the given line arrangement is an alpha cut for the specified
        // alpha cut covers all cases depending on the current cut definition
        // a line arrangement matches the alpha if getAlpha returns the right alpha
        // or if for all zeros in alpha the corresponding point set only has points on
        // the line arrangement
        ArrayList<Integer> lineAlphaNeg = getAlpha(true, lines);
        ArrayList<Integer> lineAlphaPos = getAlpha(false, lines);
        // check if normal match
        if (alpha.equals(lineAlphaNeg) || alpha.equals(lineAlphaPos)) {
            return true;
        }
        // check if matches replacing zeros in alpha with number of points on
        // arrangement
        ArrayList<Integer> newAlpha = new ArrayList<Integer>(alpha);
        for (int i = 0; i < newAlpha.size(); i++) {
            if (newAlpha.get(i) != 0) {
                continue;
            }
            newAlpha.set(i, pointSets.get(i).countPointsOn(lines));
        }
        if (newAlpha.equals(lineAlphaNeg) || newAlpha.equals(lineAlphaPos)) {
            return true;
        }
        return false;
    }

    public void save(String filename) {
        if (this.size() == 0 || this.pointSets.get(0).size() == 0) {
            return;
        }
        PApplet sketch = this.pointSets.get(0).get(0).getSketch();
        PrintWriter writer = sketch.createWriter(filename);
        for (PointSet pntSet : pointSets) {
            writer.println("" + pntSet.size());
            for (Point pnt : pntSet) {
                writer.println("" + pnt.x + "," + pnt.y);
            }
        }
        PApplet.println("PointSet saved under " + filename);
        writer.close();
    }

    public void load(PApplet sketch, String filename) {
        BufferedReader reader = sketch.createReader(filename);
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                if (line.contains(",")) {
                    throw new IOException("Invalid format");
                }
                PointSet nextSet = new PointSet();
                int n = PApplet.parseInt(line);
                for (int i = 0; i < n; i++) {
                    line = reader.readLine();
                    if (line == null || !line.contains(",")) {
                        throw new IOException("Invalid format");
                    }
                    String[] splitted = line.split(",");
                    if (splitted.length != 2) {
                        throw new IOException("Invalid format");
                    }
                    float x = PApplet.parseFloat(splitted[0]);
                    float y = PApplet.parseFloat(splitted[1]);

                    nextSet.add(new Point(sketch, x, y));
                }
                pointSets.add(nextSet);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void add(int arg0, PointSet arg1) {
        this.pointSets.add(arg0, arg1);
    }

    @Override
    public boolean addAll(Collection<? extends PointSet> c) {
        return this.pointSets.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends PointSet> c) {
        return this.pointSets.addAll(index, c);
    }

    @Override
    public boolean contains(Object o) {
        return this.pointSets.contains(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return this.pointSets.containsAll(c);
    }

    @Override
    public int indexOf(Object o) {
        return this.pointSets.indexOf(o);
    }

    @Override
    public boolean isEmpty() {
        return this.pointSets.isEmpty();
    }

    @Override
    public Iterator<PointSet> iterator() {
        return this.pointSets.iterator();
    }

    @Override
    public int lastIndexOf(Object o) {
        return this.pointSets.lastIndexOf(o);
    }

    @Override
    public ListIterator<PointSet> listIterator() {
        return this.pointSets.listIterator();
    }

    @Override
    public ListIterator<PointSet> listIterator(int index) {
        return this.pointSets.listIterator(index);
    }

    @Override
    public boolean remove(Object o) {
        return this.pointSets.remove(o);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return this.pointSets.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return this.pointSets.retainAll(c);
    }

    @Override
    public List<PointSet> subList(int fromIndex, int toIndex) {
        return this.pointSets.subList(fromIndex, toIndex);
    }

    @Override
    public Object[] toArray() {
        return this.pointSets.toArray();
    }

    @Override
    public <T> T[] toArray(T[] arg0) {
        return this.pointSets.toArray(arg0);
    }

    @Override
    public boolean add(PointSet arg0) {
        return this.pointSets.add(arg0);
    }

    @Override
    public void clear() {
        this.pointSets.clear();
    }

    @Override
    public PointSet get(int index) {
        return this.pointSets.get(index);
    }

    @Override
    public PointSet remove(int index) {
        return this.pointSets.remove(index);
    }

    @Override
    public PointSet set(int arg0, PointSet arg1) {
        return this.pointSets.set(arg0, arg1);
    }

    @Override
    public int size() {
        return this.pointSets.size();
    }
}
