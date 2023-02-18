// Conor Tumbers
// c3190729@uon.edu.au
// COMP2240, Assignment 1
// Last modified 06/09/19

import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;

public class ReadFile {

    private File inputFile;
    private ArrayList<Process> processes = new ArrayList<>();


    ReadFile(String fileName) {
        this.inputFile = new File(fileName);
    }

    public ArrayList<Process> getProcesses() throws Exception{
        // Setup variables for process constructor with default values
        String ID = "none";
        int arriveTime = 0;
        int execSize = 0;

        // If file cannot be scanned, error is handled in main()
        Scanner input = new Scanner(inputFile);

        // Create the array list of processes using input from the file
        while (input.hasNextLine()){
            String line = input.nextLine();
            int index;

            if (line.contains("ID:")) {
                index = line.indexOf(":") + 1;
                ID = line.substring(index).trim();
            } else if (line.contains("Arrive:")) {
                index = line.indexOf(":") + 1;
                arriveTime = Integer.parseInt(line.substring(index).trim());
            } else if (line.contains("ExecSize:")) {
                index = line.indexOf(":") + 1;
                execSize = Integer.parseInt(line.substring(index).trim());
            } else if (line.contains("END") && !ID.equals("none")) { // Only add a new process if one has been found
                processes.add(new Process(ID, arriveTime, execSize));
                // Reset values  to empty
                ID = "none";
                arriveTime = 0;
                execSize = 0;
            }
        }

        input.close();

        // Sorts processes by ID so that processes are in order when printing results of scheduling
        processes.sort(Process::compareTo);

        return processes;
    }

    int getDispTime () {
        // Set the scanner to read the file
        Scanner input;

        try {
            input = new Scanner(inputFile);
        } catch (Exception error) {
            System.out.println("Unable to parse file. Setting dispatch value to a default of 1. Error: " + error.getMessage());
            return 1;
        }

        while (input.hasNextLine()) {
            String line = input.nextLine();
            String data;
            int index;

            if (line.contains("DISP:")) {
                index = line.indexOf(":") + 1;
                data = line.substring(index).trim();
                try { // Handle case of extracted string containing no number values
                    if (Integer.parseInt(data) >= 0) { // Only accept dispatch values >= 0
                        return (Integer.parseInt(data));
                    }
                } catch (Exception error) {
                    System.out.println("DISP keyword found but unable to find dispatch time value. Error: " + error.getMessage());
                }
            }
        }

        System.out.println("No valid dispatcher time found, setting it to a default value of 1.");

        return 1;
    }
}
