int SPEED = 10;

color[] colors = {#006400,
  #00008b,
  #b03060,
  #ff4500,
  #ffff00,
  #00ff00,
  #00ffff,
  #ff00ff,
  #6495ed,
  #ffdead
};

void setColour(int type, Necklace neckl) {
  noStroke();
  if (neckl.n <= 10) {
    fill(colors[type]);
  } else {
    fill(type, 100, 100);
  }
}

void setColour(int type, Necklace neckl, boolean mouseOver) {
  
  if (mouseOver) {
    stroke(80);
    strokeWeight(5);
    if (neckl.n <= 10) {
      fill(colors[type]);
    } else {
      fill(type, 30, 100);
    }
  } else {
    setColour(type, neckl);
  }
}

class Bead {
  float x, y;

  float targetY;

  int type;
  float r;
  int index;
  char letter;
  Necklace neckl;

  Bead(int type, int i, char letter, Necklace neckl) {
    this.type = type;
    this.index = i;
    this.letter = letter;
    this.neckl = neckl;
    this.x = (i+1)*neckl.spacing;
    this.y = neckl.y1;
    this.r = max(neckl.spacing*0.4, width/100);
  }

  boolean isMouseOver() {
    float dx = mouseX-this.x;
    float dy = mouseY-this.y;
    return dx*dx+dy*dy < r*r;
  }

  void setY(float y) {
    this.targetY = y;
  }

  void show() {
    if (y != targetY) {
      float diff = targetY - y;
      float speed = pow(2, 0.5+abs(diff) / (abs(this.neckl.y1 - this.neckl.y2))) * SPEED;
      if (diff > 0) {
        y = constrain(y+speed, 0, targetY);
      } else {
        y = constrain(y-speed, targetY, height);
      }
    }
    setColour(this.type, this.neckl, this.isMouseOver());
    circle(x, y, 2*r);
    textSize(1.3*r);
    fill(0);
    textAlign(CENTER, CENTER);
    text(""+this.letter, x, y-0.3*r);
    textAlign(LEFT, BASELINE);
  }

  void mouseClicked() {
    if (!this.isMouseOver()) {
      return;
    }
    if (neckl.isInCut(this.index)) {
      this.neckl.removeCut(this.index);
    } else {
      this.neckl.addCut(this.index);
    }
  }
}
