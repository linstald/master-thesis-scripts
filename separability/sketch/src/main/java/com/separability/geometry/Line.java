package com.separability.geometry;

import com.separability.Utils;

import processing.core.PApplet;
import processing.core.PVector;

/**
 * A class to handle lines in the plane. Supports queries for points to lie
 * on,below or above the line.
 * Due to technical reasons, each line needs a PApplet object which serves as
 * the sketch it lives in.
 * While it is not necessary to show the line, the sketch will serve as a canvas
 * to draw the line with the show function.
 * Moreover, the line exportable to shader format.
 */
public class Line {
    private boolean degen = false;

    // start and end points
    private Point s, e;

    private PApplet sketch;

    private static final float EPS = 0.1f;

    public Line(PApplet sketch, float sx, float sy, float ex, float ey) {
        this.sketch = sketch;
        this.s = new Point(sketch, sx, sy);
        this.e = new Point(sketch, ex, ey);
        // start point should have smaller x coordinate
        if (sx > ex) {
            Point tmp = this.s;
            this.s = this.e;
            this.e = tmp;
        }
    }

    public Line(Point s, Point e) {
        this.s = s;
        this.e = e;
        if (s.x > e.x) {
            this.s = e;
            this.e = s;
        }
        this.sketch = s.getSketch();
    }

    public boolean isDegen() {
        return degen;
    }

    public PApplet getSketch() {
        return sketch;
    }

    public Point getStartPoint() {
        return this.s;
    }

    public Point getEndPoint() {
        return this.e;
    }

    public boolean isBelow(float x, float y) {
        // returns true if (x,y) is (strictly) below the line (visually)
        // since y coordinates increase in downwards directions
        // in the sketch, this means the point is above the line meaning y > mx+q
        return Utils.rightTurn(s.x, s.y, e.x, e.y, x, y, EPS);
    }

    public boolean isAbove(float x, float y) {
        // similar to isBelow
        return Utils.leftTurn(s.x, s.y, e.x, e.y, x, y, EPS);
    }

    public boolean isBelow(Point p) {
        return isBelow(p.x, p.y);
    }

    public boolean isAbove(Point p) {
        return isAbove(p.x, p.y);
    }

    public boolean isOn(float x, float y) {
        // returns true if the point is on the line
        // here, on the line means that it is neither above or below
        return !isBelow(x, y) && !isAbove(x, y);
    }

    public boolean isOn(Point p) {
        return isOn(p.x, p.y);
    }

    public PVector shaderFormat() {
        // returns a vector holding slope and y coordinate offset for a line living in
        // shader coordinates
        // shader coordinates have y coordinates increasing as in the regular coordinate
        // system
        // so we need to invert the slope and y coordinate offset
        float m = Math.abs(e.x - s.x) > 0f ? (e.y - s.y) / (e.x - s.x) : 1e9f;
        float q = s.y - m * s.x;
        return new PVector(-m, sketch.height - q);
    }

    public void show() {
        sketch.strokeWeight(2);
        sketch.stroke(0);
        float m = Math.abs(e.x - s.x) > 0f ? (e.y - s.y) / (e.x - s.x) : 1e9f;
        float q = s.y - m * s.x;
        if (Math.abs(m) > 30f) {
            return;
        }
        sketch.line(0, q, sketch.width, m * sketch.width + q);
    }
}
