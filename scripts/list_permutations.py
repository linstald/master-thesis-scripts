import os
import sys

sys.path.append(os.path.abspath(os.path.join(__file__, os.pardir, os.pardir)))
from Necklace.necklace import *

help = """
# testing script used to see what type of permutations a necklace has in its alpha cuts
# sys.argv[1] specifies a file holding necklace strings
"""
if len(sys.argv) < 2:
    print(help)
    exit()

file = sys.argv[1]


with open(file) as f:
    neckls = f.readlines()
    uniquePerms = []
    for n in neckls:
        neckl = Necklace(n)
        print(neckl)
        cuts = neckl.generateAllComponentCuts()
        uniquePerNeckl = []
        for c in cuts:
            perm = neckl.getCutPermutation([neckl.componentDict[x][0] for x in c])
            permOneLine = [i ^ perm for i in range(perm.size)]
            if permOneLine not in uniquePerms:
                uniquePerms.append(permOneLine)
                print(permOneLine)
            if permOneLine not in uniquePerNeckl:
                uniquePerNeckl.append(permOneLine)
        print(neckl, len(uniquePerNeckl))
        for perm in uniquePerNeckl:
            print(perm)
    print(len(uniquePerms))
