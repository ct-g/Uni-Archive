// Conor Tumbers
// c3190729@uon.edu.au
// COMP2240, Assignment 1
// Last modified 06/09/19

import java.util.ArrayList;

public class NRR extends Scheduler{
    // Time quantum for the RR algorithm
    // The length of a time slice is determined by a process's priority
    private int quantum;

    NRR(ArrayList<Process> processes, int dispTime) {
        super(processes, dispTime, "NRR");
        this.quantum = 0;
    }

    @Override
    protected void dispatch() {
        // Do not run dispatcher if time quantum hasn't elapsed or there are no processes waiting
        if (execProcess != null && (quantum < execProcess.getPriority() || processQueue.peek() == null)) {
            execute();
            quantum++;
            return;
        }

        // Reset quantum for the next process
        quantum = 0;

        // Add current process to end of queue
        if (execProcess != null) {
            processQueue.add(execProcess);
            execProcess = null; // Deallocate process from execution
        }

        // Update time lapsed whilst dispatching took place
        for (int i = 0; i < getDispTime(); i++) {
            dispatchTimeElapsed();
        }

        // In the case the dispatcher time is zero, fetch the next process as dispatchTimeElapsed won't be called
        if (getDispTime() == 0) {
            execProcess = processQueue.poll();
        }

        // Retrieve the next process
        // execProcess = processQueue.poll();
        // Set time quantum of process depending on if it has been in the queue previously
        if (execProcess != null) {
            if (execProcess.getPriority() == 0) {
                for (int i = 0; i < 4; i++) {
                    execProcess.incrementPriority();
                }
            // Time quantum minimum value is 2ms
            } else if (execProcess.getPriority() == 4 || execProcess.getPriority() == 3) {
                execProcess.decrementPriority();
            }
        }

        if (execProcess != null) {
            writeStartTime(execProcess);
        }
    }
}
