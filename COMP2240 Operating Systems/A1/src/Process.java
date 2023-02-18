// Conor Tumbers
// c3190729@uon.edu.au
// COMP2240, Assignment 1
// Last modified 06/09/19

public class Process implements Comparable<Process> {
    private String ID;
    private int arriveTime; // Time the process is first ready to be dispatched
    private int execSize;  // Time it takes for the process to complete execution

    private int execTime; // Current time spent executing process
    private int waitTime; // Current time spent waiting in a queue

    // Operates as current priority in the FB and NRR algorithms
    private int priority;

    Process(String ID, int arriveTime, int execSize) {
        this.ID = ID;
        this.arriveTime = arriveTime;
        this.execSize = execSize;

        this.execTime = 0;
        this.waitTime = 0;
        this.priority = 0;
    }

    @Override
    public String toString () {
        return ("ID: " + ID + ", Arrival time: " + arriveTime + ", Execution time: " + execSize);
    }

    @Override // Processes are ordered based on process ID number
    public int compareTo(Process process) {
        int index;
        Integer IDNo;

        // Extract the number from the process ID
        index = ID.indexOf("p") + 1;
        IDNo = Integer.parseInt(ID.substring(index).trim());
        System.out.println(IDNo);

        // Extract number from provided process
        index = process.ID.indexOf("p") + 1;
        Integer IDNo2;
        IDNo2 = Integer.parseInt(process.ID.substring(index).trim());

        return (IDNo.compareTo(IDNo2));
    }

    // Accessors and Mutators
    int getArriveTime(){
        return arriveTime;
    }

    int getExecSize() {
        return execSize;
    }

    void incrementExecTime() {
        execTime++;
    }

    int getExecTime() {
        return execTime;
    }

    void incrementWaitTime() {
        waitTime++;
    }

    int getWaitTime() {
        return waitTime;
    }

    void incrementPriority() {
        priority++;
    }

    void decrementPriority() {
        priority--;
    }

    int getPriority() {
        return priority;
    }

    String getID() {
        return ID;
    }

    void resetValues() {
        execTime = 0;
        waitTime = 0;
        priority = 0;
    }
}
