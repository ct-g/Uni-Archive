# Last updated 09/10/2020 

import math
import numpy as np
import pandas as pd

__version__ = "0.3"

# 0.3 - added min-max normalisation function
# 0.2 - changed entropy based discretisation to fayyad-irani algorithm
#       - by changing the gain_threshold input to a calculation
# 0.1 - initial implementation of discretisation and k-feature set reduction

def map_classes(classes, class_name):
    class_labels = []
    for item in classes:
        if (item == class_name):
            class_labels.append(0)
        else:
            class_labels.append(1)
        
    return class_labels


# Min-max normalise the input data
def normalise(data, mini=0, maxi=0, override=False):
    # Find minimum and maximum values of data
    if not override:
        mini = min(data)
        maxi = max(data)
    
    # Normalise data
    values = []
    for val in data:
        values.append((val - mini) / (maxi - mini))
        
    # Write over input data
    for i, val in enumerate(values):
        data.iloc[i] = val


# Discretisation functions
def discretise_dataset(data, classes, attr_axis=1):
    # TODO passing in a pre-discretised dataset creates poor output
    if attr_axis == 0:
        feature_count = len(data.index)
    else:
        feature_count = len(data.columns)
    
    # Number of instances used to determine gain threshold
    inst_count = len(classes)

    print("Total features = ", feature_count)
    data_splits = []
    for index in range(feature_count):
        # TODO doesn't overwrite last line sometimes
        print("Discretising feature ", index + 1, end='\r', flush=True)
        if attr_axis == 0:
            cur_data = data.iloc[index, :]
        else:
            cur_data = data.iloc[:, index]

        splits = []
        splits += __disc_recurs__(cur_data.copy(), classes, inst_count, splits)
        splits.sort()
        
        apply_splits(cur_data, splits)
        data_splits.append(splits)
        
    return data_splits


def apply_splits(data, splits):
    for i, val in data.iteritems():
        val = float(val)
        
        if len(splits) == 0:  # If the attribute is irrelevant, then no splits were found
            data.at[i] = 0
        else:
            for j, split in enumerate(splits):
                if val <= split:
                    data.at[i] = j
                    break

            # If the value is greater than the final split value
            if val > split:
                data.at[i] = j + 1


def __disc_recurs__(data, class_list, inst_count, split_list):
    """ 
        Same as for discretise()
        split_list = list of split points, internal use only
    """
    
    if len(data) == 0:
        return []
      
    # Sorted data used to find candidate split points
    data_sorted = data.sort_values()
    
    # Remove duplicates and reindex
    data_sorted = data.drop_duplicates()
    data_sorted = data_sorted.reindex([i for i in range(data_sorted.size)], method='backfill')
    
    # Set up range within in which to find a split point
    mini = min(data_sorted)
    maxi = max(data_sorted)
    
    # Set up initial values for best split found so far
    best_gain = 0
    best_split = 0
    best_e = [1, 1, 1]
    c1 = 0
    c2 = 0
    
    # Calculate the entropy of the data before splitting
    initial_e, _, c = entropy(data, class_list, mini, maxi)
    
    # Find split point in data
    for i in range(0, data_sorted.size - 1):
        # Take the average of two adjacent values and use this as the split point
        split = (data_sorted[i] + data_sorted[i+1])/2

        # Calculate entropy of the two bins produced at this split point
        e1, s1, c1 = entropy(data, class_list, mini, split)
        e2, s2, c2 = entropy(data, class_list, split, maxi)

        # Calculate information for both bins combined
        s = s1 + s2
        info = (s1 / s) * e1 + (s2 / s) * e2

        # Calculate gain
        gain = initial_e - info

        # If gain is better than previous best, this split is now the best split
        if gain > best_gain:
            best_gain = gain
            best_split = split
            best_e = [initial_e, e1, e2]

    # Calculate gain threshold
    delta = math.log(3**c, 2) - (c*best_e[0] - c1*best_e[1] - c2*best_e[2])
    gain_threshold = math.log(inst_count -  1, 2) + delta
    gain_threshold = gain_threshold / inst_count

    # Accept split point only if gain is above threshold
    if best_gain <= gain_threshold:
        return []
    
    # Split data along this point and recursively call self
    split_data1 = pd.Series()
    split_data2 = pd.Series()
    split_class_list1 = []
    split_class_list2 = []
    
    for i, val in enumerate(data):
        if val <= best_split:
            if split_data1.size == 0:
                split_data1 = split_data1.append(pd.Series([val]))
            else:
                split_data1.at[max(split_data1.index) + 1] = val
            
            split_class_list1.append(class_list[i])
        else:
            if split_data2.size == 0:
                split_data2 = split_data2.append(pd.Series([val]))
            else:
                split_data2.at[max(split_data2.index) + 1] = val

            split_class_list2.append(class_list[i])
    
    split_list += __disc_recurs__(split_data1, split_class_list1, inst_count, split_list)
    split_list += __disc_recurs__(split_data2, split_class_list2, inst_count, split_list)
    
    # Return best split found
    return [best_split]


