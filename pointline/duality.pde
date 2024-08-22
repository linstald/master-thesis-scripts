/*
** This sketch was solely used to get an interactive point-line-duality visualization.
** Use processing 4 to run this sketch.
*/

float xmin, xmax, ymin, ymax;

ArrayList<PVector> redPoints;
ArrayList<PVector> bluePoints;

float pointR = 10;

void setup() {
  xmin = -10;
  xmax = 10;
  ymin = -10;
  ymax = 10;
  size(2000, 2000);
  redPoints = new ArrayList<PVector>();
  bluePoints = new ArrayList<PVector>();
}


void draw() {
  background(255);
  stroke(0, 0, 0);
  strokeWeight(0);
  drawLine(0, 0);
  drawLine(100000, 0);
  for (int i = 0; i<redPoints.size(); i++) {
    stroke(255, 0, 0);
    strokeWeight(pointR);
    PVector p = redPoints.get(i);
    drawPoint(p.x, p.y);
    PVector dual = dualLine(p.x, p.y);
    strokeWeight(pointR/2);
    drawLine(dual.x, dual.y);
  }

  for (int i = 0; i<bluePoints.size(); i++) {
    stroke(0, 0, 255);
    strokeWeight(pointR);
    PVector p = bluePoints.get(i);
    drawPoint(p.x, p.y);
    PVector dual = dualLine(p.x, p.y);
    strokeWeight(pointR/2);
    drawLine(dual.x, dual.y);
  }
}

void mouseDragged() {
  ArrayList<PVector> points = redPoints;
  if (mouseButton == RIGHT) {
    points = bluePoints;
  }

  ArrayList<Integer> mod = new ArrayList<Integer>();
  if (!keyPressed) {
    for (int i = 0; i<points.size(); i++) {
      PVector p = points.get(i);
      PVector sk = sceneToSketch(p.x, p.y);
      if (dist(mouseX, mouseY, sk.x, sk.y) < pointR / 2) {
        points.set(i, sketchToScene(mouseX, mouseY));
        mod.add(i);
      }
    }
    for (int i = mod.size() - 1; i>0; i--) {
      int index = mod.get(i);
      points.remove(index);
    }
  } else {
    if (mod.size() == 0) {
      points.add(sketchToScene(mouseX, mouseY));
    }
  }
}

void keyPressed() {
  if (key == 'c') {
    redPoints.clear();
    bluePoints.clear();
  }
  if (key == 'b') {
    bluePoints.clear();
  }
  if (key == 'r') {
    redPoints.clear();
  }
}



PVector sketchToScene(float sx, float sy) {
  return new PVector(map(sx, 0, width, xmin, xmax), map(sy, 0, height, ymax, ymin));
}


PVector sceneToSketch(float cx, float cy) {
  return new PVector(map(cx, xmin, xmax, 0, width), map(cy, ymin, ymax, height, 0));
}

void drawPoint(float cx, float cy) {
  PVector s = sceneToSketch(cx, cy);
  point(s.x, s.y);
}


void drawLine(float m, float q) {
  float startY = m*xmin + q;
  float endY = m*xmax + q;
  PVector start = sceneToSketch(xmin, startY);
  PVector end = sceneToSketch(xmax, endY);
  line(start.x, start.y, end.x, end.y);
}


PVector dualLine(float px, float py) {
  return new PVector(px, -py);
}

PVector dualPoint(float m, float q) {
  return new PVector(m, -q);
}
