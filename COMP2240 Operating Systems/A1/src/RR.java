// Conor Tumbers
// c3190729@uon.edu.au
// COMP2240, Assignment 1
// Last modified 06/09/19

import java.util.ArrayList;

public class RR extends Scheduler {
    // Time quantum for the RR algorithm
    private int quantum;

    RR(ArrayList<Process> processes, int dispTime) {
        super(processes, dispTime, "RR");
        this.quantum = 0;
    }

    @Override
    protected void dispatch() {
        // Do not run dispatcher if time quantum hasn't elapsed or there are no processes waiting
        // Time slice maximum length is 4ms
        if ((quantum < 4 || processQueue.peek() == null) && execProcess != null) {
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

        if (execProcess != null) {
            writeStartTime(execProcess);
        }
    }
}
