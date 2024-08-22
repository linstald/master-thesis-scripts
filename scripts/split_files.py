import os
import sys

help = """
# This file was used to split and combine files holding necklaces
# in order to parallelize the separability checking/computing process
# sys.argv[1] specifies the input file/directory
# sys.argv[2] specifies the output file/directory
# sys.argv[3] specifies N, the size of the splitted files (default: 100'000)
# only if sys.argv[3] is given (also on invalid numbers) will the files be splitted
# combine functionality: sequentially read files in input directory and write them in the output file
# split functionality: read input file and create smaller files (of size N) in the output directory holding chunks of the input file
"""


def split(file, outdir, N=100_000):
    print(N)
    fname = file.replace(".txt", "").split("/")[-1]
    with open(file) as f:
        x = f.readline()
        fi = 0
        fc = 0
        fw = open(os.path.join(outdir, f"{fname}_{fi}.txt"), "w")
        while x:
            if fc > N:
                fi += 1
                fw.close()
                fw = open(os.path.join(outdir, f"{fname}_{fi}.txt"), "w")
                fc = 0
            fw.write(x)
            fc += 1
            x = f.readline()


def combine(dir, outfile):
    with open(outfile, "w") as fw:
        for x in os.listdir(dir):
            if x.endswith(".txt"):
                with open(os.path.join(dir, x), "r") as fr:
                    lne = fr.readline()
                    while lne:
                        fw.write(lne)
                        lne = fr.readline()


if len(sys.argv) < 3:
    print(help)
    exit()

inp = sys.argv[1]
out = sys.argv[2]
if len(sys.argv) > 3:
    N = int(sys.argv[3]) if sys.argv[3].isdecimal() else 100_000
    split(inp, out, N)
else:
    combine(inp, out)
