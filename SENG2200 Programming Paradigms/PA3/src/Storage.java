// Conor Tumbers
// c3190729@uon.edu.au
// SENG2200, Assignment 3
// Last modified 25/06/20

import java.util.LinkedList;

public abstract class Storage {
    int numItems;
    LinkedList<Item> queue;

    public abstract void add(Item item, double currentTime);

    public abstract Item remove(double currentTime);

    public abstract boolean atCapacity();

    public boolean empty() {
        return numItems == 0;
    }
}
