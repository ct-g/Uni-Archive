import java.util.ArrayList;

public class A3 {

    public static void main(String[] args) {
        // Initialise variables with default values
        int frames = 30;
        int quantum = 3;

        ArrayList<Process> processList = new ArrayList<>();

        // Read in frames and quantum values
        if (args.length >= 2) {
            frames = Integer.parseInt(args[0]);
            quantum = Integer.parseInt(args[1]);
        } else {
            System.out.println("Insufficient parameters supplied.");
            return;
        }

        // Read files into processes
        int processCount = 0;
        for (int i = 2; i < args.length; i++) {
            processList.add(new Process(i - 1, args[i]));
            processCount++;
        }

        // Allocate each process the same amount of frames
        Process.frames = frames / processCount;

        if (Process.frames == 0) {
            System.out.println("Less than 1 frame allocated per process, cancelling execution");
            return;
        }

        // Execute simulations and print results
        System.out.println("LRU - Fixed:");
        System.out.println("PID  Process Name      Turnaround Time  # Faults  Fault Times");

        LRUPolicy lru = new LRUPolicy(processList, frames, quantum);
        lru.execute();

        for (Process process : processList) {
            System.out.println(process);
            // Set process up for clock policy algorithm
            process.resetProcess();
            process.beginClock();
        }

        System.out.println();
        System.out.println("------------------------------------------------------------");
        System.out.println();

        System.out.println("Clock - Fixed:");
        System.out.println("PID  Process Name      Turnaround Time  # Faults  Fault Times");

        ClockPolicy clockPolicy = new ClockPolicy(processList, frames, quantum);
        clockPolicy.execute();

        for (Process process : processList) {
            System.out.println(process);
        }
    }
}
