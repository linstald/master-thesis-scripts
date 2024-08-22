import os
import sys

sys.path.append(os.path.abspath(os.path.join(__file__, os.pardir, os.pardir)))
from Necklace.necklace import *

help = """
# This file was used to test out some reduction steps on irreducible necklaces.
# However, it was not interesting enough to proceed on that (no results could be found).
# sys.argv[1] specifies a file holding necklace strings.
"""

if len(sys.argv) < 2:
    print(help)
    exit()

file = sys.argv[1]

with open(file) as f:
    neckls = f.readlines()
    for n in neckls:
        neckl = Necklace(n)

        # 1. case, first degree 3, second degree 4
        if (
            len(neckl.letterDict[neckl.necklstr[0]]) == 2
            and len(neckl.letterDict[neckl.necklstr[1]]) == 2
        ):
            # need to remove first and the its adjacent degree 2 vertex in direction of the second
            deg2 = [
                c
                for c in neckl.graph.neighbors(neckl.necklstr[0])
                if c in neckl.graph.neighbors(neckl.necklstr[1])
            ][0]
            smaller = "".join(
                [c for c in neckl.necklstr if c not in [neckl.necklstr[0], deg2]]
            )
            print(smaller)

        # 2. case, first degree 3, second degree 2, third degree 4
        if (
            len(neckl.letterDict[neckl.necklstr[0]]) == 2
            and len(neckl.letterDict[neckl.necklstr[1]]) == 1
            and len(neckl.letterDict[neckl.necklstr[2]]) == 2
            and neckl.necklstr[2] != neckl.necklstr[-1]
        ):
            smaller = "".join(
                [
                    c
                    for c in neckl.necklstr
                    if c not in [neckl.necklstr[0], neckl.necklstr[1]]
                ]
            )
            print(smaller)

        # 3. case, first degree 3, second degree 2, third degree 3
        if (
            len(neckl.letterDict[neckl.necklstr[0]]) == 2
            and len(neckl.letterDict[neckl.necklstr[1]]) == 1
            and len(neckl.letterDict[neckl.necklstr[2]]) == 2
            and neckl.necklstr[2] == neckl.necklstr[-1]
        ):
            # we remove the second degree 3 vertex and its other adjacent degree 2.
            otherdeg2 = [
                x
                for x in neckl.graph.neighbors(neckl.necklstr[2])
                if x != neckl.necklstr[1] and len(neckl.letterDict[x]) == 1
            ][0]
            smaller = "".join(
                [c for c in neckl.necklstr if c not in [neckl.necklstr[2], otherdeg2]]
            )
            print(smaller)
