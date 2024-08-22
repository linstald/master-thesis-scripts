/* 
** This file contains a Processing (https://processing.org/) sketch that displays necklaces and their cuts.
** A necklaces is represented as a string, and cuts as a HashSet<Integer> holding the indices of the cut beads.
** Use processing 4 to run this sketch.
*/

import java.util.*;
import java.util.stream.Collectors;
Necklace neckl;
ArrayList<HashSet<Integer>> allCuts;
boolean toggleAllCuts = false;

boolean inserting = false;
String inputBuffer;


void setup() {
  size(1800, 1000);
  allCuts = new ArrayList<HashSet<Integer>>();

  //neckl = new Necklace(height/3, 2*height/3, "aaaabbbccaaaabbbbb");
  //allCuts.add(new HashSet<Integer>(Arrays.asList(0, 4, 7)));
  //allCuts.add(new HashSet<Integer>(Arrays.asList(1, 4, 7)));
  //allCuts.add(new HashSet<Integer>(Arrays.asList(2, 4, 7)));
  //allCuts.add(new HashSet<Integer>(Arrays.asList(3, 4, 7)));
  //allCuts.add(new HashSet<Integer>(Arrays.asList(6, 8, 9)));
  //allCuts.add(new HashSet<Integer>(Arrays.asList(6, 8, 10)));
  //allCuts.add(new HashSet<Integer>(Arrays.asList(6, 8, 11)));
  //allCuts.add(new HashSet<Integer>(Arrays.asList(6, 8, 12)));

  //neckl = new Necklace(height/3, 2*height/3, "aaaaaaaabbbcddddddddeeeeeecccccccaaadddddddd");
  //allCuts.add(new HashSet<Integer>(Arrays.asList(0, 9, 14, 24, 32)));
  //allCuts.add(new HashSet<Integer>(Arrays.asList(1, 9, 14, 24, 32)));
  //allCuts.add(new HashSet<Integer>(Arrays.asList(2, 9, 14, 24, 32)));
  //allCuts.add(new HashSet<Integer>(Arrays.asList(3, 9, 14, 24, 32)));
  //allCuts.add(new HashSet<Integer>(Arrays.asList(4, 9, 14, 24, 32)));
  //allCuts.add(new HashSet<Integer>(Arrays.asList(5, 9, 14, 24, 32)));
  //allCuts.add(new HashSet<Integer>(Arrays.asList(6, 9, 14, 24, 32)));
  //allCuts.add(new HashSet<Integer>(Arrays.asList(7, 9, 14, 24, 32)));
  //allCuts.add(new HashSet<Integer>(Arrays.asList(9, 11, 14, 24, 33)));
  //allCuts.add(new HashSet<Integer>(Arrays.asList(9, 11, 14, 24, 34)));
  //allCuts.add(new HashSet<Integer>(Arrays.asList(9, 11, 14, 24, 35)));

  //neckl = new Necklace(height/3, 2*height/3, "aaaaaaaaaabbbbbbbccccccccccccccccdddeeeeeeeeeeeeeeffffffffffffffffdddgggggggcccccccccccaaahhheeeeeee");
  //allCuts.add(new HashSet<Integer>(Arrays.asList(0, 12, 30, 35, 39, 64, 73, 90)));
  //allCuts.add(new HashSet<Integer>(Arrays.asList(1, 12, 30, 35, 39, 64, 73, 90)));
  //allCuts.add(new HashSet<Integer>(Arrays.asList(2, 12, 30, 35, 39, 64, 73, 90)));
  //allCuts.add(new HashSet<Integer>(Arrays.asList(3, 12, 30, 35, 39, 64, 73, 90)));
  //allCuts.add(new HashSet<Integer>(Arrays.asList(4, 12, 30, 35, 39, 64, 73, 90)));
  //allCuts.add(new HashSet<Integer>(Arrays.asList(5, 12, 30, 35, 39, 64, 73, 90)));
  //allCuts.add(new HashSet<Integer>(Arrays.asList(6, 12, 30, 35, 39, 64, 73, 90)));
  //allCuts.add(new HashSet<Integer>(Arrays.asList(7, 12, 30, 35, 39, 64, 73, 90)));
  //allCuts.add(new HashSet<Integer>(Arrays.asList(8, 12, 30, 35, 39, 64, 73, 90)));
  //allCuts.add(new HashSet<Integer>(Arrays.asList(9, 12, 30, 35, 39, 64, 73, 90)));
  //allCuts.add(new HashSet<Integer>(Arrays.asList(14, 30, 39, 64, 66, 71, 87, 90)));
  //allCuts.add(new HashSet<Integer>(Arrays.asList(14, 30, 39, 64, 66, 71, 88, 90)));
  //allCuts.add(new HashSet<Integer>(Arrays.asList(14, 30, 39, 64, 66, 71, 89, 90)));

  neckl = new Necklace(height/3, 2*height/3, "cdcbeggfbefbbhjibhibbkmmlbklbbnpobnobbqsrbqgrbbtsubtmubbvwbvjswbbxzybxgybbAzBbApBbbCDbCmzDbbgbgagagaEbjajaFbmbmamamaGbpapaHbsssIbzzzJbbaa");
  allCuts.add(new HashSet<Integer>(Arrays.asList(0, 30, 31, 38, 39, 52, 53, 62, 63)));
  allCuts.add(new HashSet<Integer>(Arrays.asList(22, 23, 37, 38, 46, 47, 61, 62, 64)));
}
int index = 0;
void draw() {
  colorMode(RGB, 256, 256, 256);
  background(220);
  if (allCuts.size() > 0) {
    fill(0);
    textSize(24);
    text(index+" "+toggleAllCuts, 20, 20);
  }
  if (frameCount % 100 == 0 && allCuts.size() > 0 && toggleAllCuts) {
    HashSet<Integer> cut = allCuts.get(index);
    index = (index + 1) % allCuts.size();
    neckl.clearCut();
    for (Integer i : cut) {
      neckl.addCut(i);
    }
  }

  neckl.show();
  if (inserting) {
    fill(0);
    textSize(24);
    text("INSERTING", 20, height-50);
    text(inputBuffer, 20, height-20);
  }
}

