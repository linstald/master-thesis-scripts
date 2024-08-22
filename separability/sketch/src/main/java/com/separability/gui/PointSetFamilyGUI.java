package com.separability.gui;

import com.separability.Utils;
import com.separability.geometry.Point;
import com.separability.geometry.PointSet;
import com.separability.geometry.PointSetFamily;

public class PointSetFamilyGUI extends BelowLineGUI {
    public PointSetFamily points;

    final int startColor = color(255, 0, 0);
    final int midColor = color(0, 255, 0);
    final int endColor = color(0, 0, 255);

    private float pointR;

    public PointSetFamilyGUI() {
        this.pointR = 10f;
    }

    public PointSetFamilyGUI(float r) {
        this.pointR = r;
    }

    public void setup() {
        super.setup();
        points = new PointSetFamily();
        points.add(new PointSet());
    }

    public void draw() {
        super.draw();
        if (points == null) {
            return;
        }
        int psize = points.size();
        for (int i = 0; i < psize; i++) {
            PointSet pntSet = points.get(i);
            int c = Utils.getColor(i / (float) psize, this);
            for (Point pnt : pntSet) {
                pnt.setColor(c);
                pnt.setSize(pointR);
                pnt.show();
            }
        }
    }

    public void mouseReleased() {
        super.mouseReleased();
        if (state == 0) {
            Point pnt = new Point(this, mouseX, mouseY);
            if (points.size() == 0) {
                points.add(new PointSet());
            }
            points.get(points.size() - 1).add(pnt);
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
                PointSet curr = points.get(points.size() - 1);
                if (curr.size() > 0) {
                    curr.remove(curr.size() - 1);
                }
                if (curr.size() == 0) {
                    points.remove(points.size() - 1);
                }
            }
        }
        if (key == 'n' && state == 0) {
            points.add(new PointSet());
        }
        if (key == 'a') {
            println(points.getAlpha(true, lines), points.getAlpha(false, lines));
        }
        if (key == 's') {
            String currName = Utils.currName(this);
            if (points != null) {
                points.save("points/pointSet_" + currName + ".txt");
            }
        }
    }
}
