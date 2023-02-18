# TODO add jaccard distance from A1P1

import math

def eucl_dist(x, y):
    sum = 0
    for i in range(len(x)):
        sum += (x[i] - y[i])**2
        
    return math.sqrt(sum) 


def hamming_distance(x, y):
    distance = 0
    for i in range(len(x)):
        if x[i] != y[i]:
            distance += 1
    
    return distance
