import java.util.ArrayList;

public class ClockPolicy extends MemoryManager {
    ClockPolicy(ArrayList<Process> processList, int frames, int quantum) {
        super(processList, frames, quantum);
    }

    @Override
    protected void updateMemory(Process process) {
        // Check for blank addresses
        for (int i = process.getFirstAddress(); i <= process.getFinalAddress(); i++) {
            if (memory[i][0] == -1) {
                memory[i][0] = process.getNextPageRequest();
                memory[i][1] = 1;
                return;
            }
        }
        // If none are found, place in first address with clock bit set to 0
        boolean found = false;
        while (!found) {
            if (memory[process.getClockHand()][1] == 0) {
                found = true;
            } else {
                // Set the clock bit to 0 to signify it has been passed over
                memory[process.getClockHand()][1] = 0;
                process.incrementClock();
            }
        }
        memory[process.getClockHand()][0] = process.getNextPageRequest();
        memory[process.getClockHand()][1] = 1;
        process.incrementClock();
    }

    @Override
    protected boolean notInMemory(Process process, int pageNo) {
        int firstAddress = process.getFirstAddress();
        // Search memory for requested page, only search memory belonging to the process
        for (int i = firstAddress; i <= process.getFinalAddress(); i++) {
            if (memory[i][0] == pageNo) {
                memory[i][1] = 1;
                return false;
            }
        }

        return true;
    }
}
