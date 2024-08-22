# How to use the `necklace` class
To use our implementation of necklaces put the `Necklace` folder in your working directory.
Then you can use 
```python
from Necklace.necklace import *
```
to get access to the `Necklace` class.
To construct a necklace use the constructor:
```python
neckl = Necklace(<<some string representing necklace>>)
```
where ```<<some string representing necklace>>``` is a string or a list that represents the necklace from left to right.
For example, ```"abbbaaa"``` can be used to represent the necklace $C_a = \{1,5,6,7\}, C_b = \{2, 3, 4\}$.
The same necklace can also be represented by the list ```[1, 2, 2, 2, 1, 1, 1]```.

Our implementation of our algorithm of $\alpha$-Necklace-Splitting is given in the function `findAlphaCut`.
It takes as input a dictionary representing $\alpha$ and returns a tuple corresponding to the $\alpha$-cut and $\overline{\alpha}$-cut.
In the above example, a valid dictionary could be
```python
alpha = {"a": 2, "b": 3}
```
then call `findAlphaCut` as 
```python
from Necklace.necklace import *
neckl = Necklace("abbbaaa")
alpha = {"a":2, "b": 3}
alphaCut, negAlphaCut = neckl.findAlphaCut(alpha)
```