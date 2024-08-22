import os
import sys

sys.path.append(os.path.abspath(os.path.join(__file__, os.pardir, os.pardir)))
from Necklace.necklace import *
from multiprocessing import Pool

help = """
# Script to determine the separability of necklaces
# the first argument (sys.argv[1]) is a directory holding files names 'neckl*'.
# For each of these files, the script iterates over all lines (which hold necklace strings)
# in that file and determines the separability of that necklace. The result is written to an outfile.
# The outfile is at <dir>/sep/sep_<filename> where <dir> = sys.argv[1] and <filename> is the name of the current file.
# The output format is <sep>,<neckl> where <neckl> is a necklace string and <sep> its separability.
# The script works in parallel, if there is a second argument to the script (sys.argv[2]):
# it specifies the number of processors to use, otherwise (if no second argument) os.cpu_count() many processors are used.
"""


def sep(args):
    dir, file = args
    if not file.startswith("neck"):
        return
    infile = f"{dir}/{file}"
    outfile = f"{dir}/sep/sep_{file}"
    print(f"{infile} started")
    with open(infile) as fr, open(outfile, "w") as fw:
        lne = fr.readline()
        i = 0
        while lne:
            if i % 100 == 0:
                print(f"{infile} {i}", flush=True)
            i += 1
            neckl = Necklace(lne)
            sep = neckl.computeSep()
            fw.write(f"{sep},{lne}")
            lne = fr.readline()

    print(f"{infile} finished")


if __name__ == "__main__":
    if len(sys.argv) < 2:
        print(help)
        exit()

    dir = sys.argv[1]
    os.makedirs(f"{dir}/sep/", exist_ok=True)
    cpus = int(sys.argv[2]) if len(sys.argv) > 2 else os.cpu_count()
    print(cpus)
    with Pool(cpus) as p:
        p.map(sep, [(dir, f) for f in os.listdir(dir)])
