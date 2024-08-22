package com.separability.gui;

import com.separability.geometry.Point;
import com.separability.geometry.PointSet;

public class PointSetGUI extends BelowLineGUI {

    public PointSet points;

    private int defaultColor;
    private float pointR;

    public PointSetGUI(int defaultColor, float r) {
        this.defaultColor = defaultColor;
        this.pointR = r;
    }

    public PointSetGUI(int defaultColor) {
        this.defaultColor = defaultColor;
        this.pointR = 10f;
    }

    public PointSetGUI(float r) {
        this.defaultColor = color(180, 20, 20);
        this.pointR = r;
    }

    public PointSetGUI() {
        this.defaultColor = color(180, 20, 20);
        this.pointR = 10f;
    }

    public void setup() {
        points = new PointSet();
        super.setup();
    }

    public void draw() {
        super.draw();
        if (points == null) {
            return;
        }
        for (Point pnt : points) {
            pnt.setColor(defaultColor);
            pnt.setSize(pointR);
            pnt.show();
        }
    }

    public void mouseReleased() {
        super.mouseReleased();
        if (state == 0) {
            Point pnt = new Point(this, mouseX, mouseY);
            pnt.setSize(pointR);
            pnt.setColor(defaultColor);
            points.add(pnt);
        }
    }

    public void keyPressed() {
        super.keyPressed();
        if (key == 'c') {
            if (state == 0) {
                points.clear();
            }
        }
        if (key == BACKSPACE) {
            if (state == 0 && points.size() > 0) {
                points.remove(points.size() - 1);
            }
        }
    }
}
