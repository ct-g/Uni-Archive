// Conor Tumbers
// c3190729@uon.edu.au
// SENG2200, Assignment 3
// Last modified 25/06/20

import java.util.LinkedList;

public class InterStorage extends Storage {
    private int Qmax; // Capacity of the storage

    private String id;

    // Variables used to calculate the moving average
    private LinkedList<Double> times;
    private double avgTime;
    private double avgItem;
    private int avgTimeCalcCount;
    private int avgItemCalcCount;

    public InterStorage(String id, int Qmax) {
        this.id = id;

        this.Qmax = Qmax;
        this.queue = new LinkedList<>();
        this.times = new LinkedList<>();

        this.avgTime = 0.0;
        this.avgItem = 0.0;
        this.avgTimeCalcCount = 1;
        this.avgItemCalcCount = 1;
    }

    // Adds an item to the end of the queue
    @Override
    public void add(Item item, double currentTime) {
        // Increment running average for the amount of items if the storage has been empty
        if (empty()) {
            avgItem = (avgItemCalcCount * avgItem) / (avgItemCalcCount + 1);
            avgItemCalcCount++;
        }

        // Add item if there is room
        if (numItems < Qmax) {
            numItems++;
            queue.add(item);

            // Update times linked list for stat calculations
            times.add(currentTime);
        }
    }

    // Returns the oldest item in the queue
    @Override
    public Item remove(double currentTime) {
        if (!empty()) {
            // Calc average time
            double x = (currentTime - times.remove());
            avgTime = (x + avgTimeCalcCount * avgTime) / (avgTimeCalcCount + 1);
            avgTimeCalcCount++;

            // Calc average items
            // Do not update if an item is removed immediately after it is added as no change in time is observed
            if (x > 0) {
                avgItem = (numItems + avgItemCalcCount * avgItem) / (avgItemCalcCount + 1);
                avgItemCalcCount++;
            }

            // Remove item from queue
            numItems--;
            return queue.removeLast();
        }

        return null;
    }

    @Override
    public boolean atCapacity() {
        return numItems == Qmax;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(id);

        String value = String.format("%,.2f", avgTime);
        result.append(spaces(17 - id.length() - value.length()));
        result.append(value);

        value = String.format("%,.2f", avgItem);
        result.append(spaces(11 - value.length()));
        result.append(value);
        return result.toString();
    }

    // Generates a string of an arbitrary amount of whitespace
    private String spaces(int amount) {
        StringBuilder whitespace = new StringBuilder(" ");
        for (int i = 0; i < amount; i++){
            whitespace.append(" ");
        }

        return whitespace.toString();
    }
}
