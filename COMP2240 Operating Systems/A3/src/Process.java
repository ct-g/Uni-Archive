import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class Process implements Comparable{
    private int pID;
    private String name;
    private int instructionCount;
    private int totalTime; // Turnaround time

    static int frames; // All processes are allocated the same amount of frames

    private ArrayList<Integer> pageRequests = new ArrayList<>();

    private int currentInstruction;
    private int timeExecuting; // Current time in processor, set to zero when removed from CPU
    private int clockHand; // Current memory reference when using clock method

    // Time at which the last IO request will be fulfilled
    private int requestServedTime;

    private ArrayList<Integer> faultTimes= new ArrayList<>();

    Process(int pID, String input) {
        this.pID = pID;
        this.name = input;
        this.instructionCount = 0;
        this.currentInstruction = 0;
        this.totalTime = 0;
        this.requestServedTime = -1;

        // Read in page requests from input file
        File inputFile = new File(input);
        Scanner read;
        try {
            read = new Scanner(inputFile);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }

        String line = read.nextLine();
        while (!line.equals("begin")) { // Skip until first page request number
            line = read.nextLine();
        }

        while (read.hasNextLine()) {
            line = read.nextLine();

            if (line.contains("end")) {
                break;
            }

            this.pageRequests.add(Integer.parseInt(line));
            instructionCount++;
        }
    }

    @Override
    public int compareTo(Object T) {
        return -Integer.compare(((Process) T).pID, this.pID);
    }

    ////////////////////////////////////////////////
    // toString Building
    ////////////////////////////////////////////////
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(String.valueOf(pID));
        result.append(spaces(4 - String.valueOf(pID).length())).append(name);
        result.append(spaces(17 - name.length())).append(totalTime);
        result.append(spaces(16 - String.valueOf(totalTime).length())).append(faultTimes.size());
        result.append(spaces(9 - String.valueOf(faultTimes.size()).length())).append("{");
        for (Integer fault : faultTimes) {
            result.append(fault);
            if(faultTimes.indexOf(fault) != faultTimes.size() - 1) {
                result.append(", ");
            }
        }
        result.append("}");

        return result.toString();
    }

    private String spaces(int amount) {
        StringBuilder whitespace = new StringBuilder(" ");
        for (int i = 0; i < amount; i++){
            whitespace.append(" ");
        }

        return whitespace.toString();
    }

    ////////////////////////////////////////////////
    // Accessors & Mutators
    ////////////////////////////////////////////////
    // Returns the index of the process's first frame in main memory
    int getFirstAddress() {
        return ((pID -1) * frames);
    }

    int getFinalAddress() {
        return (getFirstAddress() + frames - 1);
    }

    //
    int getNextPageRequest() {
        return pageRequests.get(currentInstruction);
    }

    //
    int getRequestServedTime() {
        return requestServedTime;
    }

    void setRequestServedTime(int time) {
        requestServedTime = time;
    }

    //
    int getTimeExecuting() {
        return timeExecuting;
    }

    void incrementTimeExecuting() {
        timeExecuting++;
    }

    void resetTimExecuting() {
        timeExecuting = 0;
    }

    //
    void incrementCurrentInstruction() {
        if (currentInstruction < pageRequests.size()) {
            currentInstruction++;
        }
    }

    int getCurrentInstruction() {
        return currentInstruction;
    }

    //
    int getInstructionCount() {
        return instructionCount;
    }

    //
    void setTotalTime(int time) {
        totalTime = time;
    }

    //
    void addFaultTime(int time) {
        faultTimes.add(time);
    }

    //
    void beginClock() {
        clockHand = getFirstAddress();
    }

    void incrementClock() {
        if (clockHand < getFinalAddress()) {
            clockHand++;
        } else {
            beginClock(); // Loop back to the first address
        }
    }

    int getClockHand() {
        return clockHand;
    }

    //
    void resetProcess() {
        totalTime = 0;
        currentInstruction = 0;
        timeExecuting = 0;
        requestServedTime = 0;
        faultTimes.clear();
    }
}
