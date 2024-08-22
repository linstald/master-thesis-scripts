package com.separability;

import com.separability.sketches.MainSketch;

import processing.core.PApplet;

public class Main {
    public static void main(String[] args) {
        System.out.println();
        System.out.println("Launching main sketch.");
        PApplet sketch = new MainSketch();
        PApplet.runSketch(new String[] { "main" }, sketch);
    }
}