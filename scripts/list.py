import os
import sys

sys.path.append(os.path.abspath(os.path.join(__file__, os.pardir, os.pardir)))
from Necklace.necklace import *

help = """
# test script that was used to see which necklaces consist only of bicomponents,
# have no neighbouring intervals and do not have inside embracing colours.
# sys.argv[1] specifies a file holding necklace strings to check.
# For a reader knowing the results and wondering what inside embracing is:
# These are bicomponents where all colours are either in between the two components, or outside
# but there is no colour having a component between these two components and another component outside.
# It turned out that when there are no inside embracing colours (and the other conditions) we are irreducible.
"""

if len(sys.argv) < 2:
    print(help)
    exit()

file = sys.argv[1]


# these seem to have an isomorphic walk graph
with open(file) as f:
    line = f.readline()
    while line:
        sep, neckl = line.split(",")
        neckl = Necklace(neckl)
        if int(sep) <= neckl.n + 1:
            if (
                neckl.hasOnlyBicomponents()
                and not neckl.hasNeighbouringIntervals()
                and not neckl.hasInsideEmbracing()
            ):
                print(neckl)
        line = f.readline()
