// Conor Tumbers
// c3190729@uon.edu.au
// COMP2240, Assignment 2, Problem B
// Last modified 11/10/19
// Customers that arrive at the parlour to have ice cream

import java.util.concurrent.Semaphore;

public class A2BCustomer implements Runnable {
    private static final int capacity = 4; // Maximum amount of customers that can be in the parlour at once
    private static int time = 0;
    private static int countRendezvous = 0; // Used to synchronize time in the turnstile system
    private static int customerCount = 0; // Amount of customers currently in the parlour
    private static boolean atCapacity = false; // True when 4 customers are seated at once
    static int totalThreads = 0;
    private static int queuedThreads = 0;

    // Two phase turnstile system used to synchronize time
    private static Semaphore turnstile1 = new Semaphore(0);
    private static Semaphore turnstile2 = new Semaphore(1);
    // Mutex for accessing shared variables
    private static Semaphore mutex = new Semaphore(1);
    // Representation of available seats in the parlour
    private static Semaphore seat = new Semaphore(4, true); // True ensures FCFS queue for customers

    // These are used to prevent customers jumping ahead in the queue if multiple are waiting
    // i.e. starvation prevention
    private static int currentMax = 4;
    private boolean incrementCurrentMax = false;

    // Customer specific variables
    private int arriveTime;
    private int eatTime;
    private int seatTime;
    private int leaveTime;
    private String id;
    private int customerNo; // Derived from id
    private boolean finished = false;
    private boolean release = false;

    A2BCustomer(int arriveTime, int eatTime, String id) {
        this.arriveTime = arriveTime;
        this.eatTime = eatTime;
        this.id = id;
        this.customerNo = Integer.parseInt(id.substring(1));
        this.seatTime = -1;
        this.leaveTime = -1;
    }

    @Override
    public void run () {
        try {
            while (!finished) {
                mutex.acquire();
                if (time >= arriveTime && seatTime == -1 && customerNo <= currentMax) {
                    incrementCurrentMax = true; // Signal that currentMax is to be incremented during turnstile section
                    if (!atCapacity) {
                        // Take a seat
                        arrive();
                        seat.acquire();
                    } else {
                        // Go through first phase of turnstile
                        // before blocking on the seat semaphore
                        countRendezvous++;
                        if (countRendezvous == totalThreads) {
                            time++; // Last thread increments time
                            turnstile2.acquire();
                            turnstile1.release();
                        }

                        // Remove self from total thread count
                        totalThreads--;
                        queuedThreads++;
                        countRendezvous--;
                        mutex.release();

                        seat.acquire(); // Wait for a seat

                        mutex.acquire();
                        arrive(); // Take a seat
                    }
                }
                mutex.release();

                //////////////////////////////
                // TURNSTILE
                //////////////////////////////

                // Increment time
                // A two phase turnstile setup allows the last thread through to increment time
                // ensuring it is only updated once per cycle
                // Phase 1
                mutex.acquire();
                countRendezvous++;
                if (countRendezvous == totalThreads) {
                    time++; // Last thread increments time
                    turnstile2.acquire();
                    turnstile1.release();
                }
                mutex.release();

                // Wait at first turnstile
                turnstile1.acquire();
                turnstile1.release();

                // Check if it is time for this customer to leave
                // This is performed between the two turnstile phases so that deadlock does not occur
                mutex.acquire();
                // Update total threads when finished
                if (time == leaveTime) {
                    customerCount--;
                    if (customerCount == 0 && atCapacity) {
                        atCapacity = false;
                        // Update totalThreads and queuedThreads depending on how many queued customers
                        // are given the ability to take a seat
                        if (queuedThreads > capacity) {
                            totalThreads = totalThreads + capacity;
                            queuedThreads -= capacity;
                        } else {
                            totalThreads = totalThreads + queuedThreads;
                            queuedThreads = 0;
                        }
                        release = true; // Set this thread to release seats after second phase of turnstile system
                    }
                    finished = true;
                    totalThreads--;
                }

                // Increment current max if signalled near the start of the loop
                if (incrementCurrentMax) {
                    incrementCurrentMax = false;
                    currentMax++;
                }
                mutex.release();

                // Phase 2
                mutex.acquire();
                countRendezvous--;
                if (countRendezvous == 0) {
                    turnstile1.acquire();
                    turnstile2.release();
                }
                mutex.release();

                // Wait at second turnstile
                turnstile2.acquire();
                turnstile2.release();

                // Let waiting customers enter if all 4 previous customers have left
                if (release) {
                    release = false;
                    seat.release(capacity);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void arrive() {
        customerCount++;
        if (customerCount == capacity) {
            atCapacity = true;
        }
        seatTime = time;
        leaveTime = seatTime + eatTime;
    }

    @Override
    public String toString () {
        return (id + spaces(14 - id.length()) + arriveTime +
                spaces(9 - String.valueOf(arriveTime).length()) + seatTime +
                spaces(7 - String.valueOf(seatTime).length()) + leaveTime);
    }

    private String spaces(int amount) {
        StringBuilder whitespace = new StringBuilder(" ");
        for (int i = 0; i < amount; i++){
            whitespace.append(" ");
        }

        return whitespace.toString();
    }
}