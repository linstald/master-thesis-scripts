package com.separability.sketches;

import java.util.ArrayList;

import com.separability.Utils;
import com.separability.geometry.Point;
import com.separability.geometry.PointSet;
import com.separability.geometry.PointSetReader;
import com.separability.gui.PointSetGUI;

public class SepSketch extends PointSetGUI {

    int defaultColor = color(180, 20, 20);
    int selectedColor = color(20, 180, 20);

    PointSetReader psr;

    ArrayList<PointSet> nonSeparable;
    PointSet currSubset;
    int twoSeparable = 0;

    public void setup() {
        super.setup();
        psr = new PointSetReader(this, "otypes07.b08");

        points = psr.nextPointSet();
        currSubset = new PointSet();
        nonSeparable = new ArrayList<PointSet>();

        if (mode == 1) {
            incState();
        }
    }

    public void draw() {
        super.draw();

        for (Point pnt : currSubset) {
            pnt.setColor(selectedColor);
            pnt.show();
        }

        if (!Utils.isCaratheodory(points, currSubset) && !Utils.isTverberg(points, currSubset)) {
            mode = 0;
        }

        fill(0);
        noStroke();
        textSize(20);
        text(psr.getTotalPointSets() + "|" + state + "/" + nonSeparable.size(), width - 100, 40);

    }

    public void incState() {
        super.incState();
        if (state == 1) {
            nonSeparable = points.getNonTwoSeparable();
        }
        while (state > nonSeparable.size()) {
            if (nonSeparable.size() == 0) {
                twoSeparable += 1;
            }
            points = psr.nextPointSet();
            if (points == null) {
                println("no more point sets");
                println(twoSeparable + "/" + psr.getTotalPointSets() + " are 2 separable");
                noLoop();
                return;
            }
            state = 1;
            nonSeparable = points.getNonTwoSeparable();
        }
        currSubset = nonSeparable.get(state - 1);
        lines.clear();
    }
}
