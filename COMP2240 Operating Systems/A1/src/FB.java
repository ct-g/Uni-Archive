// Conor Tumbers
// c3190729@uon.edu.au
// COMP2240, Assignment 1
// Last modified 06/09/19

import java.util.ArrayList;
import java.util.LinkedList;

public class FB extends Scheduler {
    // 5 additional priority levels of queues
    private ArrayList<LinkedList<Process>> processQueues = new ArrayList<>();

    private int quantum;

    FB(ArrayList<Process> Processes, int dispTime) {
        super(Processes, dispTime, "FB (constant)");
        quantum = 0;

        // Generate a queue for each priority level
        for (int i = 0; i < 5; i++) {
            processQueues.add(new LinkedList<>());
        }
    }

    @Override
    protected void dispatch() {
        // lastProcess stores the process that was executed in the last time quantum,
        // so that if a process is chosen twice in a row when there are multiple processes queued,
        // the starting time is only output to console once
        Process lastProcess = null;

        // Do not run dispatcher if (time quantum hasn't elapsed or there are no processes waiting)
        // and there is a process executing
        // Time slice maximum length is 4ms when other processes are queue
        if ((quantum < 4 || !processIsQueued()) && execProcess != null) {
            execute();
            quantum++;
            return;
        }

        // Reset quantum for the next process
        quantum = 0;

        // Add current process to end of queue
        if (execProcess != null) {
            // Priority is capped at 4 so that if the process is from the last queue
            // it re-enters that queue again
            // The last queue essentially operates as a Round Robin
            if (execProcess.getPriority() < 4) {
                execProcess.incrementPriority();
            }
            processQueues.get(execProcess.getPriority()).add(execProcess);
            lastProcess = execProcess;
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

        // If no process was retrieved in dispatchTimeElapsed, retrieve one from a lower priority queue
        if (execProcess == null) {
            for (LinkedList<Process> queue : processQueues) {
                execProcess = queue.poll();
                if (execProcess != null) { // Exit the loop once a process is found
                    break;
                }
            }
        }

        if (execProcess != null && execProcess != lastProcess) {
            writeStartTime(execProcess);
        }
    }

    // Check all priority queues to see if a waiting process exists
    // Return false when no process is found
    private boolean processIsQueued() {
        if (processQueue.peek() != null) {
            return true;
        }
        for (LinkedList<Process> queue : processQueues) {
            if (queue.peek() != null) {
                return true;
            }
        }

        return false;
    }
}
