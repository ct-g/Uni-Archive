// Conor Tumbers
// c3190729@uon.edu.au
// SENG2200, Assignment 3
// Last modified 25/06/20

import java.util.LinkedList;

public class Event implements Comparable<Event> {
    private double time; // Time the event occurs
    private Stage origin; // Object that created the event

    public Event(double time, Stage stage){
        this.time = time;
        this.origin = stage;
    }

    public double getTime() {
        return time;
    }

    // Updates the stage's state and returns the list of events it generates
    public LinkedList<Event> update(double currentTime) {
        return origin.update(currentTime);
    }

    @Override
    public int compareTo(Event event) {
        return Double.compare(time, event.time);
    }
}
