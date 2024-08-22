import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import com.separability.geometry.Line;
import com.separability.geometry.Point;
import com.separability.geometry.PointSet;
import com.separability.geometry.PointSetFamily;
import com.separability.gui.PointSetFamilyGUI;

import processing.core.PApplet;

public class PointSetFamilyEditorTestSketch extends PointSetFamilyGUI {

    List<String> pointsFiles;
    int fileIndex;
    CountDownLatch cdl;
    ArrayList<Integer> currAlpha;
    ArrayList<ArrayList<Line>> lneArrangements;
    int currArrangementIndex = 0;
    boolean hAlpha = false;

    int framesUntilFast = 10;

    public PointSetFamilyEditorTestSketch(List<String> filenames, CountDownLatch cdl) {
        this.pointsFiles = filenames;
        this.cdl = cdl;
    }

    private void printImposs() {
        points.setCutDefinition(PointSetFamily.COLOURFUL_LINES);
        points.makeGeneral();
        Set<ArrayList<Integer>> imposs = points.getImpossibleAlpha().stream().filter((x) -> !x.contains(0))
                .collect(Collectors.toSet());
        println("impossible: " + imposs.size());
        imposs.forEach((x) -> PApplet.print(x + ", "));
        println();
    }

    public void setup() {
        super.setup();
        this.points = new PointSetFamily();
        if (pointsFiles.size() > 0) {
            fileIndex = 0;
            this.points.load(this, this.pointsFiles.get(fileIndex));
            printImposs();
            lneArrangements = this.points.lineArrangements();
        } else {
            cdl.countDown();
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
        if (key == ENTER || key == ' ') {
            printImposs();
            return;
        }
        super.keyPressed();
        if (key == ESC) {
            println("exiting");
            cdl.countDown();
        }
        if (key == 'f' || key == 'v') {
            fileIndex = (fileIndex + pointsFiles.size() + (key == 'f' ? 1 : -1)) % pointsFiles.size();
            points = new PointSetFamily();
            points.load(this, pointsFiles.get(fileIndex));
            printImposs();
            println("----------------------");
            lines.clear();
        }
    }

    public void mousePressed() {
        if (mouseButton == RIGHT) {
            Point pnt = new Point(this, mouseX, mouseY);
            int index = 0;
            if (keyPressed) {
                if (key == '1') {
                    index = 0;
                }
                if (key == '2') {
                    index = 1;
                }
                if (key == '3') {
                    index = 2;
                }
                if (key == '4') {
                    index = 3;
                }
            }
            Point settings = points.get(index).get(0);
            pnt.setColor(settings.getColor());
            pnt.setSize(settings.getSize());
            points.get(index).add(pnt);
        } else {
            mouseDragged();
        }
    }

    public void mouseReleased() {
        return;
    }

    public void mouseDragged() {
        super.mouseDragged();

        for (PointSet pntSet : points) {
            Set<Point> todel = new HashSet<Point>();
            for (Point pnt : pntSet) {
                if (pnt.dist(new Point(this, mouseX, mouseY)) < pnt.getSize()) {
                    pnt.x = mouseX;
                    pnt.y = mouseY;
                    if (keyPressed && key == DELETE) {
                        todel.add(pnt);
                    }
                }
            }
            for (Point pnt : todel) {
                pntSet.remove(pnt);
            }
        }

    }

}
