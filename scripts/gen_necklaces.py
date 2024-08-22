import os
import sys

sys.path.append(os.path.abspath(os.path.join(__file__, os.pardir, os.pardir)))
from Necklace.necklace import *
import math
import string
from tqdm import tqdm
from multiprocessing import Process


help = """
# The generator script used to generate all the necklaces.
# sys.argv[1] specifies a 'seed', i.e., a string holding the different letters of the necklace (e.g. ABCD).
# sys.argv[2] specifies the diskpath, i.e., the path to a directory holding smaller necklaces used to augment.
# sys.argv[3] specifies the outpath, i.e., the path to a directory where to write the necklaces.
# sys.argv[4] specifies the target separability of the necklaces.
# if specified, sys.argv[5] specifies the number of processors to use to generate
"""

sep = 1


def generate(seed, sep=1, diskpath=None, pid=None):
    """
    Recursive generation algorithm for necklaces (not necessarily `n`-separable).
    However, it only generates necklaces that are within the size limit we have for
    `n`-separable necklaces (or `n-1+sep`-separable) if `sep ` is set.
    The algorithm generates all necklaces on a given input alphabet `seed`, such that in every
    necklace the occurence of the `i`-th letter from the seed only happens when every letter
    appearing earlier in the seed already occured at least once in the necklace.
    This is not a loss of generality, as we can always use any permutation on the alphabet to get any necklace.

    The algorithm is based on the following observation.
    Let `C` be a necklace on the alphabet `1,2,...,n`.
    Then the substring `S` of `C` containing everything up to (but not including) the first occurence of `n` must also be a necklace.
    In particular, `S` is a necklace on the alphabet `1,2,...,n-1`.
    Using the size limit for `n`-separable necklaces we have now only a few places where we can insert other letters.

    Therefore, let `S` be a necklace on alphabet `1,2,...,n-1`. First augment `S` by inserting the letter `n`.
    If `m` is the maximum length for a `n`-separable necklace, then we now have `k:=m-|S|` spots left, where we can insert
    any of the `n` letters. This algorithm just tries every possible combination and sorts out non-trimmed necklaces.

    Returns an iterable iterating over all necklaces.
    """

    # use disk storage if flag is set
    if diskpath is not None:
        path = (
            os.path.join(diskpath, f"neckl_{len(seed)}.txt")
            if pid is None
            else os.path.join(diskpath, f"{len(seed)}_{pid}.txt")
        )
        if os.path.exists(path):
            with open(path) as f:
                line = f.readline()
                while line:
                    yield trimNecklace(line)
                    line = f.readline()
            return

    # if only one letter, only one possible necklace
    if len(seed) == 1:
        yield seed
        return

    print(f"Started {pid} on seed={seed}")
    # get the smaller necklaces
    genrec = generate(seed[:-1], diskpath=diskpath, pid=pid)
    # compute the n-1+sep-separable length bound: (3n-1)/2 + 2sep <= 3n/2 + 2sep
    n = len(seed)
    maxlen = 3 * n / 2 + 2 * sep
    # the new character is the last one
    new = seed[-1]
    index = 0
    for small in tqdm(genrec, disable=pid is not None):
        if pid is not None and index % 10000 == 0:
            print(f"[{pid}] {index}its", flush=True)
        index += 1

        big = small + new
        # compute how many spots are left
        k = math.ceil(maxlen) - len(big)
        # initialize a set of newly generated strings
        newgend = set()
        # initialize a set of newly generated modifications
        modstrings = {big}
        for _ in range(k):
            # initilize a set containing the newly computed modifications
            newmods = set()
            for mod in modstrings:
                r = trimNecklace(mod)
                # only yield a string if not already computed
                if r not in newgend:
                    newgend.add(r)
                    yield r
                # for each letter append at current spot and add each to modification set
                for x in seed:
                    newmods.add(mod + x)
            # now the current modificiation become the newly computed modification
            modstrings = newmods


def generateAndWrite(seed, sep, diskpath, outpath, pid=None):
    gend = generate(seed, sep=sep, diskpath=diskpath, pid=pid)
    path = (
        os.path.join(outpath, f"neckl_{len(seed)}.txt")
        if pid is None
        else os.path.join(outpath, f"neckl_{len(seed)}_{pid}.txt")
    )
    with open(path, "w") as f:
        for neckl in gend:
            f.write(f"{neckl}\n")
    os.system(f"mv {path} {diskpath}/")


if __name__ == "__main__":
    if len(sys.argv) < 5:
        print(help)
        exit()

    seed = "".join([x for x in sys.argv[1] if x in string.ascii_letters])
    diskpath = sys.argv[2]
    outpath = sys.argv[3]
    sep = int(sys.argv[4])
    procs = None if len(sys.argv) < 6 else int(sys.argv[5])
    print(seed, len(seed), sep, procs)
    if procs is None:
        generateAndWrite(seed, sep, diskpath, outpath)
    else:
        print(procs)
        ps = []

        for i in range(procs):
            p = Process(target=generateAndWrite, args=[seed, sep, diskpath, outpath, i])
            p.start()
            ps.append(p)

        for p in ps:
            p.join()
