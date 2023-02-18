// Conor Tumbers
// c3190729@uon.edu.au
// COMP2240, Assignment 2, Problem C
// Last modified 11/10/19
// A client that is queued to use the coffee machine

public class A2CClient implements Runnable {
    private String id;
    private int customerNo;
    private int brewTime;
    private A2CTemp type; // Client's preferred temperature
    private int startTime;
    private int finishTime;
    private HEAD usingHead; // Current dispenser head in use
    private boolean finished;

    // Client totals
    static int totalClients = 0;
    private static int totalHot = 0;
    private static int totalCold = 0;

    // These are used to force a temperature change if maxConsecutive clients of the same type
    // have used the machine in a row
    private static int consecutiveTemp = 0;
    private static final int maxConsecutive = 4;
    private static boolean forceChange = false;

    private static int time = 0;
    private static int clientCount = 0; // Count of clients in the turnstile
    // ID numbers of the next hot and cold clients in line
    private static int currentHot = 1;
    private static int currentCold = 1;
    // Object's monitor used to manage coffee machine use
    private static final A2CCoffeeMachine machine = new A2CCoffeeMachine();

    // These 2 objects' monitors are used to sync time between threads
    private static final Object phase1 = new Object();
    private static final Object phase2 = new Object();

    A2CClient(String id, int brewTime) {
        this.id = id;
        this.customerNo = Integer.parseInt(id.substring(1));

        this.brewTime = brewTime;

        this.startTime = -1;
        this.finishTime = -1;

        this.usingHead = HEAD.NONE;
        this.finished = false;

        if (id.charAt(0) == 'C') {
            type = A2CTemp.COLD;
        } else if (id.charAt(0) == 'H') {
            type = A2CTemp.HOT;
        } else {
            System.out.println("No order type found, setting to hot");
            type = A2CTemp.HOT;
        }
    }

    @Override
    public void run() {
        // Calculate total of each client type
        synchronized (machine) {
            if (type == A2CTemp.HOT) {
                totalHot++;
            } else if (type == A2CTemp.COLD) {
                totalCold++;
            }
        }
        // Queue for coffee
        while (!finished) {
            // Check for a free head if not currently using one
            if (usingHead == HEAD.NONE) {
                // Only permit the next hot and cold customer in line to check for a free dispenser head
                if ((customerNo == currentCold && type == A2CTemp.COLD) || (customerNo == currentHot && type == A2CTemp.HOT)) {
                    synchronized (machine) {
                        // Look for a dispenser head if it is the right temperature
                        // and a temperature change is not about to occur
                        if (A2CCoffeeMachine.getTemp() == type && !forceChange) {
                            // Begin using a dispenser head
                            if (machine.h1Available) {
                                usingHead = HEAD.ONE;
                                machine.h1Available = false;
                                start();
                                System.out.println("(" + time + ") " + id + " uses dispenser 1 " + "(time: " + brewTime + ")");
                            } else if (machine.h2Available) {
                                usingHead = HEAD.TWO;
                                machine.h2Available = false;
                                start();
                                System.out.println("(" + time + ") " + id + " uses dispenser 2 " + "(time: " + brewTime + ")");
                            }
                        } else if (!forceChange) {
                            // Force a temp change when one client type (temperature) has had many consecutive coffees
                            if (consecutiveTemp > maxConsecutive) {
                                consecutiveTemp = 0;
                                forceChange = true;
                            }
                        }
                    }
                }
            }

            // Two phase barrier used to increment time
            synchronized (phase1) {
                clientCount++;
                if (clientCount == totalClients) {
                    time++;
                    phase1.notifyAll();
                } else {
                    try {
                        phase1.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                // Stop using machine when finished
                // Determine this between barrier phases so as to not interfere with totalClients
                // before phase1
                synchronized (machine) {
                    if (time == finishTime) {
                        // Stop using dispenser head
                        // Change the temperature if both heads are now available
                        if (usingHead == HEAD.ONE) {
                            machine.h1Available = true;
                            if (machine.h2Available) {
                                changeTemp();
                            }
                        } else if (usingHead == HEAD.TWO) {
                            machine.h2Available = true;
                            if (machine.h1Available) {
                                changeTemp();
                            }
                        }
                        totalClients--;
                        finished = true;

                        // Final line of output when last customer is finished
                        if (totalClients == 0) {
                            System.out.println("(" + time + ") DONE");
                        }
                    }
                }
            }

            synchronized (phase2) {
                clientCount--;
                if (clientCount == 0) {
                    phase2.notifyAll();
                } else {
                    try {
                        phase2.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void start() {
        startTime = time;
        finishTime = startTime + brewTime;
        if (type == A2CTemp.COLD) {
            currentCold++;
        } else if (type == A2CTemp.HOT) {
            currentHot++;
        }
        consecutiveTemp++;
    }

    private void changeTemp() {
        if (type == A2CTemp.HOT) {
            if (currentCold <= totalCold) { // Only change temp if there are customers of the other type waiting
                A2CCoffeeMachine.invertTemp();
                forceChange = false;
                consecutiveTemp = 0;
            }
        } else {
            if (currentHot <= totalHot) {
                A2CCoffeeMachine.invertTemp();
                forceChange = false;
                consecutiveTemp = 0;
            }
        }
    }

    @Override
    public String toString() {
        return (id + ", " + brewTime + ", " + type);
    }
}

enum HEAD {
    NONE, ONE, TWO
}
