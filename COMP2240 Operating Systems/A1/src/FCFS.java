// Conor Tumbers
// c3190729@uon.edu.au
// COMP2240, Assignment 1
// Last modified 06/09/19

import java.util.ArrayList;

public class FCFS extends Scheduler {
    FCFS(ArrayList<Process> processes, int dispTime) {
        super(processes, dispTime, "FCFS");
    }

    @Override
    protected void dispatch() {
        // Do not run dispatcher if a process is executing
        if (execProcess != null) {
            execute();
            return;
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
