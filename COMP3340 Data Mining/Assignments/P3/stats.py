# Last updated 07/11/2020

import math
import numpy as np

__version__ = "0.2"

# 0.2 - added Cohen's kappa
# 0.1 - creation

# The following functions accept a 2x2 confusion matrix
# The matrix must be of the following formate
# TP FP
# FN TN
def get_values(matrix):
    return matrix[0][0], matrix[0][1], matrix[1][0], matrix[1][1]


def sensitivity(matrix):
    TP, FP, FN, TN = get_values(matrix)
    return TP / (TP + FN)


def specificity(matrix):
    TP, FP, FN, TN = get_values(matrix)
    return TN / (TN + FP)


def accuracy(matrix):
    TP, FP, FN, TN = get_values(matrix)
    return (TP + TN) / (TP+TN+FP+FN)


def precision(matrix):
    TP, FP, FN, TN = get_values(matrix)
    return TP / (TP + FP)


def f1(matrix):
    return 2 / ((1 / precision(matrix)) + (1 / sensitivity(matrix)))


def mcc(matrix):
    TP, FP, FN, TN = get_values(matrix)
    n = (TP * TN) - (FP * FN)
    d = math.sqrt((TP+FP) * (TP+FN) * (TN+FP) * (TN+FN))
    
    if d == 0:
        d = 1
    
    return n / d

def youdenJ(matrix):
    return sensitivity(matrix) + specificity(matrix) - 1


def ckappa(matrix):
    a, b, c, d = get_values(matrix)
    acc = accuracy(matrix)
    
    # Probability of randomly clustering the same pair together
    p_pos = (a + b) * (a + c) / ((a + b + c + d)**2) 
    
    # Probability of randomly clustering the same pair apart
    p_neg = (c + d) * (b + d) / ((a + b + c + d)**2)
    
    # Total probability of clusters randomly agreeing
    p = p_pos + p_neg
    
    return (acc - p) / (1 - p)
