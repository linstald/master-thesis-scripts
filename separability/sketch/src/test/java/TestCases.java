
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

import com.separability.Utils;
import com.separability.geometry.Point;
import com.separability.geometry.PointSet;
import com.separability.geometry.PointSetFamily;

import processing.core.PApplet;

public class TestCases {

    static CountDownLatch cdl = new CountDownLatch(1);

    // simple test function, runs only sketch
    void doTest(PApplet sketch) {
        PApplet.runSketch(new String[] { "test" }, sketch);
        try {
            cdl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            cdl = new CountDownLatch(1);
        }
    }

    public ArrayList<String> getAllPointSets(String dir) {

        return new ArrayList<String>(Stream.of(new File(dir).listFiles())
                .filter(file -> !file.isDirectory())
                .map(file -> dir + "/" + file.getName())
                .sorted()
                .collect(Collectors.toList()));
    }

    @Test
    public void testPointSetFamily() {
        List<String> allFiles = getAllPointSets("points");
        assertTrue(allFiles.size() > 0, "There should be point sets within 'points/'");
        String filename = allFiles.get(0);
        System.out.println(filename);
        PointSetFamilyTestSketch sketch = new PointSetFamilyTestSketch(cdl);
        PApplet.runSketch(new String[] { "test" }, sketch);

        // wait until sketch is ready
        int counter = 0;
        while (!sketch.isReady(counter)) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            counter += 1;
        }
        assertTrue(counter >= 0, "dummy assert");
        PointSetFamily points = new PointSetFamily();
        if (!filename.startsWith("notalpha") || filename.startsWith("notalphanotsep")) {
            return;
        }
        points.load(sketch, "points/" + filename);
        ArrayList<DrawTask> tasks = new ArrayList<DrawTask>();
        for (int i = 0; i < points.size(); i++) {
            PointSet pntSet = points.get(i);
            int col = Utils.getColor((float) i / (float) points.size(), sketch);
            for (Point pnt : pntSet) {
                pnt.setColor(col);
                pnt.setSize(10f);
                tasks.add(() -> {
                    pnt.show();
                });
            }
        }
        sketch.setTasks(tasks);
        // the pointset family should consist of 4 point sets
        assertTrue(points.size() == 4, "four pointsets");
        // there should be impossible alphas (even using all cutlines)
        points.setCutDefinition(PointSetFamily.CUT_LINES);
        Set<ArrayList<Integer>> alphas = points.getImpossibleAlpha();
        assertTrue(alphas.size() > 0, "invalid alphas");
        // also there should be no tangent among the impossible alphas (hence the set is
        // two-separable)
        for (ArrayList<Integer> alpha : alphas) {
            boolean isTangent = true;
            for (int i = 0; i < alpha.size(); i++) {
                isTangent = isTangent && (alpha.get(i) == 0 || alpha.get(i) == points.get(i).size());
            }
            assertFalse(isTangent, "no tangents");
        }
        try {
            cdl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            cdl = new CountDownLatch(1);
        }

    }

    @Test
    public void testInterActivePointSetFamily() {
        List<String> filenames = getAllPointSets("points").stream().filter((x) -> x.contains("notalpha2024"))
                .toList();
        assertTrue(filenames.size() > 0, "There should be files within 'points/' containing 'notalpha2024'");
        System.out.println(filenames);
        InteractiveTestSketch sketch = new InteractiveTestSketch(filenames, cdl);
        doTest(sketch);
    }

    @Test
    public void testAllLines() {
        List<String> filenames = getAllPointSets("points");
        assertTrue(filenames.size() > 0, "There should be point sets within 'points/'");
        System.out.println(filenames);
        AllLineTestSketch sketch = new AllLineTestSketch(filenames, PointSetFamily.COLOURFUL_LINES, cdl);
        doTest(sketch);
    }

    @Test
    public void testPointSetFamilyEditor() {
        List<String> filenames = getAllPointSets("points");
        assertTrue(filenames.size() > 0, "There should be point sets within 'points/'");
        PointSetFamilyEditorTestSketch sketch = new PointSetFamilyEditorTestSketch(filenames, cdl);
        doTest(sketch);

    }

}
