import networkx as nx
import sys
import random
from pulp import *


def getComponentCut(neckl, alpha=None, write_graphs=False):
    """
    Solves alpha-necklace splitting for irreducible necklaces with even n.
    To do so, this function constructs the corresponding ILP for the colouring of the walk graph.
    It computes two cuts, where here a cut is a component cut.
    That is when `[c1, ..., cn]` is returned, then `ci` are indices to components, hence indices to `neckl.necklstr`.
    This component cut can then be extended (in polynomial time) to an alpha-cut.
    If no `alpha` is provided, a random alpha-vector is used.
    The parameter `write_graphs` is intended for debug purposes and will write
    two `.dot` files (`solved1.dot`, `solved2.dot`) containing the coloured graphs.

    Requires `neckl` to be an irreducible necklace.
    Moreover, `neckl.n` should be larger than 5.

    #### Args:
    `neckl` a Necklace object to solve alpha colouring using ILP </br>
    `alpha` the alpha vector to use (if not given, a random vector is used). </br>
    `write_graphs` a boolean flag indicating if the coloured colour graphs (and the ILP problem) should be written.
    Use only for debug purposes, defaults to `False`.

    #### Returns:
    a (2)-tuple of cuts. Each cut is a component cut that can be extended to an alpha-cut.
    """
    # this does only work for larger n
    assert neckl.n > 5
    G = neckl.getColourGraph()
    if alpha is None:
        alpha = {c: random.randint(1, len(neckl.beadDict[c])) for c in neckl.beadDict}
    trav = {}
    # determine traversals: sort incident edges per vertex by `index`
    for v in G.nodes:
        if len(G[v]) == 2:
            e1, e2 = sorted(G[v])
            trav[v] = [[(e1, v), (v, e2)]]
        if len(G[v]) == 4:
            e1, e2, e3, e4 = sorted(G[v], key=lambda x: G[v][x]["index"])
            if v == neckl.necklstr[0]:
                # first component is exception, as the first edge is the one with largest index
                # which is the edge newly inserted to get the colour graph
                trav[v] = [[(e4, v), (v, e1)], [(e2, v), (v, e3)]]
            else:
                trav[v] = [[(e1, v), (v, e2)], [(e3, v), (v, e4)]]
            assert (
                G.edges[trav[v][0][0]]["weight"][v]
                == G.edges[trav[v][0][1]]["weight"][v]
                and G.edges[trav[v][1][0]]["weight"][v]
                == G.edges[trav[v][1][1]]["weight"][v]
            )

    prob = LpProblem("FindAlphaColouring")
    vars = {}

    def edgeName(edge):
        sedge = sorted(edge)
        return f"{sedge[0]}_{sedge[1]}"

    # add constant objective
    prob += 0, "ZeroObjective"

    # add x_e_0, x_e_1 vars and its constraints
    for e in G.edges:
        se = edgeName(e)
        vars[f"x_{se}_r"] = LpVariable(f"x_{se}_r", cat=LpBinary)
        vars[f"x_{se}_g"] = LpVariable(f"x_{se}_g", cat=LpBinary)
        prob += vars[f"x_{se}_r"] + vars[f"x_{se}_g"] == 1, f"EdgeUniqueColour_{se}"

    # add r_v_i and g_v_i and s_v_i vars and its constraints
    for v in G.nodes:
        for i in range(len(trav[v])):
            vars[f"r_{v}_{i}"] = LpVariable(f"r_{v}_{i}", cat=LpBinary)
            vars[f"g_{v}_{i}"] = LpVariable(f"g_{v}_{i}", cat=LpBinary)
            evi0 = edgeName(trav[v][i][0])
            evi1 = edgeName(trav[v][i][1])
            # linearize r_v_i
            prob += vars[f"r_{v}_{i}"] <= vars[f"x_{evi0}_r"], f"Linearize_r_{v}_{i}_1"
            prob += vars[f"r_{v}_{i}"] <= vars[f"x_{evi1}_r"], f"Linearize_r_{v}_{i}_2"
            prob += (
                vars[f"r_{v}_{i}"] >= vars[f"x_{evi0}_r"] + vars[f"x_{evi1}_r"] - 1,
                f"Linearize_r_{v}_{i}_3",
            )
            # linearize g_v_i
            prob += vars[f"g_{v}_{i}"] <= vars[f"x_{evi0}_g"], f"Linearize_g_{v}_{i}_1"
            prob += vars[f"g_{v}_{i}"] <= vars[f"x_{evi1}_g"], f"Linearize_g_{v}_{i}_2"
            prob += (
                vars[f"g_{v}_{i}"] >= vars[f"x_{evi0}_g"] + vars[f"x_{evi1}_g"] - 1,
                f"Linearize_g_{v}_{i}_3",
            )
            # introduce s_v_i
            vars[f"s_{v}_{i}"] = LpVariable(f"s_{v}_{i}", cat=LpInteger)
            prob += (
                vars[f"s_{v}_{i}"] == vars[f"g_{v}_{i}"] + vars[f"r_{v}_{i}"],
                f"Definition_s_{v}_{i}",
            )

    # add constraints on neighbouring colours and alpha
    for v in G.nodes:
        # colour constraints
        if len(G[v]) == 4:
            # degree 4: s_v_0 + s_v_1 == 1
            prob += (
                vars[f"s_{v}_0"] + vars[f"s_{v}_1"] == 1,
                f"ColourConstraint_{v}",
            )
            # alpha constraints
            prob += (
                vars[f"r_{v}_0"] * G.edges[trav[v][0][0]]["weight"][v] + 1 <= alpha[v],
                f"AlphaConstraint_{v}_1",
            )
            prob += (
                vars[f"r_{v}_1"] * G.edges[trav[v][1][0]]["weight"][v] + 1 <= alpha[v],
                f"AlphaConstraint_{v}_2",
            )
            prob += (
                vars[f"g_{v}_0"] * G.edges[trav[v][1][0]]["weight"][v]
                + (1 - vars[f"g_{v}_0"]) * len(neckl.beadDict[v])
                >= alpha[v],
                f"AlphaConstraint_{v}_3",
            )
            prob += (
                vars[f"g_{v}_1"] * G.edges[trav[v][0][0]]["weight"][v]
                + (1 - vars[f"g_{v}_1"]) * len(neckl.beadDict[v])
                >= alpha[v],
                f"AlphaConstraint_{v}_4",
            )
        else:
            # v deg = 2: s_v_0 == 0
            prob += vars[f"s_{v}_0"] == 0, f"ColourConstraint_{v}"

    # solve first solution
    prob.solve(PULP_CBC_CMD(msg=0))

    ones = []
    zeros = []

    # extract solution and prepare exclusion constraint
    cut1 = []
    for v in G.nodes:
        if v == "inf":
            # there is no cut in the infinity vertex
            continue
        if vars[f"s_{v}_0"].varValue != 1.0:
            # cut is in first component
            cut1.append(neckl.letterDict[v][0])
        elif vars[f"s_{v}_1"].varValue != 1.0:
            # cut is in second component
            cut1.append(neckl.letterDict[v][1])
    G = G.copy()
    for e in G.edges:
        # if we write the graph, the weight dictionary should be deleted
        # as otherwise the nx_pydot module will raise an exception
        del G.edges[e]["weight"]
        se = edgeName(e)
        if vars[f"x_{se}_r"].varValue == 1.0:
            G.edges[e]["color"] = "red"
            ones.append(vars[f"x_{se}_r"])
        else:
            zeros.append(vars[f"x_{se}_r"])
        if vars[f"x_{se}_g"].varValue == 1.0:
            G.edges[e]["color"] = "green"
            ones.append(vars[f"x_{se}_g"])
        else:
            zeros.append(vars[f"x_{se}_g"])

    if write_graphs:
        nx.nx_pydot.write_dot(G, "solved1.dot")
        prob.writeLP("problem.lp")

    # use exclusion constraint as in http://yetanothermathprogrammingconsultant.blogspot.com/2011/10/integer-cuts.html
    prob += (
        lpSum(v for v in ones) - lpSum(v for v in zeros) <= len(ones) - 1,
        "ExclusionConstraint",
    )

    # solve for second solution
    prob.solve(PULP_CBC_CMD(msg=0))

    cut2 = []
    for v in G.nodes:
        if v == "inf":
            # there is no cut in the infinity vertex
            continue
        if vars[f"s_{v}_0"].varValue != 1.0:
            # cut is in first component
            cut2.append(neckl.letterDict[v][0])
        elif vars[f"s_{v}_1"].varValue != 1.0:
            # cut is in second component
            cut2.append(neckl.letterDict[v][1])

    for e in G.edges:
        se = edgeName(e)
        if vars[f"x_{se}_r"].varValue == 1.0:
            G.edges[e]["color"] = "red"
        if vars[f"x_{se}_g"].varValue == 1.0:
            G.edges[e]["color"] = "green"

    if write_graphs:
        nx.nx_pydot.write_dot(G, "solved2.dot")

    return (sorted(cut1), sorted(cut2))


