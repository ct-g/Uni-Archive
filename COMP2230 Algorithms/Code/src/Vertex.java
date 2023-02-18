// Conor Tumbers
// c3190729@uon.edu.au
// COMP2230, Assignment 1
// Last modified 21/10/2020

public class Vertex {
    // A vertex is defined by its location and its identifier (label)
    int label;
    int index; // The index is indpendent of the label and used to access the vertex from lists/arrays/other structures
    float x, y;

    public Vertex(int label, int index, float x, float y){
        this.label = label;
        this.index = index;
        this.x = x;
        this.y = y;
    }

    public String toString() {
        return (label + "," + x + "," + y);
    }
}
