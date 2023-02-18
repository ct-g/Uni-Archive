# Last updated 10/10/2020

import numpy as np
import pandas as pd


__version__ = "0.4"
# 0.4 - modified compatible to have two modes of operation
#       - fixed error in combine (!= became = for wildcards *)
# 0.3 - rewrote lpattern with a more optimal greedy search algorithm
#       - simplifed compatibility functions to one function
#       - removed two now redundant functions (gen_patterns & add_wcard)
# 0.2 - rewrote reduce_patterns with a more optimal algorithm
# 0.1 - core functions added


def combine(pattern1, pattern2):
    """
        Takes two strings and produces a new one
        in which all dissimilar characters are
        replaced with a wildcard
    """
    new_pattern = ''
    for i in range(len(pattern1)):
        # Add a wildcard for every disimilar character
        if pattern1[i] != pattern2[i] or (pattern1[i] == '*' or pattern2[i] == '*'):
            new_pattern += '*'
        else:  # Add each similar character
            new_pattern += pattern1[i]
            
    return new_pattern


def compatible(pattern_set, dataset, mode='all'):
    """
        pattern_set = set of patterns or data
        dataset = set of data or patterns
        type = "any" or "all"
    """
    def compare(pattern1, pattern2):
        if len(pattern1) != len(pattern2):
            return False
    
        for i in range(len(pattern1)):
            if pattern1[i] == '*' or pattern2[i] == '*' or pattern1[i] == pattern2[i]:
                continue
            else:
                return False
        
        return True
    
    covers = [False] * len(dataset)
    for pattern in pattern_set:
        for i, data in enumerate(dataset):
            if compare(pattern, data):
                covers[i] = True
                
    if mode == 'all':
        for boolean in covers:
            if boolean == False:
                return False

        return True
    else:  # mode == 'any'
        for boolean in covers:
            if boolean == True:
                return True
        
        return False


def lpattern(set1, set2, l):
    """
        alphabet = set strings
        set1, set2 = disjoint sets of strings
        l = integer
    """
    # Ensure sets are disjoint
    if not set1.isdisjoint(set2):  # TODO raise error
        print("Sets are not disjoint")
    
    def find_patterns(patterns_c1, patterns_c2):
        patterns = []

        # Set instances as initial patterns to consider
        to_add = patterns_c1
        # Remove duplicate entries to reduce the search space
        to_add = list(dict.fromkeys(to_add))
        

        start = 0
        while len(to_add) > 0:
            patterns += to_add

            new_start = len(patterns)
            to_add = []
            for i in range(start, len(patterns) - 1):
                for j in range(i + 1, len(patterns)):
                    pattern1 = patterns[i]
                    pattern2 = patterns[j]
                    # Combine the two patterns to create one that can describe both
                    new_pattern = combine(pattern1, pattern2)
                
                    # Test the new pattern
                    if new_pattern not in (patterns + to_add) and not compatible(patterns_c2, [new_pattern], mode='any'):
                        to_add.append(new_pattern)

               
            start = new_start

        # Score each pattern depending on how many instances it is compatible with
        quality = []
        for pattern in patterns:
            score = 0
            for instance in patterns_c1:
                if compatible([pattern], [instance]):
                    score += 1
                        
            quality.append(score)

        # Greedily select patterns with the highest score to find the l-patterns set1
        lpatterns = []
        while not compatible(lpatterns, patterns_c1):
            index = quality.index(max(quality))
            pattern = patterns[index]
            
            quality.remove(max(quality))
            patterns.remove(pattern)
            
            if not compatible(lpatterns, [pattern]):
                lpatterns.append(pattern)
        
        return lpatterns
 
 
    result = (find_patterns(list(set1), list(set2)), find_patterns(list(set2), list(set1)))
    
    if len(result[0]) + len(result[1]) <= l:
        print("Successfully found a set of patterns such that |P|<=l")
    else:
        print("Found set of patterns does not satisfy |P|<=l")
    
    return result


def reduce_patterns(patterns_tuple):
    """
        Performs a local search on both elements of the patterns_tuple
        Uses existing patterns to inform which patterns to test
    """
    def merge(patterns, patterns2):
        to_add = ''
        to_remove = [] 
        for cur_pattern in patterns:
            loop = True
            for next_pattern in patterns:
                if cur_pattern == next_pattern:
                    continue
                
                to_add = ''
                to_remove.clear()
            
                # Combine the two patterns to create one that can describe both
                new_pattern = combine(cur_pattern, next_pattern)
                    
                # If pattern does not conflict with other set, break loop and merge
                if not compatible(patterns2, [new_pattern], mode='any'):
                    to_add = new_pattern
                    to_remove.append(cur_pattern)
                    to_remove.append(next_pattern)
                    loop = False
                    break
                
            if not loop:
                break
            
        if len(to_add) > 0:
            patterns.append(to_add)
            for pattern in to_remove:
                patterns.remove(pattern)
            
            return True
        
        return False
    
    
    # Pass over patterns, attempt a single merge for each class
    # Repeat until no merges are made
    merged = True
    while merged:
        bool1 = merge(patterns_tuple[0], patterns_tuple[1])
        bool2 = merge(patterns_tuple[1], patterns_tuple[0])
        merged = bool1 or bool2
    
