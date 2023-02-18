// Conor Tumbers
// c3190729@uon.edu.au
// COMP2230, Assignment 1
// Last modified 24/10/2020

import java.io.File;
import java.util.LinkedList;
import java.util.Scanner;

public class kcluster {
    public static void main(String[] args) {
	    // Read commandline arguments
        File input = new File(args[0]);

        int clusterAmount;
        try { // Catch non-integer input and non-positive integer input for clusterAmount (k)
            clusterAmount = Integer.parseInt(args[1]);
            if (clusterAmount <= 0) {
                throw new Exception("Requested amount of fire stations must be a positive integer");
            }
        } catch (Exception e) {
            System.err.println("Failed to parse requested amount of fire stations - " + e.getMessage());
            return;
        }

        // Process text file
        Scanner read;
        try {
            read = new Scanner(input);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return;
        }

        // Read in hotspots (vertices)
        LinkedList<Vertex> vertices = new LinkedList<>();
        int index = 0;

        try {
            if (!read.hasNext()) {
                throw new Exception("File is empty");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return;
        }

        // Read in a vertex for each line
        while (read.hasNext()) {
            String line = read.next();
            String[] parts = line.split(",");

            int label;  // Label is independent of index, index is used to access vertices and label is used for output
            float x;
            float y;
            try {
                label = Integer.parseInt(parts[0]);
            } catch (Exception e) {
                System.err.println("Could not parse integer label - " + e.getMessage());
                return;
            }

            try {
                x = Float.parseFloat(parts[1]);
                y = Float.parseFloat(parts[2]);
            } catch (Exception e) {
                System.err.println("Could not parse coordinate - " + e.getMessage());
                return;
            }

            vertices.add(new Vertex(label, index, x, y));
            index++;
        }
        read.close();

        // Build graph and cluster
        Graph graph = new Graph(vertices);
        graph.cluster(clusterAmount);
    }
}
