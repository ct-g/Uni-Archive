// Conor Tumbers
// c3190729@uon.edu.au
// COMP2240, Assignment 2
// Last modified 11/10/19
// Reads input file, initialises threads and prints results

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class A2B {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Customer    Arrives     Seats   Leaves");
        ArrayList<A2BCustomer> customerList = new ArrayList<>();
        ArrayList<Thread> threadList = new ArrayList<>();

        ////////////////////////////////////////
        // File Input
        ////////////////////////////////////////
        File inputFile = new File (args[0]);
        Scanner read;
        try {
            read = new Scanner(inputFile);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
       }

        while (read.hasNextLine()) {
            String line = read.nextLine();

            int arriveTime = -1;
            int eatTime = -1;

            if (line.contains("END")) {
                break;
            }

            String[] input = new String[3];
            int index = 0;
            int index2= 0;

            index = line.indexOf(" ");
            index2 = line.indexOf(" ", index + 1);
            input[0] = line.substring(0,index).trim();
            input[1] = line.substring(index, index2).trim();
            input[2] = line.substring(index2).trim();

            arriveTime = Integer.parseInt(input[0]);
            eatTime = Integer.parseInt(input[2]);

            //new Thread(new Customer(arriveTime, eatTime, input[1])).start();
            customerList.add(new A2BCustomer(arriveTime, eatTime, input[1]));
        }

        // Update Customer class with total amount of customers
        A2BCustomer.totalThreads = customerList.size();

        // Add threads to the thread list
        for (A2BCustomer customer : customerList) {
            threadList.add(new Thread(customer));
        }

        // Initialise threads
        for (Thread thread : threadList) {
            thread.start();
        }

        // Wait on each thread to finish
        for (Thread thread : threadList) {
            thread.join();
        }

        // Print results
        for (A2BCustomer customer : customerList) {
            System.out.println(customer);
        }
    }
}
