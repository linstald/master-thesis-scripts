import java.util.*;

class Necklace {
  float y1, y2;
  int n, N;
  ArrayList<Bead> beads;
  HashSet<Integer> cuts;
  IntDict alphaDictPos;
  IntDict alphaDictNeg;
  IntDict beadDict;
  ArrayList<Character> types;
  float spacing;

  Necklace(float y1, float y2, String neckl) {
    this.y1 = y1;
    this.y2 = y2;
    this.beadDict = new IntDict();
    this.types = new ArrayList<Character>();
    this.beads = new ArrayList<Bead>();
    this.cuts = new HashSet<Integer>();
    this.alphaDictPos = new IntDict();
    this.alphaDictNeg = new IntDict();

    // compute bead Dictionary
    for (int i = 0; i<neckl.length(); i++) {
      char c = neckl.charAt(i);
      if (this.beadDict.hasKey(""+c)) {
        this.beadDict.increment(""+c);
      } else {
        this.beadDict.set(""+c, 1);
        this.types.add(c);
      }
    }
    // compute n, the size of beadDict
    this.n = this.beadDict.size();
    this.N = neckl.length();

    this.spacing = width / (this.N + 1.0);

    // construct the different beads
    for (int i = 0; i< neckl.length(); i++) {
      char c = neckl.charAt(i);
      int type = this.types.indexOf(c);
      this.beads.add(new Bead(type, i, c, this));
    }
  }

  boolean isInCut(int i) {
    return this.cuts.contains(i);
  }

  void addCut(int i) {
    if (!this.isInCut(i)) {
      this.cuts.add(i);
    }
  }

  void removeCut(int i) {
    if (this.isInCut(i)) {
      this.cuts.remove(i);
    }
  }

  void clearCut() {
    this.cuts.clear();
  }

  void show() {
    alphaDictPos.clear();
    alphaDictNeg.clear();
    for (String k : beadDict.keys()) {
      alphaDictPos.set(k, 0);
      alphaDictNeg.set(k, 0);
    }
    boolean pos = true;
    if (n > 10) {
      colorMode(HSB, this.n+2, 100, 100);
    } else {
      colorMode(RGB);
    }
    for (Bead b : this.beads) {
      if (this.isInCut(b.index)) {
        alphaDictPos.increment(""+b.letter);
        alphaDictNeg.increment(""+b.letter);
        pos = !pos;
        b.setY(0.5*(this.y2-this.y1)+this.y1);

        stroke(0);
        strokeWeight(4);
        line(b.x, 0, b.x, height);
      } else if (pos) {
        b.setY(this.y1);
        alphaDictPos.increment(""+b.letter);
      } else {
        b.setY(this.y2);
        alphaDictNeg.increment(""+b.letter);
      }
      b.show();
    }
    float y = 60;
    float dy = 26;
    float x = 20;
    float dx = 22;
    float tsize = 30;
    for (String k : alphaDictPos.keys()) {
      setColour(this.types.indexOf(k.charAt(0)), this);
      textSize(tsize);
      text(k, x, y);
      fill(0);
      text(": " + alphaDictPos.get(k) + "/" + this.beadDict.get(k), x+dx, y);
      y+=dy;
    }
    String[] keys = alphaDictNeg.keyArray();
    y = height - 60;
    for (int i = keys.length-1; i>=0; i--) {
      String k = keys[i];
      setColour(this.types.indexOf(k.charAt(0)), this);
      textSize(tsize);
      text(k, x, y);
      fill(0);
      text(": " + alphaDictNeg.get(k)+ "/" + this.beadDict.get(k), x+dx, y);
      y-=dy;
    }
  }

  void mouseClicked() {
    for (Bead b : this.beads) {
      b.mouseClicked();
    }
  }
}
