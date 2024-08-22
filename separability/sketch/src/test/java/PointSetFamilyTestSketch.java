import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import com.separability.Utils;

import processing.core.PApplet;

interface DrawTask {
    void draw();
}

public class PointSetFamilyTestSketch extends PApplet {
    CountDownLatch cdl;
    ArrayList<DrawTask> tasks;
    boolean ready;

    public PointSetFamilyTestSketch(CountDownLatch cdl) {
        this.cdl = cdl;
        this.tasks = new ArrayList<DrawTask>();
        this.ready = false;
    }

    public void setTasks(ArrayList<DrawTask> tasks) {
        this.tasks = tasks;
    }

    public boolean isReady(int arg) {
        return ready;
    }

    public void settings() {
        size(1000, 1000, P2D);
    }

    public void draw() {
        background(255);
        for (DrawTask tsk : tasks) {
            tsk.draw();
        }
        if (frameCount > 5) {
            ready = true;
        }
    }

    public void keyPressed() {
        if (key == ESC) {
            println("exiting");
            cdl.countDown();
        }
        if (key == 's') {
            String name = "img/test_screen" + Utils.currName(this) + ".png";
            save(name);
            println("Saved image under " + name);

        }
    }
}