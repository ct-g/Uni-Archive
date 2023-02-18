// Conor Tumbers
// c3190729@uon.edu.au
// SENG2200, Assignment 3
// Last modified 25/06/20

import java.util.Random;
import java.util.LinkedList;

public class Stage {
    Storage in, out; // Inter-stage storage
    static Random r = new Random();
    int M, N; // Mean and range, respectively
    double timeLimit;
    boolean blocked, starved;

    private String id;

    private double blockStart, starveStart;
    private double blockTime, starveTime;

    Item currentItem;

    // Lists that contain the connected stages
    LinkedList<Stage> prev = new LinkedList<>();
    LinkedList<Stage> next = new LinkedList<>();

    public Stage(int M, int N, Storage in, Storage out, String id, double timeLimit, LinkedList<Stage> prev) {
        this.M = M;
        this.N = N;
        this.in = in;
        this.out = out;
        this.id = id;

        this.timeLimit = timeLimit;

        this.starveTime = 0.0;
        this.blockTime = 0.0;

        this.blockStart = 0.0;
        this.starveStart = 0.0;

        this.currentItem = null;
        this.blocked = false;
        this.starved = true;

        // References to all stages that feed into this one
        this.prev.addAll(prev);

        // Set this stage as next in line after all directly previous stages
        for (Stage stage : prev) {
            stage.connectStages(this);
        }
    }

    // Add connections to the stages that come next
    private void connectStages(Stage stage) {
        next.add(stage);
    }

    // Checks if the stage needs to store an item or retrieve an item and create events
    // Returns a list of all events generated
    public LinkedList<Event> update(double currentTime) {
        LinkedList<Event> eventList = new LinkedList<>();

        if (starved || blocked) {
            return eventList;
        }

        if (currentItem != null) {
            eventList.addAll(store(currentTime));
        }

        if (currentItem == null) {
            eventList.addAll(retrieve(currentTime));
        }

        return eventList;
    }

    LinkedList<Event> store(double currentTime) {
        LinkedList<Event> eventList = new LinkedList<>();

        // Store current item
        if(!out.atCapacity()) {
            out.add(currentItem, currentTime);
            currentItem = null;

            // Unstarve the stage that starved the earliest in the next step of production
            Stage current = null;
            for (Stage stage : next) {
                if (stage.starved) {
                    if (current == null) {
                        current = stage;
                    } else {
                        if (stage.starveStart < current.starveStart) {
                            current = stage;
                        }
                    }
                }
            }
            if (current != null) {
                current.unstarve(currentTime);
                eventList.addAll(current.update(currentTime));
            }
        } else {
            // Block
            blocked = true;
            blockStart = currentTime;
            return eventList;
        }

        return  eventList;
    }

    LinkedList<Event> retrieve(double currentTime) {
        LinkedList<Event> eventList = new LinkedList<>();

        // Retrieve next item
        if (!in.empty()) {
            currentItem = in.remove(currentTime);
            currentItem.logStage(getId());

            // Unblock the stage that blocked the earliest in the prior step of production
            Stage current = null;
            for (Stage stage : prev) {
                if (stage.blocked) {
                    if (current == null) {
                        current = stage;
                    } else {
                        if (stage.blockStart < current.blockStart) {
                            current = stage;
                        }
                    }

                }
            }
            if (current != null) {
                current.unblock(currentTime);
                eventList.addAll(current.update(currentTime));
            }

            // Calculate production time for item
            double d = r.nextDouble();
            double P = M + N * (d - 0.5);
            eventList.add(new Event(currentTime + P, this));
        } else {
            // Starve
            starved = true;
            starveStart = currentTime;
            return eventList;
        }

        return  eventList;
    }

    public void unblock(double currentTime) {
        blocked = false;
        blockTime += currentTime - blockStart;
    }

    public void unstarve(double currentTime) {
        starved = false;
        starveTime += currentTime - starveStart;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(id);

        double percent = 100 * (1 - (blockTime + starveTime) / timeLimit);
        String value = String.format("%,.2f", percent);
        result.append(spaces(13 - id.length() - value.length()));
        result.append(value);
        result.append("%");

        value = String.format("%,.2f", starveTime);
        result.append(spaces(14 - value.length()));
        result.append(value);

        value = String.format("%,.2f", blockTime);
        result.append(spaces(13 - value.length()));
        result.append(value);

        return result.toString();
    }

    private String spaces(int amount) {
        StringBuilder whitespace = new StringBuilder(" ");
        for (int i = 0; i < amount; i++){
            whitespace.append(" ");
        }

        return whitespace.toString();
    }
}