def entropy(data, classes, lbound, ubound):
    """ 
        data = pandas Series
        classes = class
        lbound and ubound = numbers
    """
        
    # Assign classes an integer to be used as an index
    class_set = set()
    for item in classes:
        class_set.add(item)
                
    class_amount = len(class_set)
    
    class_dict = {}
    for i, item in enumerate(class_set):
        class_dict.update({item: i})
    
    # Count how many values occur within the bounds and which class each belongs to
    class_totals = []
    for i in range(class_amount):
        class_totals.append(0)
               
    for val, category in zip(data, classes):
        if (val < ubound and val >= lbound):
            class_totals[class_dict[category]] += 1
    
    # Calculate proportion of values in each class
    prob = []
    size = sum(class_totals)
    if size == 0:
        return 1, 0, 0  # Empty interval, return worst case result of entropy = 1

    for item in class_totals:
        prob.append(item / size)

    # Calculate entropy
    total = 0
    for i in range(class_amount):
        # Do not add anything if class occurs 0 times, to prevent log(0) occurring
        if prob[i] != 0:
            total += prob[i] * math.log(prob[i], 2)
        
    total = total * -1

    return total, size, class_amount


# K-feature set reduction functions
def chi_sq(data, class_labels, attr_axis=1):
    # Calculate chi-squared values for each attribute in dataset
    chi_values = []
    
    if attr_axis == 0:
        attributes = data.iterrows()
    else:
        attributes = data.items()
    
    for _, attribute in attributes:
        m = 2  # Two classes
        n = int(max(attribute)) + 1

        # Contingency table for class attribute and the current attribute
        con_table = np.zeros([m+1, n+1])
        for val, class_num in zip(attribute, class_labels):
            val = int(val)
            con_table[class_num][val] += 1

        # Update sum column and row in table
        for i in range(m):
            for j in range(n):
                con_table[i][n] += con_table[i][j]
                con_table[m][j] += con_table[i][j]

                # Update intersection of sum column and row
                con_table[m][n] += con_table[i][j]

        # Calculate chi-table
        chi_table = np.zeros([m, n])
        for i in range(m):
            for j in range(n):
                chi_table[i][j] = (con_table[i][n] * con_table[m][j]) / con_table[m][n]

        # Calculate chi-squared
        chi_sq = 0
        for i in range(m):
            for j in range(n):
                chi_sq += ((con_table[i][j] - chi_table[i][j]) ** 2 ) / chi_table[i][j]

        chi_values.append(chi_sq)
        
    return chi_values


# Create reduced dataset by choosing k highest scoring attributes
def k_chi_reduce(data, k, chi_values, labels, attr_axis=1):
    cur_chi_values = chi_values.copy()
    indices = []
    for i in range(k):
        max_chi_index = cur_chi_values.index(max(cur_chi_values))
        cur_chi_values.remove(max(cur_chi_values))
        indices.append(max_chi_index)
    
    keep = []
    for i in indices:
        keep.append(labels[i])
        
    return data.filter(keep, axis=attr_axis)
