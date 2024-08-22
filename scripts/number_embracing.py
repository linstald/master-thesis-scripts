import os
import sys

sys.path.append(os.path.abspath(os.path.join(__file__, os.pardir, os.pardir)))
from Necklace.necklace import *

help = """
# this file was used to count the number of embracing colours in a necklace
# sys.argv[1] specifies a file holding necklace string, it will print the number per necklace
"""

if len(sys.argv) < 2:
    print(help)
    exit()

file = sys.argv[1]

with open(file) as f:
    neckls = f.readlines()
    for n in neckls:
        neckl = Necklace(n)
        k = 0
        for i in range(len(neckl.necklstr)):
            left = neckl.necklstr[:i]
            right = neckl.necklstr[i:]
            ki = len(set(left).intersection(set(right)))
            k = ki if ki > k else k
        print(k)
