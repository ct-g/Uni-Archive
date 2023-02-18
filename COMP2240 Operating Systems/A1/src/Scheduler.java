// Conor Tumbers
// c3190729@uon.edu.au
// COMP2240, Assignment 1
// Last modified 06/09/19

import java.util.ArrayList;
import java.util.LinkedList;

public abstract class Scheduler {
    private ArrayList<Process> processes;
    private int dispTime;
    private int time;
    private String algorithmName;

    LinkedList<Process> processQueue = new LinkedList<>();

    Process execProcess; // Process that is currently being executed

    private double avgTurnaround; // Average execution time
    private double avgWait;  // Average wait time

    /////////////////////////////////////////
    // Constructor
    /////////////////////////////////////////
    Scheduler(ArrayList<Process> processes, int dispTime, String name) {
        this.processes = processes;
        this.dispTime = dispTime;
        this.algorithmName = name;

        this.avgTurnaround = 0;
        this.avgWait = 0;
        this.time = 0;
    }

    /////////////////////////////////////////
    // Scheduling
    /////////////////////////////////////////
    protected abstract void dispatch ();

    void schedule() {
        boolean finished = false;

        System.out.println(algorithmName + ":");

        // Setup queue for when time = 0
        updateQueue();
        // Loop continues until all processes have finished executing
        while (!finished) {
            // Whether the dispatcher is run or not is determined in the dispatch() function
            dispatch();

            // Remove executing process if it has finished execution
            if (execProcess != null && execProcess.getExecTime() == execProcess.getExecSize()) {
                execProcess = null;
            }

            // Check if all processes have finished executing
            for (Process currentProcess : processes) {
                if (currentProcess.getExecTime() != currentProcess.getExecSize()) {
                    finished = false;
                    break;
                } else {
                    finished = true;
                }
            }
        }

        finish();
    }

    // Update timers and the arrival queue for the time in which a process is executing
    void execute() {
        increaseTime();

        // Update time spent processing
        if (execProcess != null) {
            // Don't increment process time if the process is finished
            if (execProcess.getExecTime() < execProcess.getExecSize()) {
                execProcess.incrementExecTime();
            }
        }

        updateWait();

        updateQueue();
    }

    // Time elapsed while the dispatcher was running
    void dispatchTimeElapsed() {
        increaseTime();

        updateWait();

        if (execProcess == null) {
            // Retrieve the next process
            execProcess = processQueue.poll();
        } else {
            execProcess.incrementWaitTime();
        }

        updateQueue();
    }

    private void updateWait() {
        for (Process currentProcess : processes) {
            // Update waitTime if process is not executing, has arrived and has not finished
            if (currentProcess != execProcess && getTime() > currentProcess.getArriveTime()
                    && currentProcess.getExecSize() != currentProcess.getExecTime()) {
                currentProcess.incrementWaitTime();
            }
        }
    }

    private void updateQueue() {
        // Add any newly arriving processes to the primary queue
        for (Process currentProcess : processes) {
            if (currentProcess.getArriveTime() == getTime() && currentProcess.getWaitTime() == 0) {
                processQueue.add(currentProcess);
            }
        }
    }

    // Processes are reset when the scheduler concludes so that another scheduler can operate
    // on the processes
    private void resetProcessTime() {
        for (Process currentProcess : processes) {
            currentProcess.resetValues();
        }
    }

    // Time the process begins executing when selected by the dispatcher from a queue
    void writeStartTime(Process currentProcess) {
        System.out.println("T" + getTime() + ": " + currentProcess.getID());
    }

    // Run at the end of schedule() to write out results and reset process values so that
    // another scheduling algorithm can run
    private void finish() {
        writeResults();
        calculateAverages();
        resetProcessTime();
    }

    /////////////////////////////////////////
    // Results
    /////////////////////////////////////////
    private void writeResults () {
        System.out.println();
        System.out.println("Process Turnaround Time Waiting Time");
        for (Process currentProcess : processes) {
            // Each process and its turnaround & waiting times

            // Construct the string
            String output;
            int turnaround = currentProcess.getExecTime() + currentProcess.getWaitTime();
            output = currentProcess.getID();
            output += spaces(7 - currentProcess.getID().length()); // Spaces to align table columns correctly
            output += String.valueOf(turnaround);
            output += spaces(15 - String.valueOf(turnaround).length());
            output += String.valueOf(currentProcess.getWaitTime());

            // Print it to command line
            System.out.println(output);
        }
        System.out.println();
    }

    // Generate a string of an arbitrary amount of spaces
    private String spaces(int amount) {
        StringBuilder whitespace = new StringBuilder(" ");
        for (int i = 0; i < amount; i++){
            whitespace.append(" ");
        }

        return whitespace.toString();
    }

    // Calculate average turnaround time and wait time for the scheduling algorithm
    // Output in the summary at conclusion of main()
    private void calculateAverages() {
        avgTurnaround = 0;
        avgWait = 0;

        for (Process currentProcess : processes) {
            avgTurnaround += currentProcess.getExecTime() + currentProcess.getWaitTime();
        }
        avgTurnaround /= processes.size();

        for (Process currentProcess : processes) {
            avgWait += currentProcess.getWaitTime();
        }
        avgWait /= processes.size();
    }

    String summary() {
        String resultsSummary = getAlgorithmName();
        String turnaroundSummary = String.format("%.2f", getAvgTurnaround());
        String waitSummary = String.format("%.2f", getAvgWait());

        resultsSummary += spaces(15 - getAlgorithmName().length());
        resultsSummary += turnaroundSummary;
        resultsSummary += spaces(25 - turnaroundSummary.length());
        resultsSummary += waitSummary;

        return resultsSummary;
    }

    /////////////////////////////////////////
    // Accessors and Mutators
    /////////////////////////////////////////
    private int getTime() {
        return time;
    }

    private void increaseTime() {
        time++;
    }

    int getDispTime(){
        return dispTime;
    }

    private String getAlgorithmName() {
        return algorithmName;
    }

    private double getAvgTurnaround() {
        return avgTurnaround;
    }

    private double getAvgWait() {
        return avgWait;
    }
}
