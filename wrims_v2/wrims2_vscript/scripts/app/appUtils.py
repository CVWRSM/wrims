import sys


def clearVars():
    this = sys.modules[__name__]
    for _n in dir():
        if _[0]!='_': delattr(this, _n)
    

