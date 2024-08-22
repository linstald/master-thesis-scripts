import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import com.separability.geometry.PointSetFamily;
import com.separability.sketches.AlphaSketch;

public class InteractiveTestSketch extends AlphaSketch {

    List<String> pointsFiles;
    int fileIndex;
    CountDownLatch cdl;

    public InteractiveTestSketch(List<String> filenames, CountDownLatch cdl) {
        this.pointsFiles = filenames;
        this.cdl = cdl;
    }

    public void setup() {
        super.setup();
        this.points = new PointSetFamily();
        if (pointsFiles.size() > 0) {
            fileIndex = 0;
            this.points.load(this, this.pointsFiles.get(fileIndex));
        } else {
            cdl.countDown();
        }
    }

    public void incState() {
        super.incState();
        if (currAlpha != null) {
            boolean isTangent = true;
            for (int i = 0; i < currAlpha.size(); i++) {
                isTangent = isTangent && (currAlpha.get(i) == 0
                        || currAlpha.get(i) == points.get(i).size());
            }
            if (isTangent) {
                println("tangent");
                mode = 0;
            }
        }
    }

    public void draw() {
        super.draw();
        textSize(20);
        fill(0);
        noStroke();
        text(fileIndex + " " + pointsFiles.get(fileIndex), 50, 50);
    }

    public void keyPressed() {
        super.keyPressed();
        if (key == ESC) {
            println("exiting");
            cdl.countDown();
        }
        if (key == 'f') {
            fileIndex = (fileIndex + 1) % pointsFiles.size();
            points = new PointSetFamily();
            points.load(this, pointsFiles.get(fileIndex));
            currAlpha = new ArrayList<Integer>();
            for (int i = 0; i < points.size(); i++) {
                currAlpha.add(0);
            }
            lines.clear();
        }
    }

}
