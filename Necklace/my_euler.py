import random

# This file is only used to generate random irreducible necklaces.
# We do this by using a random euler path.
# Since networkx is deterministic, this is our own implementation of an euler walk.


def arbitrary_element(something):
    return random.choice(list(something))


# The following functions are directly from the networkx module
# we have them here to implement a random euler path function
# which just needs another `arbitrary_element` function

# Source: https://networkx.org/documentation/stable/_modules/networkx/algorithms/euler.html


def _simplegraph_eulerian_circuit(G, source):
    degree = G.degree
    edges = G.edges
    vertex_stack = [source]
    last_vertex = None
    while vertex_stack:
        current_vertex = vertex_stack[-1]
        if degree(current_vertex) == 0:
            if last_vertex is not None:
                yield (last_vertex, current_vertex)
            last_vertex = current_vertex
            vertex_stack.pop()
        else:
            _, next_vertex = arbitrary_element(edges(current_vertex))
            vertex_stack.append(next_vertex)
            G.remove_edge(current_vertex, next_vertex)


def eulerian_path(G, source=None, keys=False):
    """Return an iterator over the edges of an Eulerian path in `G`.

    Parameters
    ----------
    G : NetworkX Graph
        The graph in which to look for an eulerian path.
    source : node or None (default: None)
        The node at which to start the search. None means search over all
        starting nodes.
    keys : Bool (default: False)
        Indicates whether to yield edge 3-tuples (u, v, edge_key).
        The default yields edge 2-tuples

    Yields
    ------
    Edge tuples along the eulerian path.

    Warning: If `source` provided is not the start node of an Euler path
    will raise error even if an Euler Path exists.
    """
    G = G.copy()
    if source is None:
        source = [v for v in G if G.degree(v) % 2 != 0][0]
    yield from reversed([(v, u) for u, v in _simplegraph_eulerian_circuit(G, source)])
