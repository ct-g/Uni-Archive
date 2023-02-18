// Conor Tumbers
// c3190729@uon.edu.au
// COMP2230, Assignment 1
// Last modified 21/10/2020

import java.util.Arrays;
import java.util.LinkedList;

public class Cluster {
    public final LinkedList<Vertex> vertices;
    private float x;
    private float y;

    public Cluster(Vertex vertex) {
        // Cluster requires a single vertex to be initialised
        this.vertices = new LinkedList<>();
        vertices.add(vertex);

        this.x = vertex.x;
        this.y = vertex.y;
    }

    public void add (Vertex vertex) {
        // Add a new vertex to the cluster
        vertices.add(vertex);

        // Recalculate x and y coordinates using average of all vertices in cluster
        x = 0;
        y = 0;
        for (Vertex hspot : vertices) {
            x += hspot.x;
            y += hspot.y;
        }
        x /= vertices.size();
        y /= vertices.size();
    }

    @Override
    public String toString() {
        // Get centre of cluster
        String coords = String.format("(%.2f, %.2f)\n", x, y);

        // Get names of vertices in cluster
        int[] labels = new int[vertices.size()];
        for (int i = 0; i < vertices.size(); i++) {
            labels[i] = vertices.get(i).label;
        }
        Arrays.sort(labels);
        StringBuilder string = new StringBuilder("Hotspots: {");
        for (int i = 0; i < labels.length; i++) {
            string.append(labels[i]);
            if (i == labels.length - 1) {
                string.append("}");
            } else {
                string.append(",");
            }
        }

        // Output
        return "Coordinates: " + coords + string;
    }
}
