import os
import sys

sys.path.append(os.path.abspath(os.path.join(__file__, os.pardir, os.pardir)))
import networkx as nx
from Necklace.necklace import *

help = """
# this file was used to write and analyse walk graphs.
# It either reads a file holding necklaces and writing the walk graphs of these necklaces to an output directory.
# Or, it checks/analyses isomorphisms of walk graphs in a directory.
# Isomorphism analysis has two modes: checking and computing.
# checking isomorphisms, means reading a walk graph from a file and identifiying graphs not isomorphic to this graph in a directory.
# computing isomorphisms, means iterating over walk graphs in directory and computing isomorphism classes in separate direcotries
# sys.argv[1] specifies the file either containing necklaces or a walk graph
# sys.argv[2] specifies the output directory where to write graphs or the input directory to locate graphs
# sys.argv[3] is used as a flag: if sys.argv[3] is present isomorphism check/analysis is performed, otherwise graphs are written
# the isomorpism mode is determined whether sys.argv[1] is a valid file, if yes checking isomorphisms is performed, otherwise computing.
"""


def write(file, outdir):
    name = file.replace(".txt", "").split("/")[-1]
    os.makedirs(outdir, exist_ok=True)
    with open(file) as f:
        ns = f.readlines()
        for n in ns:
            neckl = Necklace(n)
            nx.nx_pydot.write_dot(
                neckl.graph, os.path.join(outdir, f"{name}_{neckl}.dot")
            )


def check_iso(file, indir):
    if not os.path.exists(file):
        compute_iso(indir)
        return
    G = nx.nx_pydot.read_dot(file)
    for x in os.listdir(indir):
        if x.endswith(".dot"):
            Gt = nx.nx_pydot.read_dot(os.path.join(indir, x))
            if not nx.is_isomorphic(G, Gt):
                print(x)


def compute_iso(indir):
    isos = {}
    newkey = 0
    files = [x for x in os.listdir(indir) if x.endswith(".dot")]
    for x in files:
        if x in isos:
            continue
        isos[x] = newkey
        xpath = os.path.join(indir, x)
        isopath = os.path.join(indir, f"iso{newkey}")
        os.makedirs(isopath, exist_ok=True)
        os.system(f"cp {xpath} {isopath}")
        G = nx.nx_pydot.read_dot(xpath)

        for y in files:
            if y in isos:
                continue
            ypath = os.path.join(indir, y)
            Gt = nx.nx_pydot.read_dot(ypath)
            if nx.is_isomorphic(G, Gt):
                isos[y] = newkey
                os.system(f"cp {ypath} {isopath}")
        newkey += 1
    print(isos)
    print({x: len([y for y in isos if isos[y] == x]) for x in isos.values()})


if len(sys.argv) < 3:
    print(help)
    exit()

file = sys.argv[1]
dir = sys.argv[2]
if len(sys.argv) > 3:
    check_iso(file, dir)
else:
    write(file, dir)
