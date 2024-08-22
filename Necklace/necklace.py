import itertools as it
import string
import random
import networkx as nx
import Necklace.solve_lp as solve_lp
import Necklace.my_euler as my_euler
from sympy.combinatorics.permutations import Permutation


def indexDict(neckl):
    """
    Computes the indices of each colour in the necklace given in `neckl`.
    This can be viewed as translating a necklace in string representation to
    the representation of `n` families of point sets.

    #### Args:
    `neckl` a list (or string) representing a necklace.

    #### Returns:
    a dictionary holding each colour as a key and its indices as a list as value.
    """
    indices = {}
    for i, c in enumerate(neckl):
        if c in indices:
            indices[c].append(i)
        else:
            indices[c] = [i]
    return indices


def getWalkGraph(neckl):
    """
    Computes the walk graph for a given necklace string.
    The walk graph is a graph consisting of a vertex for each colour and
    edges between two colours for each adjacency of the two colours in the necklace.

    #### Args:
    `neckl` a list (or string) representing a necklace string.

    #### Returns:
    a `networkx.MultiGraph` object with vertices and edges as described above.
    """
    G = nx.MultiGraph()
    G.add_nodes_from(set(neckl))
    for i in range(len(neckl) - 1):
        G.add_edge(neckl[i], neckl[i + 1])
    return G


def pumpNecklace(neckl):
    """
    Given a necklace, outputs a "pumped" version of it by including the same letter multiple times.
    The multiplicity is chosen randomly for each letter between 1 (no additional letter) and `n`,
    where `n` is the number of distinct letters ocurring in `neckl` (or 8 if this number is smaller 8).

    #### Args:
    `neckl` a list (or string) representing a necklace string.

    #### Returns:
    a string or list which is a necklace with the same string representation as the original `neckl`.
    """
    n = len(set([x for x in neckl]))
    n = 8 if n < 8 else n
    pumped = []
    for c in neckl:
        mult = random.randint(1, n)
        for _ in range(mult):
            pumped.append(c)
    return "".join(pumped) if isinstance(neckl, str) else pumped


def trimNecklace(neckl):
    """
    Given a necklace `neckl` replaces multiple equal consecutive characters to only one character.
    Moreover, removes every non alphabetic character if given a string.
    Hence, this function cleans the necklace and returns its necklace string.

    #### Args:
    `neckl` a list (or string) representing a necklace string.

    #### Returns:
    a new string or list that is the trimmed instance of `neckl`.
    """
    i = 0
    trimmed = []
    while i < len(neckl):
        while i + 1 < len(neckl) and neckl[i] == neckl[i + 1]:
            i += 1
        if not isinstance(neckl, str) or neckl[i] in string.ascii_letters:
            trimmed.append(neckl[i])
        i += 1
    return "".join(trimmed) if isinstance(neckl, str) else trimmed


def getTypeIndex(neckl):
    """
    Given a necklace, returns a dictionary mapping each type (read: letter) to its index as it appears
    in order of the necklace. This index determines the order of the colours within the necklace.

    #### Args:
    `neckl` a list (or string) representing a necklace string.

    #### Returns:
    a dictionary having the letters of `neckl` as keys and mapping to `int`
    """
    nextIndex = 0
    typeIndex = {}
    for c in neckl:
        if c not in typeIndex:
            typeIndex[c] = nextIndex
            nextIndex += 1
    return typeIndex


def getComponentDict(neckl):
    """
    Computes the bead indices in each component. The components are indexed as they appear in the the necklace string
    and the bead indices are indexed as they appear in the necklace. So the first component corresponds to the first
    letter in the necklace string. The `componentDict` will then hold the indices from the necklace of the beads
    within this first necklace.

    #### Example:
    `neckl = "aaabbccc"`
    Then `componentDict[0]` corresponds to the three 'a's: `componentDict[0] = [0,1,2]`

    #### Args:
    `neckl` a list (or string) representing a necklace string.

    #### Returns:
    a dictionary holding the indices of the necklace string as keys and a list of indices from the necklace as value.
    """
    index = 0
    i = 0
    components = {}
    while i < len(neckl):
        components[index] = []
        prev = neckl[i]
        while i < len(neckl) and neckl[i] == prev:
            components[index].append(i)
            prev = neckl[i]
            i += 1
        index += 1
    return components


