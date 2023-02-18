// Conor Tumbers
// c3190729@uon.edu.au
// COMP2240, Assignment 2, Problem A
// Last modified 11/10/19
// Farmers that continually cross a bridge, back and forth

import java.util.concurrent.Semaphore;

public class A2AFarmer implements Runnable {
    // Semaphore initialised with FCFS blocking queue to prevent starvation
    private static Semaphore mutex = new Semaphore(1, true);

    private static final int maxSteps = 15;
    private static final int stepRate = 5; // How often to print out steps progress
    private static int neonCount = 0; // Total amount of farmers that have crossed

    static boolean keepRunning = true;

    private String id;
    private int steps;
    private A2AIsland location;

    A2AFarmer(int number, A2AIsland island){
        this.steps = 0;

        // Set ID based on island of origin
        if (island == A2AIsland.NORTH) {
            this.id = "N_Farmer" + number;

        } else if (island == A2AIsland.SOUTH) {
            this.id = "S_Farmer" + number;
        } else {
            System.out.println("No island found, setting to North island origin");
            this.id = "N_Farmer" + number;
        }

        this.location = island;
    }

    @Override
    public void run() {
         try {
                // Farmers cross the bridge constantly
                // Execution can be ended by hitting the ENTER key
                // This keyboard input is handled in A2A main()
                while(keepRunning) { // Farmers cross the bridge constantly
                    if (location == A2AIsland.NORTH) {
                        System.out.println(id + ": Waiting for bridge. Going towards South.");
                    } else if (location == A2AIsland.SOUTH) {
                        System.out.println(id + ": Waiting for bridge. Going towards North.");
                    }

                    mutex.acquire();
                        // Cross the bridge
                        for (int i = 0; i < maxSteps; i++) {
                            steps++;
                            if(steps % stepRate == 0 && steps != maxSteps) {
                                System.out.println(id + ": Crossing bridge Step " + steps +".");
                            }
                        }
                        steps = 0;
                        System.out.println(id + ": Across the bridge.");
                        updateNeon();
                    mutex.release();

                    // Update location
                    if (location == A2AIsland.NORTH) {
                        location = A2AIsland.SOUTH;
                    } else if (location == A2AIsland.SOUTH) {
                        location = A2AIsland.NORTH;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }

    private void updateNeon() {
        neonCount++;
        System.out.println("NEON = " + neonCount);
    }

    @Override
    public String toString() {
        return ("ID: " + id + ", steps: " + steps);
    }
}
