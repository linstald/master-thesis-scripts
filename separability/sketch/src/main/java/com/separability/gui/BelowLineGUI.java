package com.separability.gui;

import java.util.ArrayList;

import com.separability.Utils;
import com.separability.geometry.Line;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import processing.opengl.PShader;

public class BelowLineGUI extends PApplet {

    // allows to add points in a first phase and then add lines in a second phase
    public int state; // 0: first phase, else second phase
    public int mode; // 0: manual increment of state, 1: auto increment

    private PVector fMousePos; // first mouse position

    private int saveFrame = -10;

    private PShader belowLineShader;
    private PGraphics bg;

    public ArrayList<Line> lines;

    public void settings() {
        size(1000, 1000, P2D);
    }

    public void setup() {
        state = 0;
        mode = 0;

        fMousePos = null;

        belowLineShader = loadShader("resources/belowlines.glsl");
        bg = createGraphics(width, height, P2D);

        lines = new ArrayList<Line>();
    }

    public void draw() {
        if (saveFrame == frameCount + 1) {
            String name = "img/screen_" + Utils.currName(this) + ".pdf";
            beginRecord(PDF, name);
            println("Saved image under " + name);
        }
        if (saveFrame == frameCount) {
            saveFrame = -10;
            endRecord();
        }
        if (state > 0) {
            // render the positive and negative side in a separate image using a shader
            belowLineShader.set("lines", Utils.linesToShader(lines));
            belowLineShader.set("size", 2 * lines.size());
            bg.beginDraw();
            bg.background(255);
            bg.rect(0, 0, bg.width, bg.height);
            bg.shader(belowLineShader);
            // for (int x = 0; x<bg.width; x++) {
            // for(int y = 0; y<bg.height; y++) {
            // int belowCount = 0;
            // for (Line lne : lines) {
            // if(lne.isBelow((float)x, (float)y)) {
            // belowCount +=1;
            // }
            // }
            // if(belowCount % 2 == 0) {
            // bg.stroke(0, 0, 255, 50);
            // } else {
            // bg.stroke(255, 0, 255, 50);
            // }
            // bg.strokeWeight(1);
            // bg.point(x, y);
            // }
            // }
            bg.endDraw();
        } else {
            bg.beginDraw();
            bg.background(255);
            bg.endDraw();
        }

        image(bg, 0, 0, width, height);
        if (lines != null) {
            for (Line lne : lines) {
                lne.show();
            }
        }

        if (fMousePos != null && mousePressed) {
            Line lne = new Line(this, fMousePos.x, fMousePos.y, mouseX, mouseY);
            lne.show();
        }
        if (mode == 1) {
            incState();
        }
    }

    public void mousePressed() {
        if (state > 0) {
            fMousePos = new PVector(mouseX, mouseY);
        }
    }

    public void mouseReleased() {
        if (state > 0 && fMousePos != null) {
            Line lne = new Line(this, fMousePos.x, fMousePos.y, (float) mouseX, (float) mouseY);
            if (!lne.isDegen()) {
                lines.add(lne);
            }
            fMousePos = null;
        }
    }

    public void keyPressed() {
        if (key == ENTER) {
            incState();
        }
        if (key == 'c') {
            if (state > 0) {
                lines.clear();
            }
        }
        if (key == DELETE) {
            fMousePos = null;
        }
        if (key == BACKSPACE) {
            if (state > 0 && lines.size() > 0) {
                lines.remove(lines.size() - 1);
            }
        }
        if (key == ' ') {
            mode = 1 - mode;
        }

        if (key == 's') {
            saveFrame = frameCount + 2;
        }
    }

    public void incState() {
        state += 1;
    }
}