void mousePressed() {
  neckl.mouseClicked();
}

void keyPressed() {
  if (inserting) {
    if (inputBuffer == null) {
      inputBuffer = "";
    }
    if (key == BACKSPACE) {
      if (inputBuffer.length() > 0) {
        inputBuffer = inputBuffer.substring(0, inputBuffer.length()-1);
      }
    } else if (key == ENTER || key == RETURN) {
      inserting = false;
      neckl = new Necklace(height/3, 2*height/3, inputBuffer.strip());
      allCuts.clear();
    } else if (key == DELETE) {
      inserting = false;
    } else if (key != CODED) {
      inputBuffer += key;
    }
  } else if (key == 'i') { //insert
    inputBuffer = "";
    inserting = true;
  } else if (key == ' ') {
    toggleAllCuts = false;
  } else {
    toggleAllCuts = true;
  }
}

ArrayList<HashSet<Integer>> computeAllCuts(String neckl) {
  // identify the possible indices for each type
  HashMap<String, ArrayList<Integer>> indicesPerType = new HashMap<String, ArrayList<Integer>>();
  for (int i = 0; i<neckl.length(); i++) {
    String k = ""+neckl.charAt(i);
    if (!indicesPerType.containsKey(k)) {
      indicesPerType.put(k, new ArrayList<Integer>());
    }
    indicesPerType.get(k).add(i);
  }
  Set<String> allTypes = indicesPerType.keySet();
  int n = allTypes.size();
  // the list eventually containing all cuts
  ArrayList<HashSet<Integer>> cuts = new ArrayList<HashSet<Integer>>();
  // a queue that contains a dictionary containing the cut index per type.
  Stack<HashMap<String, Integer>> queue = new Stack<HashMap<String, Integer>>();
  queue.push(new HashMap<String, Integer>());
  HashSet<String> uniqueCuts = new HashSet<String>();
  // as long as there are unfinished sets, pop one and fix it iteratively
  while (!queue.isEmpty()) {
    HashMap<String, Integer> fixed = queue.pop();
    // if all keys fixed, get cut and append to list
    if (fixed.keySet().size() == n) {
      HashSet<Integer> cut = new HashSet<Integer>();
      for (Integer index : fixed.values()) {
        cut.add(index);
      }
      String cutKey = cut.stream().sorted().collect(Collectors.toList()).toString();
      if (!uniqueCuts.contains(cutKey)) {
        cuts.add(cut);
        uniqueCuts.add(cutKey);
      }
      continue;
    }
    // for each unfixed key, fix one index and insert new set
    for (String k : allTypes) {
      if (!fixed.containsKey(k)) {
        for (Integer index : indicesPerType.get(k)) {
          HashMap<String, Integer> newFixed = (HashMap<String, Integer>)fixed.clone();
          newFixed.put(k, index);
          // this is dangerous: newFixed should only be pushed if it was never considered before
          queue.push(newFixed);
        }
      }
    }
  }

  return cuts;
}
