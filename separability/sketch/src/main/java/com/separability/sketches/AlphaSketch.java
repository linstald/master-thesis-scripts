package com.separability.sketches;

import java.util.ArrayList;

import com.separability.geometry.Line;
import com.separability.geometry.PointSet;
import com.separability.geometry.PointSetFamily;
import com.separability.gui.PointSetFamilyGUI;

public class AlphaSketch extends PointSetFamilyGUI {

    protected ArrayList<Integer> currAlpha;
    ArrayList<PointSet> clfl;
    ArrayList<Line> ctlns;

    public void setup() {
        super.setup();
        currAlpha = null;
        clfl = null;
        ctlns = null;
    }

    public void incState() {
        super.incState();
        if (state == 1) {
            currAlpha = new ArrayList<Integer>();
            for (int i = 0; i < points.size(); i++) {
                currAlpha.add(0);
            }
        } else {
            for (int i = 0; i < currAlpha.size(); i++) {
                if (currAlpha.get(i) < points.get(i).size()) {
                    currAlpha.set(i, currAlpha.get(i) + 1);
                    break;
                } else {
                    currAlpha.set(i, 0);
                }
            }
        }
        points.setCutDefinition(PointSetFamily.COLOURFUL_LINES);
        boolean hAlpha = points.hasAlpha(currAlpha, lines);
        if (!hAlpha) {
            mode = 0;

            // if alpha corresponds to a tangent, then there is no separator for this alpha
            // implying that this point set is not 2 separable
            boolean isTangent = true;
            for (int i = 0; i < currAlpha.size(); i++) {
                isTangent = isTangent && (currAlpha.get(i) == 0
                        || currAlpha.get(i) == points.get(i).size());
            }
            if (isTangent) {
                println("not 2-separable");
            }

        }
        println(currAlpha, hAlpha);
    }

}
