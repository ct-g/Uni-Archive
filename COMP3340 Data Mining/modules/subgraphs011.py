# Last update 09/09/2020

import math
import networkx as nx
import numpy as np
import pylab as plt

__version__ = "0.1.1"
# 0.1 - subgraph functions now return a list of edges

def mst(V, D, save_as="graph-mst.gml"):
    MST = nx.Graph()
    mst_labels = {}
    mst_edges =[]
    curr_vert = 0
    mst_vertices = {curr_vert}
    
    while mst_vertices != V:
        for vert in  range(len(D)):
            if vert not in mst_vertices:
                break
        
        prev_vert = curr_vert
        curr_vert = vert
        min_dist = D[prev_vert][curr_vert]
        
        for u in mst_vertices:
            for v, distance in enumerate(D[u][:]):
                if distance < min_dist and v not in mst_vertices:
                    min_dist = distance
                    prev_vert = u
                    curr_vert = v
                    
        MST.add_edge(prev_vert, curr_vert, weight=min_dist)
        mst_edges.append([prev_vert, curr_vert, min_dist])
        mst_labels[(prev_vert, curr_vert)] = "{:.2f}".format(min_dist)
        mst_vertices.add(curr_vert)
        
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


def rngraph(V, D, save_as="graph-rng.gml"):
    R = nx.Graph()
    rng_labels = {}
    rng_edges = []
    
    for u in V:
        for v in (V - set([u])):
            dist = D[u][v]
            if rngraph_compare(V, D, u, v):
                R.add_edge(u, v, weight=dist)
                rng_edges.append([u, v, dist])
                rng_labels[(u, v)] = "{:.2f}".format(dist)

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


def gg_graph(V, D, save_as="graph-gg.gml"):
    GG = nx.Graph()
    gg_labels = {}
    gg_edges = []
    
    for u in V:
        for v in (V - set([u])):
            dist = D[u][v]
            if gg_graph_compare(V, D, u, v):
                GG.add_edge(u, v, weight=dist)
                gg_edges.append([u, v, dist])
                gg_labels[(u, v)] = "{:.2f}".format(dist)

    pos = nx.spring_layout(GG)
    nx.draw(GG, pos)
    nx.draw_networkx_labels(GG, pos)
    nx.draw_networkx_edge_labels(GG, pos, edge_labels=gg_labels)

    plt.show()
    
    nx.write_gml(GG, save_as)
    
    return gg_edges
    
        
def knn(V, D, k, save_as="graph-knn.gml"):
    KNN = nx.Graph()
    knn_vertices = set()
    knn_labels = {}
    knn_edges = []

    # Set k to the max permitted value if it is greater than it
    # A vertex cannot have more neighbours than there are other vertices
    if k > len(V) - 1:
        print("k value too large, creating fully connected graph")
        k = len(V) - 1

    for u in V:
        neighbours = set([u]) # Currently connected vertices
        for i in range(k):
            # Set some other point as closest point found so far
            for v in (V - neighbours):
                if v not in knn_vertices:
                    break
            new_vert = v
            min_dist = D[u][v]
            
            # Find the closest point
            for v in (V - neighbours):
                if D[u][v] < min_dist:
                    min_dist = D[u][v]
                    new_vert = v
                    
            neighbours.add(new_vert)
            knn_vertices.add(new_vert)
            KNN.add_edge(u, new_vert, weight=min_dist)
            knn_edges.append([u, new_vert, min_dist])
            knn_labels[(u, new_vert)] = "{:.2f}".format(min_dist)
            
    pos = nx.spring_layout(KNN)
    nx.draw(KNN, pos)
    nx.draw_networkx_labels(KNN, pos)
    nx.draw_networkx_edge_labels(KNN, pos, edge_labels=knn_labels)

    plt.show()
    
    nx.write_gml(KNN, save_as)
    
    return knn_edges