if __name__ == "__main__":
    import necklace

    if len(sys.argv) > 1:
        arg = sys.argv[1]
        try:
            n = int(arg)
            neckl = necklace.Necklace(necklace.getRandomIrreducible(n))
        except:
            neckl = necklace.Necklace(necklace.pumpNecklace(sys.argv[1]))
    else:
        neckl = necklace.Necklace(necklace.getRandomIrreducible(1000))
    print(neckl)
    alpha = {c: 1 for c in neckl.beadDict}
    alphaCut, negAlphaCut = getComponentCut(neckl, alpha, write_graphs=True)
    # actually, these are not guaranteed to be the alphaCut and negAlphaCut as their name suggests
    # but it better be the case that one of them is the alpha and the other the negalpha cut
    print(alphaCut, negAlphaCut)

    # check cut
    checkNeckl = necklace.Necklace(neckl.necklstr)
    negalpha = {c: len(checkNeckl.beadDict[c]) - alpha[c] + 1 for c in alpha}
    checkStr = checkNeckl.necklstr
    pos = {c: 0 for c in checkStr}
    neg = {c: 0 for c in checkStr}
    cutcolsAlpha = set()
    cutcolsNegAlpha = set()
    parity = True
    alphaParity = checkNeckl.getCutParity(alphaCut)
    alphaSide = True
    negAlphaParity = checkNeckl.getCutParity(negAlphaCut)
    negAlphaSide = True
    for i, c in enumerate(checkStr):
        if i in alphaCut:
            alphaSide = not alphaSide
            cutcolsAlpha.add(c)
            pos[c] += 1
        else:
            if alphaSide == alphaParity:
                pos[c] += 1
        if i in negAlphaCut:
            negAlphaSide = not negAlphaSide
            cutcolsNegAlpha.add(c)
            neg[c] += 1
        else:
            if negAlphaSide == negAlphaParity:
                neg[c] += 1

    assert len(cutcolsAlpha) == len(pos.keys())
    assert len(cutcolsNegAlpha) == len(pos.keys())
    assert (pos == alpha and neg == negalpha) or (pos == negalpha and neg == alpha)
