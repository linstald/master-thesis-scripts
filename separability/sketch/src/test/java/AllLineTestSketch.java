import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import com.separability.geometry.Line;
import com.separability.geometry.PointSetFamily;
import com.separability.gui.PointSetFamilyGUI;

import processing.core.PApplet;

public class AllLineTestSketch extends PointSetFamilyGUI {

    List<String> pointsFiles;
    int fileIndex;
    CountDownLatch cdl;
    ArrayList<Integer> currAlpha;
    ArrayList<ArrayList<Line>> lneArrangements;
    int cutDefinition;
    int currArrangementIndex = 0;
    boolean hAlpha = false;

    int framesUntilFast = 10;

    public AllLineTestSketch(List<String> filenames, int cutDef, CountDownLatch cdl) {
        this.pointsFiles = filenames;
        this.cdl = cdl;
        this.cutDefinition = cutDef;
    }

    public void setup() {
        super.setup();
        this.points = new PointSetFamily();
        if (pointsFiles.size() > 0) {
            fileIndex = 0;
            this.points.load(this, this.pointsFiles.get(fileIndex));
            this.points.setCutDefinition(this.cutDefinition);
            this.points.makeGeneral();
            Set<ArrayList<Integer>> imposs = points.getImpossibleAlpha();
            println("impossible: " + imposs.size());
            imposs.forEach((x) -> PApplet.print(x + ", "));
            println();
            lneArrangements = this.points.lineArrangements();
        } else {
            cdl.countDown();
        }
    }

    public void incState() {
        super.incState();
        if (state == 1) {
            currAlpha = new ArrayList<Integer>();
            for (int i = 0; i < points.size(); i++) {
                currAlpha.add(0);
            }
            println(currAlpha);
        } else {
            do {
                if (currArrangementIndex >= lneArrangements.size()) {
                    if (!hAlpha) {
                        mode = 0;

                        println("Has not alpha: " + currAlpha);
                        // if alpha corresponds to a tangent, then there is no separator for this alpha
                        // implying that this point set is not 2 separable
                        boolean isTangent = true;
                        for (int i = 0; i < currAlpha.size(); i++) {
                            isTangent = isTangent && (currAlpha.get(i) == 0
                                    || currAlpha.get(i) == points.get(i).size());
                        }
                        if (isTangent) {
                            println("not 2-separable.");
                        }

                    }

                    for (int i = 0; i < currAlpha.size(); i++) {
                        if (currAlpha.get(i) < points.get(i).size()) {
                            currAlpha.set(i, currAlpha.get(i) + 1);
                            break;
                        } else {
                            currAlpha.set(i, 0);
                        }
                    }

                    println(currAlpha);
                    currArrangementIndex = 0;
                    hAlpha = false;

                } else {
                    lines = lneArrangements.get(currArrangementIndex);
                    ArrayList<Integer> pos = points.getAlpha(false, lines);
                    ArrayList<Integer> neg = points.getAlpha(true, lines);
                    for (int i = 0; i < pos.size(); i++) {
                        assert (pos.get(i) + neg.get(i) - 1 == points.get(i).size());
                    }
                    if (mode == 0) {
                        println(pos, neg);
                    }
                    if (points.matchesAlpha(lines, currAlpha)) {
                        println("Has alpha: " + currAlpha);
                        hAlpha = true;
                        mode = 0;
                        currArrangementIndex = lneArrangements.size();
                    }
                    currArrangementIndex += 1;

                }
            } while (mode == 1);
        }
    }

    public void draw() {
        super.draw();
        textSize(20);
        fill(0);
        noStroke();
        text(fileIndex + " " + pointsFiles.get(fileIndex), 50, 50);
        if (keyPressed) {
            if (framesUntilFast > 0) {
                framesUntilFast--;
            } else {
                keyPressed();
            }
        }
    }

    public void keyReleased() {
        framesUntilFast = 10;
    }

    public void keyPressed() {
        super.keyPressed();
        if (key == ESC) {
            println("exiting");
            cdl.countDown();
        }
        if (key == 'f' || key == 'v') {
            fileIndex = (fileIndex + pointsFiles.size() + (key == 'f' ? 1 : -1)) % pointsFiles.size();
            points = new PointSetFamily();
            points.load(this, pointsFiles.get(fileIndex));
            points.setCutDefinition(this.cutDefinition);
            points.makeGeneral();
            println();
            println("----------------------");

            Set<ArrayList<Integer>> imposs = points.getImpossibleAlpha().stream().filter((x) -> !x.contains(0))
                    .collect(Collectors.toSet());
            println("impossible: " + imposs.size());
            imposs.forEach((x) -> PApplet.print(x + ", "));
            println();
            lneArrangements = points.lineArrangements();
            currArrangementIndex = 0;
            currAlpha = new ArrayList<Integer>();
            for (int i = 0; i < points.size(); i++) {
                currAlpha.add(0);
            }
            lines.clear();
        }
    }

}
