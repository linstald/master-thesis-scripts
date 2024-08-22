import random
import string

# This script is an implementation of the 3SAT to alpha-Necklace-Deciding reduction used to show NP-hardness.
# It reduces a 3SAT instance PHI to a necklace.

# The 3SAT instance, a list of clauses, each containing exactly three different variables
# Each clause is given by a set of three numbers.
# Each number represents a variable, negative numbers mean as a negative literal (positive numbers are positive literals)
PHI = [{1, -2, 3}, {4, -3, 1}]  # {2, -1, 3},
# all the variables occurring in PHI (need to take abs to account for negative literals)
VARS = {abs(x) for y in PHI for x in y}

# the N and P beads
NEG = 0
POS = 1

# special alpha values
ALL = -1
HALF = -2
NONE = -3

# a dictionary that will hold all different type of beads
# since there may be many beads we use numbers to represents the type of beads
# The dictionary will then hold the name of the bead types.
BEADS = {NEG: "NEG", POS: "POS"}  # 0: neg, 1: pos
# This will be the alpha vector
ALPHA = {NEG: NONE, POS: ALL}


def show(nList):
    """
    prints the necklace list using the bead names from BEADS
    """
    print([BEADS[i] for i in nList])


def letters(nList):
    """
    prints the necklace as a string of letters (only works when n is small enough)
    """
    alphabet = string.ascii_letters
    print("".join([alphabet[i] for i in nList]))


def getAlpha(nList):
    """
    computes and returns the alpha vector (accounting for the special alpha values)
    NONE, HALF, ALL
    """
    alpha = {}
    for c in ALPHA:
        number = nList.count(c)
        if ALPHA[c] == ALL:
            alpha[c] = number
        elif ALPHA[c] == HALF:
            alpha[c] = (number + 1) // 2
        elif ALPHA[c] == NONE:
            alpha[c] = 1
        else:
            alpha[c] = ALPHA[c]
    return alpha


def nextBead(name="", alpha=NONE):
    """
    creates a new bead with a specified name and alpha value
    if the name is empty a new separator bead is created
    the returned value is the bead id (that is a number)
    """
    if len(name) == 0:
        return nextBead(f"SEP{random.randbytes(5).hex()}", alpha=alpha)
    k = getBead(name)
    if k is None:
        next = max(BEADS.keys()) + 1
        BEADS[next] = name
        ALPHA[next] = alpha
        return next
    return k


def getBead(name):
    """
    returns the bead id given a bead name (or None if it does not exist)
    """
    for k in BEADS:
        if BEADS[k] == name:
            return k
    return None


def posEnforcer():
    """
    creates the positive side enforcing string, that is
    `aba`
    """
    a = nextBead("a", ALL)
    b = nextBead("b", ALL)
    enforcerString = []
    enforcerString.append(a)
    enforcerString.append(b)
    enforcerString.append(a)
    return enforcerString


def varInit(var):
    """
    creates the first variable part for a given variable, that is </br>
    P x_0^A x_T ... x_T x_0^B P x_0^A x_0^B P
    """
    occ = len([C for C in PHI if var in C or -var in C])
    initString = []
    varA = nextBead(f"{var}A0", ALL)
    varB = nextBead(f"{var}B0", ALL)
    varX = nextBead(f"{var}X", HALF)
    initString.append(POS)
    initString.append(varA)
    for _ in range(occ):
        initString.append(varX)
    initString.append(varB)
    initString.append(POS)
    initString.append(varA)
    initString.append(varB)
    initString.append(POS)
    return initString


def varFromClause(var, clause):
    """
    creates the variable encoding for a clause, that is </br>
    P x_i^A C_i x_i^B P x_i^A x_T x_i^B P , or</br>
    P x_i^A x_i^B P x_i^A x_T C_i x_i^B P </br>
    """
    i = PHI.index(clause) + 1
    clauseString = []
    varA = nextBead(f"{var}A{i}", ALL)
    varB = nextBead(f"{var}B{i}", ALL)
    varX = nextBead(f"{var}X", HALF)
    varC = nextBead(f"C{i}", 3)
    clauseString.append(POS)
    clauseString.append(varA)
    if var in clause:
        clauseString.append(varC)
    clauseString.append(varB)
    clauseString.append(POS)
    clauseString.append(varA)
    clauseString.append(varX)
    if -var in clause:
        clauseString.append(varC)
    clauseString.append(varB)
    clauseString.append(POS)
    return clauseString


def configClause(clause):
    """
    creates the string enforcing the clause cut bead, that is</br>
    P C_i C_i C_i S_i
    """
    i = PHI.index(clause) + 1
    configString = []
    varC = nextBead(f"C{i}", 3)
    sep = nextBead()
    configString.append(POS)
    configString.append(varC)
    configString.append(varC)
    configString.append(varC)
    configString.append(sep)
    return configString


def configVar(var):
    """
    creates the enforcing string for the x_T bead, that is </br>
    P x_T ... P x_T N x_T ... N x_T N S_x
    """
    varX = nextBead(f"{var}X", HALF)
    occ = len([C for C in PHI if var in C or -var in C])
    sep = nextBead()
    configString = []
    for _ in range(occ):
        configString.append(POS)
        configString.append(varX)
    for _ in range(occ):
        configString.append(NEG)
        configString.append(varX)
    configString.append(NEG)
    configString.append(sep)
    return configString


def configPosNeg():
    """
    creates the string enforcing cuts in P and N, that is </br>
    P N
    """
    configString = []
    configString.append(POS)
    configString.append(NEG)
    return configString


# Now we can combine these strings using the given instance PHI
nlst = posEnforcer()

for var in VARS:
    nlst = nlst + varInit(var)
for clause in PHI:
    for x in clause:
        nlst = nlst + varFromClause(abs(x), clause)

for var in VARS:
    nlst = nlst + configVar(var)

for clause in PHI:
    nlst = nlst + configClause(clause)

nlst = nlst + configPosNeg()

# nlst now holds the necklace as a string

print(nlst)
show(nlst)
letters(nlst)
print(getAlpha(nlst))
