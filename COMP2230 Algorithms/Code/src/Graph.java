// Conor Tumbers
// c3190729@uon.edu.au
// COMP2230, Assignment 1
// Last modified 24/10/2020

import java.text.DecimalFormat;
import java.util.Hashtable;
import java.util.LinkedList;

public class Graph {
    double[][] distMatrix;
    final int size; // The amount of vertices in the graph, once constructed the graph cannot be updated
    LinkedList<Edge> edges;
    LinkedList<Vertex> vertices;
    DecimalFormat dec; // Used to format the distance matrix in toString()

    public Graph(LinkedList<Vertex> vertices) {
        // Generate distance matrix and list of edges
        this.vertices = vertices;
        this.edges = new LinkedList<>();
        this.size = vertices.size();
        this.distMatrix = new double[size][size];

        for (int i = 0; i < this.size; i++) {
            for (int j = i + 1; j < size; j++) {
                Edge e = new Edge(vertices.get(i), vertices.get(j));
                // Distance matrix is symmetric along its diagonal
                this.distMatrix[i][j] = e.weight;
                this.distMatrix[j][i] = e.weight;

                this.edges.add(e);
            }
        }

        this.edges.sort(Edge::compareTo);

        this.dec = new DecimalFormat("#.##");
    }

    public void cluster(int k) {
        // Output introduction
        System.out.println("Hello and welcome to Kruskal's Clustering!\n");

        // If k is larger than the amount of vertices, restrict its value to produce the maximum amount of clusters
        if (k > vertices.size()) {
            k = vertices.size();
            System.out.println("Requested amount of clusters larger than the amount of hotspots.");
            System.out.println("Producing maximum amount of clusters possible.\n");
        }

        // Output the distance matrix
        System.out.println("The weighted graph of hotspots:\n");
        System.out.println(this);

        // Find clusters
        DisjointSets dSets = kruskal(k);

        // Prepare clusters for output
        Hashtable<Integer, Cluster> clusters = new Hashtable<>();
        LinkedList<Integer> keys = new LinkedList<>();
        for (int i = 0; i < size; i++) {
            int parent = dSets.getParent(i);
            // Find root node of this disjoint set's tree to determine which cluster the vertex belongs to
            while (parent != dSets.getParent(parent)) {
                parent = dSets.getParent(parent);
            }

            // If the cluster has not been initialised yet, do so, then add the current vertex
            if (!clusters.containsKey(parent)) {
                clusters.put(parent, new Cluster(vertices.get(i)));
                keys.add(parent);
            } else {
                clusters.get(parent).add(vertices.get(i));
            }
        }

        // Output amount of vertices and requested amount of clusters
        System.out.println("There are " + size + " hotspots.");
        System.out.println("You have requested " + k + " temporary fire stations.\n");

        // Output the results of clustering
        int n = 1;
        for (Integer key : keys) {
            Cluster cluster = clusters.get(key);
            System.out.println("Station " + n +":");
            System.out.println(cluster + "\n");
            n++;
        }

        // Set the initial inter-cluster distance to the longest edge in the graph
        double interclustDistance;
        if (edges.size() > 0 && k > 1) {
            interclustDistance = edges.get(edges.size() - 1).weight;
        } else { // Set to zero if there is only a single hotspot or a single cluster
            interclustDistance = 0;
        }
        // Find minimum inter-cluster distance
        for (Integer key : keys) {
            Cluster cluster = clusters.get(key);
            // Find each edge from this cluster to any other cluster and compare distances
            for (Vertex vertex : cluster.vertices) {
                int v = vertex.index;
                // Start at v + 1 so as to only consider the upper half of the symmetric distance matrix
                for (int w = v + 1; w < vertices.size(); w++) {
                    // Ignore any vertices from the same cluster
                    if (cluster.vertices.contains(vertices.get(w))) {
                        continue;
                    }
                    if (distMatrix[v][w] < interclustDistance) {
                        interclustDistance = distMatrix[v][w];
                    }
                }
            }
        }

        // Final output
        System.out.println("Inter-clustering distance: " + dec.format(interclustDistance) + "\n");
        System.out.println("Thank you for using Kruskal's Clustering. Bye.");
    }

    private DisjointSets kruskal(int k){
        // The edge list has been sorted in the constructor

        // Create set within the disjoint sets for each vertex in the graph
        DisjointSets dSets = new DisjointSets(size);
        for (int i = 0; i < size; i++) {
            dSets.makeSet(i);
        }

        // If clusters requested = amount of vertices, return the unmerged disjoint sets
        if (k == size) {
            return dSets;
        }

        // Place vertices into clusters using a variant of Kruskal's algorithm
        int count = 0;
        for (Edge edge : edges) {
            if (dSets.findSet(edge.v.index) != dSets.findSet(edge.w.index)) {
                count++;
                dSets.union(edge.v.index, edge.w.index);
            }

            // Break early when k disjoint sets have been produced, k = 1 results in the Minimum Spanning Tree
            if (count >= size - k) {
                break;
            }
        }

        return dSets;
    }

    @Override
    public String toString() {
        // Output the distance matrix representation of the graph
        double max;
        if (edges.size() > 0) {
            max = edges.get(edges.size() - 1).weight;
        } else { // Edge case of one hotspot and no edges
            max = 0;
        }

        int length = String.format("%.2f", max).length(); // Max length of string used for correct amount of whitespace
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++){
                result.append(String.format("%-" + length + "s", dec.format(distMatrix[i][j]))).append(" ");
            }
            result.append("\n");
        }

        return result.toString();
    }
}
