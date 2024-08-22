package com.separability.geometry;

import java.util.LinkedList;
import java.util.Random;

import processing.core.PApplet;
import processing.core.PVector;

public class PointSetFuzzer implements PointSetSupplier {

    private LinkedList<PointSet> ready;
    private LinkedList<PointSet> toFuzz;
    private int batchSize = 100;

    interface Operation {
        void perform(PointSet pntSet);
    }

    private Random rnd = new Random();

    private Operation moveRandomOP = (pntSet) -> {
        int index = rnd.nextInt(pntSet.size());
        Point pnt = pntSet.get(index);
        PVector dir = PVector.random2D().mult(10 + rnd.nextFloat() * 50);
        pnt.add(dir);
    };

    private Operation deleteRandomOP = (pntSet) -> {
        if (pntSet.size() == 1) {
            return;
        }
        int index = rnd.nextInt(pntSet.size());
        pntSet.remove(index);
    };

    private Operation duplicateAndMoveRandomOP = (pntSet) -> {
        int index = rnd.nextInt(pntSet.size());
        Point dupl = pntSet.get(index);
        Point ndu = new Point(dupl);
        ndu.add(PVector.random2D().mult(10 + rnd.nextFloat() * 50));
        pntSet.add(ndu);
    };

    private Operation addRandomOP = (pntSet) -> {
        if (pntSet.size() > 0) {
            PApplet sketch = pntSet.get(0).getSketch();
            Point pnt = new Point(sketch, rnd.nextFloat() * sketch.width, rnd.nextFloat() * sketch.height);
            pntSet.add(pnt);
        }
    };

    private Operation bulkOP = (pntSet) -> {
        duplicateAndMoveRandomOP.perform(pntSet);
        moveRandomOP.perform(pntSet);
        deleteRandomOP.perform(pntSet);
        addRandomOP.perform(pntSet);
    };

    private Operation[] operations = new Operation[] {
            moveRandomOP,
            deleteRandomOP,
            duplicateAndMoveRandomOP,
            addRandomOP,
            bulkOP
    };

    public PointSetFuzzer(PointSet initialSet) {
        this.ready = new LinkedList<PointSet>();
        this.ready.add(initialSet);
        this.toFuzz = new LinkedList<PointSet>();
    }

    private void fuzzAll() {
        if (toFuzz.size() > batchSize * batchSize) {
            batchSize = Math.max(1, batchSize / 2);
        }
        for (PointSet curr = toFuzz.poll(); curr != null; curr = toFuzz.poll()) {
            fuzzBatch(curr);
        }
    }

    private void fuzzBatch(PointSet pntSet) {
        // fuzz the pntSet batchSize times
        for (int i = 0; i < batchSize; i++) {
            fuzzSingle(pntSet);
        }
    }

    private void fuzzSingle(PointSet pntSet) {
        // performs one fuzz step on pntSet and adds it to the ready list
        // contains actual fuzzing logic

        PointSet copied = pntSet.copy();
        // pick random operation to perform
        Random rnd = new Random();
        int opIndex = rnd.nextInt(operations.length);
        operations[opIndex].perform(copied);
        // restrict every point to be inside canvas
        for (Point pnt : copied) {
            if (pnt.x > pnt.getSketch().width) {
                pnt.x = pnt.getSketch().width;
            }
            if (pnt.y > pnt.getSketch().height) {
                pnt.y = pnt.getSketch().height;
            }
            if (pnt.x < 0) {
                pnt.x = 0;
            }
            if (pnt.y < 0) {
                pnt.y = 0;
            }
        }
        ready.add(copied);
    }

    public PointSet nextPointSet() {
        if (ready.size() <= 0) {
            fuzzAll();
        }
        PointSet last = ready.poll();
        toFuzz.add(last);
        return last;
    }
}
