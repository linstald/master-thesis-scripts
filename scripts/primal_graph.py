import sys
import networkx as nx
import itertools as it
import string

help = """
# this file can be used to analyse the primal graph of a solved ILP instance
# it will take the ILP problem formulation as a file and generate the primal graph
# lists the treewidth using `nx.algorithms.approximation.treewidth.treewidth_min_degree`
# to get a problem formulation use `solve_lp.getComponentCut` setting `write_graphs` to `True`
# sys.argv[1] specifies the file holding the ILP problem formulation. 
"""

if len(sys.argv) < 2:
    print(help)
    exit()


file = sys.argv[1]
constraints = []  # each constraint consists of a list of variables
variables = set()  # all the variables
VAR_ALPHABET = string.ascii_letters + string.digits + "_"
with open(file) as f:
    # read from "subject to"
    line = f.readline()
    while line:
        if line == "Subject To\n":
            break
        line = f.readline()
    # read until "bounds"
    line = f.readline()
    while line:
        if line == "Bounds\n":
            break
        # this is a constraint
        line = line[line.index(":") + 1 : line.index("=")]
        toremove = [x for x in line if x not in VAR_ALPHABET]
        for x in toremove:
            line = line.replace(x, " ")
        line = line.strip()
        vs = line.split()
        constraints.append(vs)
        for v in vs:
            variables.add(v)
        line = f.readline()


G = nx.Graph()
G.add_nodes_from(variables)

for c in constraints:
    if len(c) <= 1:
        continue
    edges = it.combinations(c, 2)
    G.add_edges_from(edges)

nx.nx_pydot.write_dot(G, "primal.dot")
print(nx.algorithms.approximation.treewidth.treewidth_min_degree(G))
