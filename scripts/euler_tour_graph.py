import os
import sys

sys.path.append(os.path.abspath(os.path.join(__file__, os.pardir, os.pardir)))
import networkx as nx
from Necklace.necklace import Necklace

help = """
# This file was a first prototype generating the colour graph (here neamed euler tour graph)
# sys.argv[1] is a string specifying the necklace.
"""

if len(sys.argv) < 2:
    print(help)
    exit()

neckl = Necklace(sys.argv[1])

vs = {c: 0 for c in neckl.necklstr}

G = nx.Graph()
prev = None
first = None
for c in neckl.necklstr:
    newV = (c, vs[c])
    vs[c] += 1
    G.add_node(newV)
    if prev is not None:
        G.add_edge(prev, newV)
    else:
        first = newV
    prev = newV
# connect first and last
if neckl.n % 2 == 0:
    G.add_edge(prev, first)
else:
    helper = (prev[0], prev[1] + 1)
    G.add_node(helper)
    G.add_edge(first, helper)
    G.add_edge(helper, prev)

chords = [(u, v) for u in G.nodes for v in G.nodes if u[0] == v[0] and u[1] != v[1]]

G.add_edges_from(chords)
