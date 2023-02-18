// Conor Tumbers
// c3190729@uon.edu.au
// SENG2200, Assignment 3
// Last modified 25/06/20

import java.util.LinkedList;

public class Warehouse extends Storage {
    private int aa, ab, ba, bb; // Count of items through production paths
    private int a, b; // Count of items that originated from the corresponding start stage

    public Warehouse() {
        this.queue = new LinkedList<>();

        a = 0;
        b = 0;
        aa = 0;
        ab = 0;
        ba = 0;
        bb = 0;
    }

    // Adds an item to the queue and updates the production path statistics
    @Override
    public void add(Item item, double currentTime) {
        numItems++;
        queue.add(item);

        // Update production path data
        LinkedList<String> stageIDs = item.getStagePath();

        for (String stageID : stageIDs) {
            switch (stageID) {
                case "S3a":
                    for (String stageID2 : stageIDs) {
                        if (stageID2.equals("S5a")) {
                            aa++;
                        } else if (stageID2.equals("S5b")) {
                            ab++;
                        }
                    }
                    break;
                case "S3b":
                    for (String stageID2 : stageIDs) {
                        if (stageID2.equals("S5a")) {
                            ba++;
                        } else if (stageID2.equals("S5b")) {
                            bb++;
                        }
                    }
                    break;
                case "S0a":
                    a++;
                    break;
                case "S0b":
                    b++;
                    break;
            }
        }
    }

    @Override
    public Item remove(double currentTime) {
        if (!empty()) {
            // Remove item from queue
            numItems--;
            return queue.removeLast();
        } else {
            return null;
        }
    }

    // Warehouse is considered infinite
    @Override
    public boolean atCapacity() {
        return false;
    }

    @Override
    public String toString() {
        return "Warehouse Contents: " + String.format("%,d", numItems); // TODO
    }

    public void productionPaths(){
        System.out.println("S3a -> S5a:  " + aa);
        System.out.println("S3a -> S5b:  " + ab);
        System.out.println("S3b -> S5a:  " + ba);
        System.out.println("S3b -> S5b:  " + bb);
    }

    public void productionItems() {
        System.out.println("S0a:  " + a);
        System.out.println("S0b:  " + b);
    }
}
