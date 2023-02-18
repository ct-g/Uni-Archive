# Last update 12/09/2020

import math
import networkx as nx
import numpy as np
import pylab as plt

__version__ = "0.3.1"
# 0.3 - added plot argument to functions
# 0.2 - custom vertex label support
# 0.1 - subgraph methods now return a list of edges
# 0.0 - core functions added

def mst(V, D, save_as="graph-mst.gml", labels=[], plot=False):
    MST = nx.Graph()
    mst_labels = {}
    mst_edges =[]
    curr_vert = 0
    mst_vertices = {curr_vert}
    
    if len(labels) == 0:
        labels = [n for n in range(len(V))]
    
    # Add vertices until all are in the MST
    while mst_vertices != V:
        # Select an edge not in the MST and set it as the shortest found so far
        for vert in  range(len(D)):
            if vert not in mst_vertices:
                prev_vert = curr_vert
                curr_vert = vert
                min_dist = D[prev_vert][curr_vert] 
                break
        
        # Find the shortest edge to a vertex not in the MST
        for u in mst_vertices:
            for v, distance in enumerate(D[u][:]):
                if distance < min_dist and v not in mst_vertices:
                    min_dist = distance
                    prev_vert = u
                    curr_vert = v
                    
        # Add this vertex to the MST
        source = labels[prev_vert]
        target = labels[curr_vert]
        
        MST.add_edge(source, target, weight=min_dist)
        mst_edges.append([source, target, min_dist])
        mst_labels[(source, target)] = "{:.2f}".format(min_dist)
        mst_vertices.add(curr_vert)
        
    if plot:
        pos = nx.spring_layout(MST)
        nx.draw(MST, pos)
        nx.draw_networkx_labels(MST, pos)
        nx.draw_networkx_edge_labels(MST, pos, edge_labels=mst_labels)
        plt.show()
    
    nx.write_gml(MST, save_as)
    
    return mst_edges


def rngraph_compare(V, D, u, v):
    for r in (V - set([u, v])):
            if D[r][u] < D[u][v] and D[r][v] < D[u][v]:
                return False
    return True


def rngraph(V, D, save_as="graph-rng.gml", labels=[], plot=False):
    R = nx.Graph()
    rng_labels = {}
    rng_edges = []
    
    if len(labels) == 0:
        labels = [n for n in range(len(V))]
    
    for u in V:
        for v in (V - set([u])):
            dist = D[u][v]
            if rngraph_compare(V, D, u, v):
                source = labels[u]
                target = labels[v]
                
                R.add_edge(source, target, weight=dist)
                rng_edges.append([source, target, dist])
                rng_labels[(source, target)] = "{:.2f}".format(dist)

    if plot:
        pos = nx.spring_layout(R)
        nx.draw(R, pos)
        nx.draw_networkx_labels(R, pos)
        nx.draw_networkx_edge_labels(R, pos, edge_labels=rng_labels)
        plt.show()
    
    nx.write_gml(R, save_as)
    
    return rng_edges
    
    
def gg_graph_compare(V, D, u, v):
    for r in (V - set([u, v])):
        if D[u][v]**2 > D[u][r]**2 + D[v][r]**2:
            return False
    return True


def gg_graph(V, D, save_as="graph-gg.gml", labels=[], plot=False):
    GG = nx.Graph()
    gg_labels = {}
    gg_edges = []
    
    if len(labels) == 0:
        labels = [n for n in range(len(V))]
    
    for u in V:
        for v in (V - set([u])):
            dist = D[u][v]
            if gg_graph_compare(V, D, u, v):
                source = labels[u]
                target = labels[v]
                
                GG.add_edge(source, target, weight=dist)
                gg_edges.append([source, target, dist])
                gg_labels[(source, target)] = "{:.2f}".format(dist)

    if plot:
        pos = nx.spring_layout(GG)
        nx.draw(GG, pos)
        nx.draw_networkx_labels(GG, pos)
        nx.draw_networkx_edge_labels(GG, pos, edge_labels=gg_labels)
        plt.show()
    
    nx.write_gml(GG, save_as)
    
    return gg_edges
    
        
def knn(V, D, k, save_as="graph-knn.gml", labels=[], plot=False):
    KNN = nx.Graph()
    knn_vertices = set()
    knn_labels = {}
    knn_edges = []
    
    if len(labels) == 0:
        labels = [n for n in range(len(V))]

    # Set k to the max permitted value if it is greater than it
    # A vertex cannot have more neighbours than there are other vertices
    if k > len(V) - 1:
        print("k value too large, creating fully connected graph")
        k = len(V) - 1

    for u in V:
        neighbours = set([u]) # Currently connected vertices
        # Find k nearest neighbours and connect these vertices to u
        for i in range(k):
            # Set some other point as closest point found so far
            for v in (V - neighbours):
                if v not in knn_vertices:
                    break
            new_vert = v
            min_dist = D[u][v]
            
            # Find the closest point that has not already been added
            for v in (V - neighbours):
                if D[u][v] < min_dist:
                    min_dist = D[u][v]
                    new_vert = v
            
            # Add it to the graph
            neighbours.add(new_vert)
            knn_vertices.add(new_vert)
            
            source = labels[u]
            target = labels[new_vert]
            
            KNN.add_edge(source, target, weight=min_dist)
            knn_edges.append([source, target, min_dist])
            knn_labels[(source, target)] = "{:.2f}".format(min_dist)
            
    if plot:
        pos = nx.spring_layout(KNN)
        nx.draw(KNN, pos)
        nx.draw_networkx_labels(KNN, pos)
        nx.draw_networkx_edge_labels(KNN, pos, edge_labels=knn_labels)
        plt.show()
    
    nx.write_gml(KNN, save_as)
    
    return knn_edges

# Requires mst_edges and knn_edges to use the same vertex labelling scheme
# Assumes if edges are equivalent if the source and target are the same
# Cannot handle the case where two edges fulfill this condition with different weights
def mst_knn(mst_edges, knn_edges, saveas="graph-mst-knn.gml", plot=False):
    # Unlike the other functions, labels=[] is not required
    # As labels are used from the input edge lists
    MST_KNN = nx.Graph()
    mst_knn_labels = {}
    mst_knn_edges = []

    
    for mst_edge in mst_edges:
        for knn_edge in knn_edges:
            if (mst_edge[0] == knn_edge[0] and mst_edge[1] == knn_edge[1]) or \
            (mst_edge[0] == knn_edge[1] and mst_edge[1] == knn_edge[0]):
                
                source = mst_edge[0]
                target = mst_edge[1]
                dist = mst_edge[2]
                
                MST_KNN.add_edge(source, target, weight=dist)
                mst_knn_labels[(source, target)] = "{:.2f}".format(dist)
                mst_knn_edges.append([source, target, dist])


    if plot:
        pos = nx.spring_layout(MST_KNN)
        nx.draw(MST_KNN, pos)
        nx.draw_networkx_labels(MST_KNN, pos)
        nx.draw_networkx_edge_labels(MST_KNN, pos, edge_labels=mst_knn_labels)  
        plt.show()

    nx.write_gml(MST_KNN, saveas)
    
    return mst_knn_edges
