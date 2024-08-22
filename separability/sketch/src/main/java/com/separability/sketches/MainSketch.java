package com.separability.sketches;

import java.util.ArrayList;

import processing.core.PApplet;

class AppWrapper {
    PApplet sketch;
    String name;

    AppWrapper(PApplet sketch, String name) {
        this.sketch = sketch;
        this.name = name;
    }
}

public class MainSketch extends PApplet {

    ArrayList<AppWrapper> sketches;
    int index;

    boolean started;
    boolean restart;

    public void settings() {
        size(500, 250, P2D);
    }

    public void setup() {
        started = false;
        restart = false;
        index = 0;
        sketches = new ArrayList<AppWrapper>();
        sketches.add(new AppWrapper(new AlphaSketch(), "Alpha"));
        sketches.add(new AppWrapper(new SepSketch(), "Separability"));
        sketches.add(new AppWrapper(new FuzzingSketch(), "Fuzzing"));

    }

    public void draw() {
        background(255);
        String name = sketches.get(index).name;
        fill(0);
        noStroke();
        textSize(30);
        textAlign(CENTER, CENTER);
        text(name, width / 2, height / 2);
        if (restart) {
            PApplet selected = sketches.get(index).sketch;
            selected.setup();
            selected.loop();
            restart = false;
        }
    }

    public void mouseClicked() {
        if (mouseButton != LEFT) {
            return;
        }
        if (started) {
            PApplet selected = sketches.get(index).sketch;
            selected.noLoop();
            restart = true;
        } else {
            run(sketches.get(index));
        }
    }

    public void keyPressed() {
        if (!started) {
            index = (index + 1) % sketches.size();
        }
    }

    void run(AppWrapper sketch) {
        if (started) {
            return;
        }
        started = true;
        runSketch(new String[] { "--sketch-path=" + sketchPath(), sketch.name }, sketch.sketch); // <>//
    }
}