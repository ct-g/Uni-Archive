// Conor Tumbers
// c3190729@uon.edu.au
// SENG2200, Assignment 3
// Last modified 25/06/20

import java.util.LinkedList;

public class Item {
    private String ID;

    private LinkedList<String> stagePath; // Represents the path the item takes through the production line

    public Item(String ID) {
        this.ID = ID;
        stagePath = new LinkedList<>();
    }

    // Update the path when first processed by a new stage
    public void logStage (String stageID) {
        stagePath.add(stageID);
    }

    public String getID() {
        return ID;
    }

    public LinkedList<String> getStagePath() {
        return stagePath;
    }
}
