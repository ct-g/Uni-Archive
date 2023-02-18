import java.util.ArrayList;
import java.util.LinkedList;

abstract class MemoryManager {

    private int quantum;

    // First dimension contains a stored page
    // Second dimension stores time last used for LRU Policy & use bit for Clock Policy
    int[][] memory;
    int time;

    private LinkedList<Process> ioQueue = new LinkedList<>(); // Processes waiting for page to be loaded into memory
    private LinkedList<Process> sortQueue = new LinkedList<>(); // Intermediate queue to sort processes that page fault at the same time
    private LinkedList<Process> processQueue = new LinkedList<>(); // Processes waiting to be executed

    private Process execProcess;

    ////////////////////////////////////////////////
    // Constructor
    ////////////////////////////////////////////////

    MemoryManager(ArrayList<Process> processList, int frames, int quantum) {
        this.processQueue.addAll(processList);
        this.time = 0;
        this.memory = new int[frames][2];
        this.quantum = quantum;
        for (int i = 0; i < frames; i++) {
            memory[i][0] = -1;
        }
    }

    ////////////////////////////////////////////////
    // Abstract Methods
    ////////////////////////////////////////////////

    abstract void updateMemory(Process process);

    abstract boolean notInMemory(Process process, int pageNo);

    ////////////////////////////////////////////////
    // Methods
    ////////////////////////////////////////////////

    void execute() {
        while(processQueue.size() > 0 || ioQueue.size() > 0 || execProcess != null) {
            // Process IO requests
            if (ioQueue.peek() != null) {
                while (ioQueue.peek().getRequestServedTime() == time) {
                    if (ioQueue.peek() != null) {
                        updateMemory(ioQueue.peek());
                    }
                    sortQueue.add(ioQueue.poll());
                    if (ioQueue.peek() == null) {
                        break;
                    }
                }
                // Sort processes that have completed an io request by pID
                // before adding them to the ready queue
                if (sortQueue.peek() != null) {
                    sortQueue.sort(Process::compareTo);
                    processQueue.addAll(sortQueue);
                    sortQueue.clear();
                }
            }

            // Check if executing process is finished
            if (execProcess != null && execProcess.getCurrentInstruction() == execProcess.getInstructionCount()) { // Check if process is finished
                execProcess.setTotalTime(time);
                execProcess = null;
            }
            // Check if executing process has reached allocated CPU time
            if (execProcess != null && execProcess.getTimeExecuting() >= quantum) { // Check if time quantum has expired
                execProcess.resetTimExecuting();
                processQueue.add(execProcess);
                execProcess = null;
            }
            // Check if executing process needs to issue a page fault
            if (execProcess != null) {
                if (notInMemory(execProcess, execProcess.getNextPageRequest())) {
                    // Issue a page fault
                    execProcess.resetTimExecuting();
                    execProcess.setRequestServedTime(time + 6);
                    execProcess.addFaultTime(time);
                    ioQueue.add(execProcess);
                    execProcess = null;
                }
            }

            // Issue a segfault for all queued processes that do not have a page in main memory
            int limit = processQueue.size();
            for (int i = 0; i < limit; i++) {
                if (processQueue.peek() != null) {
                    Process currentProcess = processQueue.peek();
                    // Issue all page faults first
                    if (notInMemory(currentProcess, currentProcess.getNextPageRequest())) {
                        // Issue a page fault
                        currentProcess.setRequestServedTime(time + 6);
                        currentProcess.addFaultTime(time);
                        processQueue.poll();
                        ioQueue.add(currentProcess);
                    }
                } else {
                    break;
                }
            }

            // Begin executing next available process if CPU is available
            if (processQueue.peek() != null && execProcess == null) {
                execProcess = processQueue.poll();
            }
            // Increment time
            if (execProcess != null) {
                execProcess.incrementCurrentInstruction();
                execProcess.incrementTimeExecuting();
            }
            time++;
        }
    }

    // For debugging purposes
    /*
    private String memoryToString() {
        StringBuilder result = new StringBuilder("{");
        for (int i = 0; i < Process.frames; i++) {
            result.append(memory[i][0]);
            result.append(", ");
        }
        return result.toString();
    }
    */
}
