// Conor Tumbers
// c3190729@uon.edu.au
// SENG2200, Assignment 3
// Last modified 25/06/20

import java.util.LinkedList;

public class StartStage extends Stage {
    private Singleton idGen;
    private String letter; // Letter that is appended to the id of all items generated

    public StartStage(int M, int N, InterStorage out, String id, double timeLimit) {
        super(M, N, null, out, id, timeLimit, new LinkedList<>()); // Pass in empty stage list as no prior stages
        this.starved = false;
        this.idGen = Singleton.getInstance();

        this.letter = id.replace("S", "");
        this.letter = this.letter.replaceAll("[0-9]", "").toUpperCase();
    }

    // Generate a new item and create an event for when it is ready to be stored
    @Override
    LinkedList<Event> retrieve(double currentTime) {
        LinkedList<Event> eventList = new LinkedList<>();

        // Generate a new item
        String itemID = idGen.getID().append(letter).toString();
        currentItem = new Item(itemID);
        currentItem.logStage(getId());

        // Calculate production time and create an event for when production completes
        double d = r.nextDouble();
        double P = M + N * (d - 0.5);
        eventList.add(new Event(currentTime + P, this));

        return eventList;
    }
}
