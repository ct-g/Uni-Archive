# Last updated 09/10/2020

import math
import numpy as np

__version__ = "0.1"

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
    d = (TP * TN) - (FP * FN)
    n = math.sqrt((TP+FP) * (TP+FN) * (TN+FP) * (TN+FN))
    return d / n

def youdenJ(matrix):
    return sensitivity(matrix) + specificity(matrix) - 1
