# Last updated 11/10/2020

import math
import numpy as np
import pandas as pd

# TODO generalise to multiclass classification
def naive_bayes(data, class_labels, attr_axis=1):  # Axis: 0 = rows, 1 = columns
    # Train Naive Bayes classifier
    freq_tables = []

    # Count how many instances of each class
    class_count = [0, 0]
    for val in class_labels:
        class_count[val] += 1

    # Record proportion of instances belonging to each class
    class_freq = [0, 0]
    for val in class_labels:
        class_freq[val] += 1 / len(class_labels)

    # Calculate a frequency table for each attribute
    if attr_axis == 0:
        attributes = data.iterrows()
    else:
        attributes = data.items()
    
    for _, attribute in attributes:
        m = 2  # Two classes
        n = int(max(attribute)) + 1

        freq_table = np.zeros([m, n])

        for item, class_index in zip(attribute, class_labels):
            val_index = int(item)
            freq_table[class_index][val_index] += 1 / class_count[class_index]

        freq_tables.append(freq_table)
        
    return freq_tables


def nb_classify(freq_tables, data, class_labels, inst_axis=0):
    # Get numerical class labels
    class_index = []
    for label in class_labels:
        if label not in class_index:
            class_index.append(label)
            
    class_index.sort()
    
    # Set the axis of the data that contains instances
    classifications = []
    if inst_axis == 0:
        instances = data.iterrows()
    else:
        instances = data.items()
        
    # Use the frequency table to classify the input data
    for _, inst in instances:
        class_prob = [1] * len(class_index)
        for class_label in class_index:
            for attr_val, freq_table in zip(inst, freq_tables):
                class_prob[class_label] *= freq_table[class_label][int(attr_val)]
        
        classifications.append(class_prob.index(max(class_prob)))
        
    return classifications


def cmatrix(classifications, class_labels):
    # Get numerical class labels
    class_index = []
    for label in class_labels:
        if label not in class_index:
            class_index.append(label)
            
    class_index.sort()
    
    # Build confusion matrix
    cm = np.zeros([len(class_index), len(class_index)])
    for prediction, actual in zip(classifications, class_labels):
        cm[prediction][actual] += 1
    
    return cm
