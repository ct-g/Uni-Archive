// Conor Tumbers
// c3190729@uon.edu.au
// COMP2230, Assignment 1
// Last modified 21/10/2020

import java.util.Arrays;

public class DisjointSets {
    private final int[] parent;
    private final int[] rank;

    public DisjointSets(int size) {
        this.parent = new int[size];
        this.rank = new int[size];
    }

    void makeSet(int i) {
        // Initialise a new set with a single element, each set being a tree with only a root
        parent[i] = i;
        rank[i] = 0;
    }

    int findSet(int i) {
        // Find the root of the tree representation of a set
        int root = i;
        while (root != parent[root]) {
            root = parent[root];
        }
        // Set all nodes in this set as a child of the root node
        int j = parent[i];
        while (j != root) {
            parent[i] = root;
            i = j;
            j = parent[i];
        }
        return root;
    }

    void mergeTrees(int i, int j) {
        // Merge two trees by making one a child of the other's root node
        if (rank[i] < rank[j]) {
            parent[i] = j;
        }
        else if (rank[i] > rank[j]) {
            parent[j] = i;
        }
        else {
            parent[i] = j;
            rank[j] = rank[j] + 1;
        }
    }

    void union(int i, int j) {
        // Merge the two sets that i and j belong to
        mergeTrees(findSet(i), findSet(j));
    }

    public int getParent(int i) {
        return parent[i];
    }

    @Override
    public String toString() {
        return Arrays.toString(parent);
    }
}
