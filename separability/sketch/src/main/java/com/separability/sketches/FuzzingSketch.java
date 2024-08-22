package com.separability.sketches;

import java.util.ArrayList;
import java.util.Set;

import com.separability.Utils;
import com.separability.geometry.Line;
import com.separability.geometry.PointSet;
import com.separability.geometry.PointSetFamily;
import com.separability.geometry.PointSetFuzzer;
import com.separability.gui.PointSetFamilyGUI;

public class FuzzingSketch extends PointSetFamilyGUI {
    ArrayList<PointSetFuzzer> psf;

    ArrayList<Integer> currAlpha;
    ArrayList<PointSet> clfl;
    ArrayList<Line> ctlns;

    ArrayList<ArrayList<Line>> lneArrangements;

    public void setup() {
        super.setup();

        currAlpha = null;
        clfl = null;
        ctlns = null;

        lneArrangements = null;

        psf = null;
    }

    public void incState() {
        // if (lneArrangements != null && lneArrangements.size() > 0) {
        // lines = lneArrangements.remove(0);
        // println(points.getAlpha(false, lines), points.getAlpha(true, lines));
        // return;
        // }
        super.incState();
        if (state == 1) {
            psf = new ArrayList<PointSetFuzzer>();
            for (PointSet pntSet : points) {
                psf.add(new PointSetFuzzer(pntSet));
            }
        } else {

            for (int i = 0; i < points.size(); i++) {
                points.set(i, psf.get(i).nextPointSet());
            }
            points.setCutDefinition(PointSetFamily.COLOURFUL_LINES);
        }

        Set<ArrayList<Integer>> impossAlpha = points.getImpossibleAlpha();
        if (impossAlpha.size() > 0) {
            println(impossAlpha);
            String name = "notalpha";
            println("not alpha splittable");
            // conjecture: 2-separability + at most 1 3-stabber => all alpha cuts
            // hence if not all alpha cuts, but 2-separable, we should find more than 1
            // 3-stabber
            for (ArrayList<Integer> alpha : impossAlpha) {
                // if there is an alpha that does correspond to a tangent, the point set is not
                // 2-separable
                boolean isTangent = true;
                for (int i = 0; i < alpha.size(); i++) {
                    isTangent = isTangent && (alpha.get(i) == 0 || alpha.get(i) == points.get(i).size());
                }
                if (isTangent) {
                    println("not 2-separable");
                    name += "notsep";
                    break;
                }
            }
            int numStabbers = Utils.computeThreeStabbers(points).size();
            name += "" + Utils.currName(this);
            name += "_" + numStabbers;
            points.save("points/" + name + ".txt");
        }

    }
}