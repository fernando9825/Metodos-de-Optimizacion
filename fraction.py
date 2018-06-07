#!/usr/bin/python
#-*- coding: utf-8 -*-

import sys
from fractions import Fraction

val = str(sys.argv[1])

print(str(Fraction(val).limit_denominator(1000)))