package com.separability.geometry;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import processing.core.PApplet;

public class PointSetReader implements PointSetSupplier {
    private InputStream file;
    private int totalPointSets;

    private int n;
    private PApplet sketch;

    public PointSetReader(PApplet sketch, String filename) throws IllegalArgumentException {
        if (!filename.startsWith("otypes")) {
            throw new IllegalArgumentException("filename must start with 'otypes'");
        }
        String[] splitted = PApplet.split(filename, ".");
        if (splitted.length != 2) {
            throw new IllegalArgumentException("filename must contain '.'");
        }

        if (splitted[0].length() != 8) {
            throw new IllegalArgumentException("filename must have format 'otypesdd.bxx'");
        }
        this.sketch = sketch;

        n = Integer.valueOf(splitted[0].substring(6));
        file = sketch.createInput(filename);
        totalPointSets = 0;
    }

    public int getTotalPointSets() {
        return totalPointSets;
    }

    public PointSet nextPointSet() {
        byte[] pointArray = new byte[2 * n];
        ArrayList<Point> pointSet = new ArrayList<Point>();
        try {
            int rd = file.read(pointArray);
            if (rd == -1) {
                return null;
            }
        } catch (IOException e) {
            return null;
        }
        for (int i = 0; i < pointArray.length; i += 2) {
            int bx = (int) pointArray[i];
            int by = (int) pointArray[i + 1];

            // because java is such a nice language not to provide unsigned types
            // we have to do such a hacky thing to convert to unsigned values
            if (bx < 0) {
                bx = 128 + Math.abs(-128 - bx);
            }
            if (by < 0) {
                by = 128 + Math.abs(-128 - by);
            }
            float x = PApplet.map(bx, 0, 255, (float) 0.1 * sketch.width, (float) 0.9 * sketch.width);
            float y = PApplet.map(by, 0, 255, (float) 0.1 * sketch.height, (float) 0.9 * sketch.height);
            pointSet.add(new Point(sketch, x, y));
        }
        totalPointSets += 1;

        return new PointSet(pointSet);
    }
}
