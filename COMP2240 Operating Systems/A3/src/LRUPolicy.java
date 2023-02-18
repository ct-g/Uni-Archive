import java.util.ArrayList;

public class LRUPolicy extends MemoryManager{
    LRUPolicy(ArrayList<Process> processList, int frames, int quantum) {
        super(processList, frames, quantum);
    }

    @Override
    protected void updateMemory(Process process) {
        // Check for blank addresses
        for (int i = process.getFirstAddress(); i <= process.getFinalAddress(); i++) {
            if (memory[i][0] == -1) {
                memory[i][0] = process.getNextPageRequest();
                memory[i][1] = time;
                return;
            }
        }
        // If none are found, place the page in the least recently used frame
        int index = process.getFirstAddress();
        for (int i = index + 1; i <= process.getFinalAddress(); i++) {
            if (memory[i][1] < memory[index][1]) {
                index = i;
            }
        }

        memory[index][0] = process.getNextPageRequest();
        memory[index][1] = time;
    }

    @Override
    protected boolean notInMemory(Process process, int pageNo) {
        int firstAddress = process.getFirstAddress();
        // Search memory for requested page, only search memory belonging to the process
        for (int i = firstAddress; i <= process.getFinalAddress(); i++) {
            if (memory[i][0] == pageNo) {
                memory[i][1] = time;
                return false;
            }
        }

        return true;
    }
}