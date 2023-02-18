// Conor Tumbers
// c3190729@uon.edu.au
// COMP2240, Assignment 1
// Last modified 06/09/19

import java.util.ArrayList;

public class A1 {
    public static void main(String[] args) {
        /////////////////////////////////////////
        // Text File Input
        /////////////////////////////////////////
        ReadFile reader;
        ArrayList<Process> processes;
        int dispTime;

        try {
            reader = new ReadFile(args[0]);
        } catch (Exception error) {
            System.out.println("No command line argument found. Error: " + error.getMessage());
            return;
        }

        try {
            processes = reader.getProcesses();
            if (processes.size() == 0) {
                System.out.println("No processes found in file.");
                return;
            }
        } catch (Exception error) {
            System.out.println("Unable to parse process data. Exiting Program. Error: " + error.getMessage());
            return;
        }
        dispTime = reader.getDispTime();

        /////////////////////////////////////////
        // Scheduling Algorithms
        /////////////////////////////////////////
        FCFS fcfs = new FCFS(processes, dispTime);
        fcfs.schedule();

        RR rr = new RR(processes, dispTime);
        rr.schedule();

        FB fb = new FB(processes, dispTime);
        fb.schedule();

        NRR nrr = new NRR(processes, dispTime);
        nrr.schedule();

        /////////////////////////////////////////
        // Write Summary
        /////////////////////////////////////////
        System.out.println("Summary");
        System.out.println("Algorithm       Average Turnaround Time   Average Waiting Time");
        System.out.println(fcfs.summary());
        System.out.println(rr.summary());
        System.out.println(fb.summary());
        System.out.println(nrr.summary());
    }
}
