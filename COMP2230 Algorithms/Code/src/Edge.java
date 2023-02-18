// Conor Tumbers
// c3190729@uon.edu.au
// COMP2230, Assignment 1
// Last modified 21/10/2020

public class Edge implements Comparable<Edge> {
    // An edge is defined by the two vertices it connects and the distance between them (the weight of the edge)
    Vertex v, w;
    double weight;

    public Edge(Vertex v, Vertex w){
        this.v = v;
        this.w = w;

        // Calculate weight using Euclidean distance
        float value1 = v.x - w.x;
        value1 *= value1;

        float value2 = v.y - w.y;
        value2 *= value2;

        this.weight = Math.sqrt(value1 + value2);
    }

    @Override
    public int compareTo(Edge other) {
        return Double.compare(weight, other.weight);
    }

    @Override
    public String toString() {
        return String.format("%d->%d=%.2f", v.index, w.index, weight);
    }
}
