## Scripts
This directory holds all the code developed during my Master's Thesis.
A general note: The software is developed parallel to the development of the algorithm.
The code does not claim to be elegant in any means, in fact it is rather ugly and meant to be functional.
Moreover, there are many scripts that do not do anythng useful, as they were only used to experiment.
However, I added commments in each of the files explaining what the files are/were supposed to do.

The structure of this code repository is as follows.

### Necklace
This folder contains an implementation of a `Necklace` class.
Moreover, it contains an implementation of the alpha-Necklace-Splitting algorithm (using PULP to solve the ILP).
Note, however, that this implementation is slightly different from the algorithm described in the thesis.
In particular, we use a string to represent the necklace (not a family of point sets) and the order of the colours is not global
but determined by the order as the colours appear in the necklace.
However, I tried to include as many comments as possible to explain the implementation.
The main implementation is in the file `necklace`.
The file `solve_lp` is only used to solve the ILP for irreducible necklaces.
Moreover `my_euler` is a randomized implementation of an euler walk algorithm from `networkx`, to generate random irreducible necklaces.
For usage information of how to use our implementation see the `readme.md` file of that directory.

### scripts
This folder contains many python scripts used to generate, test, list, experiment, count, etc. with/on necklaces.
Many files do not perform useful tasks, look at the files to see what they are supposed to do.
To highlight one specific file the `sat_to_neckl.py` script implements the reduction from 3SAT to alpha-Necklace-Deciding used to show NP-hardness.

### NecklaceGUI
This is a processing sketch used to visualize necklaces and their cuts in an interactive way.
See the file `main.pde` for an explanation.

### pointline
An experimental processing sketch to get some intuition on point-line duality.
It did not turn out to be useful.

### separability
This folder contains a Java project (using processing as a library) used to experiment with a alpha-Necklace-Splitting formulation in two dimensions.
Running the project will launch a wrapper where one can select various sketches, including a separability checker in 2D, Fuzzing point sets and showing/generating alpha cuts.
This project was also used to generate the counter-examples for some conjectures on 2D necklace splitting.
See the corresponding source code.
Again, there should be some comments explaining what the functionality should be.

## Further Notes
 - The python requirements are given in `requirements.txt`.
 - For the separability Java project, processing needs to be installed. Moreover, the paths to the .jar files need to be adapted in `pom.xml`.
 - To display `.dot` graph files, I recommend using VScode and the extension "Graphviz (dot) language support for Visual Studio Code" by J. Pinto. Alternatively, use `dot` directly in the command line using `dot -Tpng graph.dot > graph.png`. In any case you need the graphviz framework obtainable from https://graphviz.org/download/.
 - I generated a lot of necklaces (~90GB) these are not included in this repository, but I am happy to share them with anyone who is interested.
 - This repository does not include driver code to run the algorithm, it is solely meant for archiving every peace of software developed during my thesis. However, driver code can be found in the more clean repository [https://github.com/linstald/alpha-necklace-splitting][alpha-necklace-splitting]