def getRandomIrreducible(n: int):
    """
    Computes a random irreducible necklace with `n` colours.

    #### Args:
    `n` an integer specifying the number of colours in the necklace.

    #### Returns:
    a string or list which is a random irreducible necklace string with `n` colours.
    """
    # prepare irreducible walk graph
    N = nx.Graph()
    N.add_nodes_from(range(n))
    N.add_edges_from([(i, (i + 1) % n) for i in range(n)])
    N.add_edges_from([(2 * i - 1, (2 * i + 1) % n) for i in range(1, (n + 1) // 2)])
    # get a random euler path (we use our own implementation, as networkx is deterministic)
    eulerPath = []
    for u, v in my_euler.eulerian_path(N):
        # we need to store the first vertex
        if len(eulerPath) == 0:
            eulerPath.append(u)
        # and every second vertex of the edges
        eulerPath.append(v)
    # map the nodes from the graph to letters / types
    letter = "a" if n <= 26 else 1
    letterDict = {}
    for u in eulerPath:
        if u not in letterDict:
            letterDict[u] = letter
            letter = chr(ord(letter) + 1) if n <= 26 else letter + 1
    lettered = [letterDict[u] for u in eulerPath]
    return "".join(lettered) if n <= 26 else lettered


class Necklace:

    def __init__(self, neckl):
        """
        Constructs a `Necklace` given a sequence of beads of different colours.
        Each type of bead is represented by a different letter from the alphabet and
        each character from `neckl` represents an individual bead where the type is
        given by the letter. `neckl` can be a list or a string, where for strings only letters
        from the alphabet are considered valid beads. For lists any type works but integers (read:
        colour indices) are suggested. As an example consider
        ```
        neckl = "aaabbcdcbbaa"
        ```
        There are four types of beads, namely `a`,`b`,`c`,`d`.
        The necklace consists of 3 beads of type `a` followed by 2 beads of type `b` and so on.
        An equivalent necklace is obtained by:
        ```
        neckl = [0,0,0,1,1,2,3,2,1,1,0,0]
        ```

        #### Args:
        `neckl` a string or list representing a necklace.
        """
        # the necklace, stored as a list for convenience
        if isinstance(neckl, str):
            self.necklace = [x for x in neckl if x in string.ascii_letters]
        else:
            self.necklace = neckl.copy()
        # the string representation of the necklace,
        # where consecutive beads of the same type are represented by only one letter
        self.necklstr = trimNecklace(neckl)
        # the number of types of beads
        self.n = len(set(self.necklace))
        # the size of the necklace
        self.N = len(self.necklace)
        # get the index of each type as they appear in order
        self.typeIndex = getTypeIndex(self.necklace)
        # a dictionary containing the indices of occurances of gems for each bead
        self.beadDict = indexDict(self.necklace)
        # a dictionary containing the indices of occurances of beads in the necklace string.
        self.letterDict = indexDict(self.necklstr)
        # a dictionary containing the indices of beads per component
        self.componentDict = getComponentDict(self.necklace)
        # the length of the necklace string
        self.size = len(self.necklstr)
        # the walk graph obtained from the necklace string
        self.graph = getWalkGraph(self.necklstr)

    def __str__(self):
        return (
            "".join(self.necklace)
            if isinstance(self.necklace[0], str)
            else str(self.necklace)
        )

    def checkSep(self):
        """
        checks if the necklace is `n`-separable.
        Currently, this is done in brute force (as we assume `n` is small).
        That is, the max-cut from the walk graph is determined by iterating over all cuts.

        #### Returns:
        `True` if necklace is `n`-separable and `False` otherwise.
        """
        vs = self.graph.nodes
        subsets = it.chain.from_iterable(
            [it.combinations(vs, r) for r in range(len(vs) + 1)]
        )
        max_cut = 0
        for S in subsets:
            mc = nx.cut_size(self.graph, S)
            if mc > max_cut:
                max_cut = mc
        return max_cut <= self.n

    def computeSep(self):
        """
        Computes the separability of the necklace, i.e. the max-cut of the walk graph.
        Currently, this is done in brute force (as we assume `n` is small).
        That is, the max-cut from the walk graph is determined by iterating over all cuts.

        #### Returns:
        the max-cut of the walk graph.
        """
        vs = self.graph.nodes
        subsets = it.chain.from_iterable(
            [it.combinations(vs, r) for r in range(len(vs) + 1)]
        )
        max_cut = 0
        for S in subsets:
            mc = nx.cut_size(self.graph, S)
            if mc > max_cut:
                max_cut = mc
        return max_cut

    def hasOnlyBicomponents(self):
        """
        Checks if each colour appears at most 2 times in the necklace.

        #### Returns:
        `True` if there are only colours with at most 2 components and `False` otherwise.
        """
        letters = self.letterDict
        hasTricolor = False
        for c in letters:
            hasTricolor |= len(letters[c]) > 2
        return not hasTricolor

    def hasNeighbouringIntervals(self):
        """
        Checks if the necklace has two neighbouring interval.
        An interval is a colour that only occurs once in the necklace.

        #### Returns:
        `True` if there are two neighbouring intervals and `False` otherwise.
        """
        letters = self.letterDict
        hasIntervals = False
        for i in range(self.size - 1):
            hasIntervals |= (
                len(letters[self.necklstr[i]]) == 1
                and len(letters[self.necklstr[i + 1]]) == 1
            )
        return hasIntervals

    def hasInsideEmbracing(self):
        """
        Checks whether there is a colour that is inside-embracing.
        A bicomponent is inside embracing if its two components are the first and last component of the necklace.
        Moreover, an interval is inside embracing if it is the first or the last component.

        #### Returns:
        `True` if there is an inside-embracing colour and `False` otherwise.
        """
        return (
            self.necklstr[0] == self.necklstr[-1]
            or len(self.letterDict[self.necklstr[0]]) == 1
            or len(self.letterDict[self.necklstr[-1]]) == 1
        )

    def count(self, cut: list | set, type):
        """
        Define a count function that given a cut and a type counts
        the number of beads from that type on the side determined by the cut permutation.

        #### Args:
        `cut` a list or set representing a cut by holding the indices of the cut beads.</br>
        `type` a letter (or number) corresponding to the type to count

        #### Returns:
        A number corresponding to the number of beads of type `type` on the positive side of the cut `cut`.
        """
        start = self.getCutParity(cut)
        total = 0
        side = True
        cutSet = set(cut)
        for i, c in enumerate(self.necklace):
            if i in cutSet:
                side = not side
                if c == type:
                    total += 1
                continue
            if c == type and side == start:
                total += 1
        return total

    def augmentCut(self, partialCut: list, component: int, alphaComponent: int):
        """
        Tries to augment a given `partialCut`, which is a cut of `n-1` cut indices except for the
        colour of the given `component`, such that the cut of the remaining colour lies in that component.
        The cut is considered valid if there are `alphaComponent` beads of the remaining colour on the positive side.

        #### Args:
        `partialCut` a list representing a partial cut by holding indicies of the cut beads for all colour except the colour of `component`.</br>
        `component` an index of a component which is to be augmented. </br>
        `alphaComponent` a number corresponding to the target number of beads of the colour of `component` on the positive side.

        #### Returns:
        the index of the bead where to place the cut of the remaining colour or -1 if no such bead exists.
        """
        compBeads = self.componentDict[component]
        compType = self.necklstr[component]
        for bead in compBeads:
            if self.count(partialCut + [bead], compType) == alphaComponent:
                return bead
        return -1

    def findAlphaCut(self, alpha: dict = None):
        """
        Computes an alpha and a negalpha cut in the necklace given the alpha vector `alpha`.
        This function is an implementation of the recursive algorithm, using the case distinction.
        Ultimatively, this may lead to an irreducible necklace, where brute force is applied.

        The alpha cut is the (unique) colourful set such that on the positive side, which is
        determined by the cut permutation, there are exactly `alpha[c]` beads for each type `c`.
        Similarly, the negalpha cut is the cut with `|beadDict[c]| - alpha[c] + 1`beads on the positive side.

        #### Args:
        `alpha` is a dictionary that needs to have a value for each type of bead appearing in the necklace. The value must be between 1 and the total number of beads of that type. If `alpha` is not provided, the constant dictionary `{c: 1 for c in self.beadDict}` is used. </br>

        #### Returns:
        a tuple of lists, the first element being an alpha cut and the second one a negalpha cut.
        """
        ####################################
        # input validation and definitions #
        ####################################
        # an empty neclace has an empty cut
        if self.n == 0:
            return ([], [])
        # compute the number of beads and letters per type
        beads = {x: len(self.beadDict[x]) for x in self.beadDict}
        letters = {x: len(self.letterDict[x]) for x in self.letterDict}
        # check validity of alpha
        if alpha == None:
            alpha = {c: 1 for c in beads}
        for c in beads:
            if c not in alpha:
                raise ValueError(
                    f"Invalid arguments: no alpha value supplied for type: {c}"
                )
            if alpha[c] < 1 or alpha[c] > beads[c]:
                raise ValueError(
                    f"Invalid arguments: alpha[{c}] must be within [1, {beads[c]}], but is: {alpha[c]}"
                )

        # compute alpha bar
        negalpha = {c: beads[c] - alpha[c] + 1 for c in beads}

        #############################################
        # 1. case: check for neighbouring intervals #
        #############################################
        neighIntervals = []
        for i in range(self.size - 1):
            if letters[self.necklstr[i]] == 1 and letters[self.necklstr[i + 1]] == 1:
                neighIntervals = [self.necklstr[i], self.necklstr[i + 1], i]
                break
        # neighIntervals is a list of three elements:
        # the first two elements are the types/letters of the neighbouring intervals
        # the third element is the index of the first of these intervals within the necklace string
        # that is: the third element is the index of the component of the first interval
        # thus neighIntervals[2]+1 is the index of the component of the second interval
        if len(neighIntervals) != 0:
            # there are neighbouring intervals, solve recursively and find alpha cuts in intervals
            smaller = []
            firstIntervalInd = -1
            lastIntervalInd = -1
            # compute the smaller instance, and keep track where the intervals are exactly
            for i, c in enumerate(self.necklace):
                if c == neighIntervals[0] and firstIntervalInd == -1:
                    # we keep track of the first index of the first interval
                    firstIntervalInd = i
                if c == neighIntervals[1]:
                    # we keep track of the last index of bead of the second interval
                    lastIntervalInd = i
                if c != neighIntervals[0] and c != neighIntervals[1]:
                    # c is not part of the neighbouring intervals, so we add it to the smaller necklace
                    smaller.append(c)
            # solve recursively
            necklSmaller = Necklace(smaller)
            alphaCut, negAlphaCut = necklSmaller.findAlphaCut(alpha)
            # correct the indices
            indexRange = lastIntervalInd - firstIntervalInd + 1
            # this correction is necessary because when we remove neighbouring intervals
            # the indices of cut beads within the smaller indices that come after the two
            # removed intervals are not correct in the original necklace
            # we thus have to add the number of removed beads for these cut beads
            alphaCut = [
                i + (indexRange if i >= firstIntervalInd else 0) for i in alphaCut
            ]
            negAlphaCut = [
                i + (indexRange if i >= firstIntervalInd else 0) for i in negAlphaCut
            ]
            # augment both cuts, first in first interval
            # note that since we order the colours always as they appear first in the necklace
            # augmenting the cuts for neighbouring intervals cannot change the cut parity
            # we can just augment the smaller alpha cut to an alpha cut in the original necklace

            firstAlpha = alphaCut + [lastIntervalInd]
            firstNegAlpha = negAlphaCut + [lastIntervalInd]
            # note: the added index is just there to make a valid partial cut
            # it is just a dummy index of the other interval
            cutFirstAlpha = self.augmentCut(
                firstAlpha, neighIntervals[2], alpha[neighIntervals[0]]
            )
            cutFirstNegAlpha = self.augmentCut(
                firstNegAlpha, neighIntervals[2], negalpha[neighIntervals[0]]
            )
            assert cutFirstAlpha != -1 and cutFirstNegAlpha != -1
            # now in second interval
            secondAlpha = alphaCut + [cutFirstAlpha]
            secondNegAlpha = negAlphaCut + [cutFirstNegAlpha]
            # here we can directly add the found cut beads for the other intervals
            cutSecondAlpha = self.augmentCut(
                secondAlpha, neighIntervals[2] + 1, alpha[neighIntervals[1]]
            )
            cutSecondNegAlpha = self.augmentCut(
                secondNegAlpha, neighIntervals[2] + 1, negalpha[neighIntervals[1]]
            )
            assert cutSecondAlpha != -1 and cutSecondNegAlpha != -1
            # construct final cuts
            alphaCut.append(cutFirstAlpha)
            alphaCut.append(cutSecondAlpha)
            negAlphaCut.append(cutFirstNegAlpha)
            negAlphaCut.append(cutSecondNegAlpha)
            return (sorted(alphaCut), sorted(negAlphaCut))

        ##########################################################
        # 2. case: check for colours with more than 2 components #
        ##########################################################

        # determine colours with more than 2 components
        # we directly store the component indices for each of these colours
        col3 = {
            c: self.letterDict[c]
            for c in self.letterDict
            if len(self.letterDict[c]) > 2
        }
        if len(col3) > 0:
            # generate all combinations of components
            # for each combination its components will then be treated as fixed
            # meaning that the cut will go through that component
            compComb = [[]]
            for c in col3:
                newCompComb = []
                # at this point combComb holds all combinations of components
                # of colours in col3 up to (excluding) c
                # for each of these combinations, we add all indices of components
                # of the current colour c
                for comp in col3[c]:
                    for comb in compComb:
                        newComb = comb.copy()
                        newComb.append(comp)
                        newCompComb.append(newComb)
                compComb = newCompComb

            alphaCut = []
            negAlphaCut = []

            # all component indices from colours in col3
            # this is used to get a set of components to remove for each iteration
            # by subtracting the components from the current iteration
            allComp = set(j for i in col3.values() for j in i)

            # for each combination, compute recursive instance and find alpha cut
            for comb in compComb:
                # we need to remove the beads that are not in the current combination
                beadsIndicesToRemove = set(
                    it.chain.from_iterable(
                        self.componentDict[c] for c in allComp.difference(set(comb))
                    )
                )
                # compute the smaller instance
                smaller = []
                for i, b in enumerate(self.necklace):
                    if i not in beadsIndicesToRemove:
                        smaller.append(b)

                # use a modified alpha vector, since it does not matter what the alpha is for the fixed components
                alphaSmaller = {c: alpha[c] if c not in col3 else 1 for c in alpha}

                # solve the smaller instance
                necklSmaller = Necklace(smaller)
                smallAlpha, smallNegAlpha = necklSmaller.findAlphaCut(alphaSmaller)
                smallAlphaParity = necklSmaller.getCutParity(smallAlpha)
                smallNegAlphaParity = necklSmaller.getCutParity(smallNegAlpha)

                # we need to fix the smaller solutions similar to the case when removing neigh. intervals
                # when we remove components, the cut beads in the smaller necklace will have wrong indices
                sortedBeads = sorted(beadsIndicesToRemove)
                beadIndex = 0
                fixedAlpha = []
                inc = 0
                # for each removed bead, we need to increment all the cut indices that are larger by one
                for c in smallAlpha:
                    while (
                        beadIndex < len(sortedBeads)
                        and sortedBeads[beadIndex] <= c + inc
                    ):
                        # this while loop iterates over removed beads
                        # coming before the current cut bead
                        # and increases the increment accordingly
                        inc += 1
                        beadIndex += 1
                    # the cut bead is now increased to be correct in the original necklace
                    fixedAlpha.append(c + inc)

                # repeat for negAlpha cut as well
                beadIndex = 0
                fixedNegAlpha = []
                inc = 0
                for c in smallNegAlpha:
                    while (
                        beadIndex < len(sortedBeads)
                        and sortedBeads[beadIndex] <= c + inc
                    ):
                        inc += 1
                        beadIndex += 1
                    fixedNegAlpha.append(c + inc)

                # at this point fixedAlpha and fixedNegAlpha are valid cuts in this necklace
                # now we try to find the alpha or the negAlpha cut by shifting around in the components
                # this is the most tricky part, as there are a couple of cases that could occur
                # Note: the tricky thing comes from our implementation of ordering the colours
                # as now removing all but one component may swap the cut parity (e.g. when the first component is remove)
                cuts = {
                    c: [-1, -1, -1, -1] for c in col3
                }  # 0: alphatoalpha, 1: alphatonegalpha, 2: negalphatoalpha, 3: negalphatonegalpha
                # there are four cases, for each colour in col3
                # we determine which (if any) case can be used, if yes the corresponding cut bead is stored in cuts[col][case]
                # then the combination is valid if there is a case that works for all colours
                for col in col3:
                    # we need to check if the alphaCut can be augmented to a alphaCut or negAlphaCut
                    # and similar for the negAlphaCut. This is because the permutation parity may flip.
                    # hence there are four cases: turning the alpha to alpha cut, turning the alpha to negalpha cut,
                    # turning the negalpha to alpha cut, turning the negalpha to negalpha cut

                    # find and remove the cut point in that colour
                    colAlpha = [x for x in fixedAlpha if self.necklace[x] != col]
                    colNegAlpha = [x for x in fixedNegAlpha if self.necklace[x] != col]

                    # for each index in the fixed component, check if one of the 4 cases appllies
                    colComp = [c for c in comb if self.necklstr[c] == col][0]
                    for b in self.componentDict[colComp]:
                        # note: since we need to know if the parity flips we cannot just use `augmentCut`
                        colAlphaTry = colAlpha + [b]
                        colNegAlphaTry = colNegAlpha + [b]
                        alphaTryParity = self.getCutParity(colAlphaTry)
                        negAlphaTryParity = self.getCutParity(colNegAlphaTry)
                        cntAlpha = self.count(colAlphaTry, col)
                        cntNegAlpha = self.count(colNegAlphaTry, col)
                        # 1. case: turning alphacut to alphacut if parity and count matches
                        if (
                            cntAlpha == alpha[col]
                            and alphaTryParity == smallAlphaParity
                        ):
                            cuts[col][0] = b
                        # 2. case: turning alphacut to negalpha cut if neg count matches but not parity matches
                        if (
                            cntAlpha == negalpha[col]
                            and alphaTryParity != smallAlphaParity
                        ):
                            cuts[col][1] = b
                        # 3. case: turning negalphacut to alphacut if count matches but not parity matches
                        if (
                            cntNegAlpha == alpha[col]
                            and negAlphaTryParity != smallNegAlphaParity
                        ):
                            cuts[col][2] = b
                        # 4. case: turning negalphacut to negalphacut if neg count matches and parity matches
                        if (
                            cntNegAlpha == negalpha[col]
                            and negAlphaTryParity == smallNegAlphaParity
                        ):
                            cuts[col][3] = b

                # the cuts need to be calculated according to the four cases, but consistently for all the fixed colours
                # hence, the current combination is valid if one of the four cases applies to all colours
                # if all colours have an alpha/negAlpha cut, we found the alpha/negAlpha cut
                allAlphaToAlpha = [cuts[col][0] for col in cuts].count(-1) == 0
                allAlphaToNegAlpha = [cuts[col][1] for col in cuts].count(-1) == 0
                allNegAlphaToAlpha = [cuts[col][2] for col in cuts].count(-1) == 0
                allNegAlphaToNegAlpha = [cuts[col][3] for col in cuts].count(-1) == 0
                # compute the cuts
                if allAlphaToAlpha:
                    alphaCut = [
                        c for c in fixedAlpha if self.necklace[c] not in cuts
                    ] + [cuts[col][0] for col in cuts]
                if allAlphaToNegAlpha:
                    negAlphaCut = [
                        c for c in fixedAlpha if self.necklace[c] not in cuts
                    ] + [cuts[col][1] for col in cuts]
                if allNegAlphaToAlpha:
                    alphaCut = [
                        c for c in fixedNegAlpha if self.necklace[c] not in cuts
                    ] + [cuts[col][2] for col in cuts]
                if allNegAlphaToNegAlpha:
                    negAlphaCut = [
                        c for c in fixedNegAlpha if self.necklace[c] not in cuts
                    ] + [cuts[col][3] for col in cuts]

            # having calculated the alpha cuts for the fixed necklace for each combination there
            # better be a valid alphacut and negalphacut now, otherwise some promise is violated.
            assert len(alphaCut) != 0
            assert len(negAlphaCut) != 0

            return sorted(alphaCut), sorted(negAlphaCut)

        ########################################
        # 3. case: check for embracing colours #
        ########################################

        # 3a) the last component is an interval
        if len(self.letterDict[self.necklstr[-1]]) == 1:
            # solve on everything before and add the right cut in the interval
            lastInterval = self.necklstr[-1]
            smaller = [c for c in self.necklace if c != lastInterval]
            necklSmaller = Necklace(smaller)
            smallAlphaCut, smallNegAlphaCut = necklSmaller.findAlphaCut(alpha)
            # now augment the cut, we know that the cut parity does not change, so alpha to alpha, negalpha to negalpha
            alphaCut = []
            negAlphaCut = []
            bAlpha = self.augmentCut(smallAlphaCut, self.size - 1, alpha[lastInterval])
            bNegAlpha = self.augmentCut(
                smallNegAlphaCut, self.size - 1, negalpha[lastInterval]
            )
            # this better works, otherwise promises are violated
            assert bAlpha != -1
            assert bNegAlpha != -1

            alphaCut = smallAlphaCut + [bAlpha]
            negAlphaCut = smallNegAlphaCut + [bNegAlpha]
            return sorted(alphaCut), sorted(negAlphaCut)

        # 3b) the first component is an interval
        if len(self.letterDict[self.necklstr[0]]) == 1:
            # solve on everything after and add the right cut in the interval
            firstInterval = self.necklstr[0]
            smaller = [c for c in self.necklace if c != firstInterval]
            necklSmaller = Necklace(smaller)
            smallAlphaCut, smallNegAlphaCut = necklSmaller.findAlphaCut(alpha)
            # we need to fix the cuts, by adding the number of beads in the first components to each cut index
            smallAlphaCut = [c + beads[firstInterval] for c in smallAlphaCut]
            smallNegAlphaCut = [c + beads[firstInterval] for c in smallNegAlphaCut]
            alphaCut = []
            negAlphaCut = []
            # we augment, but since parity is switched we augment negAlpha to alpha and alpha to negAlpha
            bAlpha = self.augmentCut(smallNegAlphaCut, 0, alpha[firstInterval])
            bNegAlpha = self.augmentCut(smallAlphaCut, 0, negalpha[firstInterval])

            # this better works, otherwise promises are violated
            assert bAlpha != -1
            assert bNegAlpha != -1

            alphaCut = smallNegAlphaCut + [bAlpha]
            negAlphaCut = smallAlphaCut + [bNegAlpha]
            return sorted(alphaCut), sorted(negAlphaCut)

        # 3c) the first and last component are from the same colour (which cannot have more components)
        if self.necklstr[0] == self.necklstr[-1]:
            # solve everything inbetween and check if cut needs to be inserted in first or last component
            firstLast = self.necklstr[0]
            smaller = [c for c in self.necklace if c != firstLast]
            necklSmaller = Necklace(smaller)
            smallAlphaCut, smallNegAlphaCut = necklSmaller.findAlphaCut(alpha)
            # we need to fix the cuts by adding the number of beads in the first component to each cut index
            smallAlphaCut = [c + len(self.componentDict[0]) for c in smallAlphaCut]
            smallNegAlphaCut = [
                c + len(self.componentDict[0]) for c in smallNegAlphaCut
            ]
            alphaCut = []
            negAlphaCut = []
            # check if the negalpha cut can be augmented to an alpha cut
            # and the alpha to an negalpha, by trying the beads in the first components
            # since the parity of all other cuts switches, but the cut permutation remains
            # at its parity we can only turn alpha to negalpha or negalpha to alpha cuts
            bAlpha0 = self.augmentCut(smallNegAlphaCut, 0, alpha[firstLast])
            bNegAlpha0 = self.augmentCut(smallAlphaCut, 0, negalpha[firstLast])
            if bAlpha0 != -1:
                alphaCut = smallNegAlphaCut + [bAlpha0]
            if bNegAlpha0 != -1:
                negAlphaCut = smallAlphaCut + [bNegAlpha0]
            # if not both cuts are already found, we need to try the beads in the last component
            # here the parity of the separator points does not change, however the cut permutation
            # may change the parity. In particular, the permutation changes parity if n is even
            # and remains at the same parity if n is odd. If the permutation parity changes we need
            # to turn an alpha to an negalpha and vice versa and otherwise an alpha to an alpha and vice versa.
            if self.n % 2 == 0:
                smallAlphaCut, smallNegAlphaCut = smallNegAlphaCut, smallAlphaCut
            bAlpha1 = self.augmentCut(smallAlphaCut, self.size - 1, alpha[firstLast])
            bNegAlpha1 = self.augmentCut(
                smallNegAlphaCut, self.size - 1, negalpha[firstLast]
            )

            if bAlpha1 != -1:
                alphaCut = smallAlphaCut + [bAlpha1]
            if bNegAlpha1 != -1:
                negAlphaCut = smallNegAlphaCut + [bNegAlpha1]

            # this better works, otherwise promises are violated
            assert bAlpha0 != -1 or bAlpha1 != -1
            assert bNegAlpha0 != -1 or bNegAlpha1 != 1

            return sorted(alphaCut), sorted(negAlphaCut)

        ###########################################
        # 4. case: irreducible, apply brute force #
        ###########################################

        # generate all possible cuts in the necklace string
        # and check which such cut can be extended to an alpha cut

        # if n large enough we can use the ILP solver
        if self.n > 5:
            # this will actually return only the valid component cuts (of which there are only 2)
            # hence everything after runs in polynomial time
            allAlphaCuts = solve_lp.getComponentCut(self, alpha)
            allNegAlphaCuts = solve_lp.getComponentCut(self, negalpha)
            allCuts = allAlphaCuts + allNegAlphaCuts
        else:
            # if n <= 5 use brute force and enumerate all component cuts
            allCuts = self.generateAllComponentCuts()
        n = self.n
        # for each cut, check if it can be moved within its fixed components to get an alpha cut
        alphaCut = []
        negAlphaCut = []
        for cut in allCuts:
            fullCutAlpha = []
            fullCutNegAlpha = []
            for c in cut:
                # check if it can be moved
                partialCut = [self.componentDict[nc][0] for nc in cut if nc != c]
                beadAlpha = self.augmentCut(partialCut, c, alpha[self.necklstr[c]])
                beadNegAlpha = self.augmentCut(
                    partialCut, c, negalpha[self.necklstr[c]]
                )
                if beadAlpha != -1:
                    fullCutAlpha.append(beadAlpha)
                if beadNegAlpha != -1:
                    fullCutNegAlpha.append(beadNegAlpha)
            # if all cuts can be moved to get an alpha cut, store it
            if len(fullCutAlpha) == n:
                alphaCut = fullCutAlpha
            if len(fullCutNegAlpha) == n:
                negAlphaCut = fullCutNegAlpha

        return sorted(alphaCut), sorted(negAlphaCut)

    def generateAllComponentCuts(self) -> list[list]:
        """
        Computes a list of all possible cuts with respect to the components.
        That is, for all possible combinations of components the corresponding cut is computed.

        #### Returns:
        a list containing lists of cuts.
        """
        # identify possible indices per type
        n = self.n
        allTypes = self.typeIndex.keys()
        allCuts = []
        allFixed = []
        queue = [{}]
        while len(queue) > 0:
            fixed = queue.pop()
            # if all keys are already fixed, append to cuts list (if not already in)
            if len(fixed.keys()) == n:
                cut = sorted([c for c in fixed.values()])
                if cut not in allCuts:
                    allCuts.append(cut)
                continue
            # for all unfixed keys, iterate over all possible indices and push a new fixed dict
            # at this point fixed is a dictionary using a partial set of type of beads
            # for each such type, a component index is chosen to be a cut (stored in the fixed dict)
            # now we go over all types not already in fixed and add a new partial cut by adding this type
            # and all combinations of components of that type
            for key in allTypes:
                if key not in fixed:
                    for index in self.letterDict[key]:
                        newFixed = fixed.copy()
                        newFixed[key] = index
                        # append only if not already used
                        if newFixed not in allFixed:
                            queue.append(newFixed)
                            allFixed.append(newFixed)
        return allCuts

    def getCutPermutation(self, cut: list | set) -> Permutation:
        """
        Computes the cut permutation for a given cut.
        The cut permutation is the permutation of the type indices obtained by
        traversing the cut beads from left to right.
        For example if we consider the necklace `aabbbaaaccddd` and the cut `[3, 5, 8, 10]`
        the cut traversed from left to right is `bacd`, and the permutation is thus `[1, 0, 2, 3]`.

        #### Args:
        `cut` a list or set representing a cut by holding the indices of the cut beads.

        #### Returns:
        a `Permutation` representing the cut permutation.
        """
        sortedCut = sorted(cut)
        perm = []
        for c in sortedCut:
            perm.append(self.typeIndex[self.necklace[c]])
        if len(set(perm)) != len(perm):
            print(cut)
            print(perm)
        if len(perm) != self.n:
            print(cut)
        return Permutation(perm)

    def getCutParity(self, cut: list | set) -> bool:
        """
        Computes the parity of the cut permutation in the given cut.

        #### Args:
        `cut` a list or set representing a cut by holding the indices of the cut beads.

        #### Returns
        `True` if the permutation is even and `False` otherwise.
        """
        return self.getCutPermutation(cut).is_even

    def displayCut(self, cut: list | set):
        """
        Pretty-prints a cut, by showing the beads strictly on the positive side on top,
        beads strictly on negative side on the bottom and cut beads in the middle.

        #### Args:
        `cut` a list or set representing a cut by holding the indices of the cut beads.
        """

        parity = self.getCutParity(cut)
        pos = ""
        neg = ""
        mid = ""
        side = True
        for i, c in enumerate(self.necklace):
            if i in cut:
                mid += f"{c} "
                pos += "  "
                neg += "  "
                side = not side
            elif side:
                pos += f"{c} "
                neg += "  "
                mid += "  "
            else:
                pos += "  "
                neg += f"{c} "
                mid += "  "
        if parity:
            print(pos)
            print(mid)
            print(neg)
        else:
            print(neg)
            print(mid)
            print(pos)

    def getColourGraph(self):
        """
        Computes the colour cut graph. The colour cut graph is the same as the walk graph,
        but each half edge is labelled with the size of the component it is incient. Thus,
        the weight of each edge (`G[u][v]["weight"]`) is a dictionary containing `u` and `v`
        as keys and the sizes of the corresponding components as values.
        Moreover, each edge gets an attribute `index` which is the index of the first component
        it corresponds to. This implies that the edge between the first and last component has
        index `size-1` where `size` is the length of the necklace.
        This `index` field can then be used to determine the traversals of a vertex:
        Order the incident edges according to `index` and then the traversals are given by the
        consecutive tuples of edges.

        Requires this necklace to be irreducible (not enforced nor checked).

        #### Returns
        a new `nx.Graph()` representing the colour graph.
        """
        G = nx.Graph()
        G.add_nodes_from(self.beadDict.keys())
        comp = self.componentDict
        for i in range(self.size - 1):
            c = self.necklstr[i]
            cn = self.necklstr[i + 1]
            G.add_edge(
                c,
                cn,
                weight={c: len(comp[i]), cn: len(comp[i + 1])},
                label=f"{c}-{len(comp[i])}, {cn}-{len(comp[i + 1])}",
                index=i,
            )
        first, last = [u for u in G.nodes if len(G[u]) == 3]
        if self.n % 2 == 0:
            # if n even we add an edge between first and last component
            G.add_edge(
                first,
                last,
                weight={first: len(comp[0]), last: len(comp[self.size - 1])},
                label=f"{first}-{len(comp[0])}, {last}-{len(comp[self.size - 1])}",
                index=self.size - 1,
            )
        else:
            # if n odd, we add a new 'infinity' vertex capturing the colour change from the parts
            # to the left and to the right of the necklace
            G.add_node("inf")
            G.add_edge(
                "inf",
                first,
                weight={"inf": 0, first: len(comp[0])},
                label=f"inf-0, {first}-{len(comp[0])}",
                index=self.size - 1,
            )
            G.add_edge(
                last,
                "inf",
                weight={"inf": 0, last: len(comp[self.size - 1])},
                label=f"{last}-{len(comp[self.size-1])}, inf-0",
                index=self.size,
            )
        return G


if __name__ == "__main__":
    # timing: n=26 done in 1.8s (using ILP), n=25 not done after 64m (using brute force)
    # n=10'000 done in ~15min
    neckl = Necklace(pumpNecklace("abcdeafghfiace"))
    print(neckl)
    alpha = {c: random.randint(1, len(neckl.beadDict[c])) for c in neckl.beadDict}
    print(alpha)
    alphaCut, nalphaCut = neckl.findAlphaCut(alpha)
    print(alphaCut, nalphaCut)
    neckl.displayCut(alphaCut)
