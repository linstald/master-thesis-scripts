import os
import sys

sys.path.append(os.path.abspath(os.path.join(__file__, os.pardir, os.pardir)))

import Necklace.necklace as neckl
import random

help = """
# Testing script that was used to see what happens if alpha only changes in one coordinate
# The first argument (sys.argv[1]) specifies the necklace string to test on.
# The alpha cut is computed for a random alpha, where the alpha of the first colour varies
# the output is a Processing code, that can be copy-pasted to the NecklaceGUI sketch.
"""


def genProcessingCode(n: neckl.Necklace, cuts: list[list]):
    print(f'neckl = new Necklace(height/3, 2*height/3, "{n}");')
    for c in cuts:
        cstr = ", ".join([str(x) for x in c])
        print(f"allCuts.add(new HashSet<Integer>(Arrays.asList({cstr})));")


if __name__ == "__main__":
    if len(sys.argv) < 2:
        print(help)
        exit()

    N = neckl.Necklace(neckl.pumpNecklace(sys.argv[1]))

    alpha = {c: random.randint(1, len(N.beadDict[c])) for c in N.beadDict}
    firstCol = list(N.beadDict.keys())[0]
    cuts = []
    for i in range(1, len(N.beadDict[firstCol]) + 1):
        alpha[firstCol] = i
        alphaCut, negAlphaCut = N.findAlphaCut(alpha)
        print(N, alpha, (alphaCut, negAlphaCut))
        cuts.append(alphaCut)

    genProcessingCode(N, cuts)
