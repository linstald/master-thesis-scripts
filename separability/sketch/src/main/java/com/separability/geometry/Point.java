package com.separability.geometry;

import java.lang.Math;
import processing.core.PApplet;
import processing.core.PVector;

/**
 * A class to handle points in the plane. Supports queries for other points for
 * distance and direction vector.
 * Due to technical reasons, each point needs a PApplet object which serves as
 * the sketch it lives in.
 * While it is not necessary to show the point, the sketch will serve as a
 * canvas to draw the point with the show function.
 * To do this, a size and a color value can be passed directly to this point
 * object (default 1/black).
 */
public class Point {
    public float x, y;

    private float r;
    private int c;

    private PApplet sketch;

    public Point(PApplet sketch, float x, float y) {
        this.sketch = sketch;
        this.x = x;
        this.y = y;
        c = sketch.color(0, 0, 0);
        r = 1;
    }

    public Point(Point pnt) {
        this.x = pnt.x;
        this.y = pnt.y;
        this.sketch = pnt.sketch;
    }

    public void setSize(float r) {
        this.r = r;
    }

    public float getSize() {
        return this.r;
    }

    public void setColor(int c) {
        this.c = c;
    }

    public int getColor() {
        return this.c;
    }

    public void show() {
        sketch.fill(this.c);
        sketch.noStroke();
        sketch.circle(x, y, r);
        // sketch.noStroke();
        // sketch.fill(0);
        // sketch.textSize(20);
        // sketch.text(this.toString(), x, y - 20);
    }

    public void add(PVector offset) {
        // adds the offset vector to the coordinates of the point
        this.x += offset.x;
        this.y += offset.y;
    }

    public float dist(Point other) {
        // returns the distance (euclidean norm) to the other point
        float dx = this.x - other.x;
        float dy = this.y - other.y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    public PVector dir(Point other) {
        // returns a normalized direction vector pointing in the direction from this
        // point to the other point
        return new PVector(other.x - this.x, other.y - this.y).normalize();
    }

    public PApplet getSketch() {
        return sketch;
    }
}
