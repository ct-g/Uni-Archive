// Conor Tumbers
// c3190729@uon.edu.au
// COMP2240, Assignment 2, Problem C
// Last modified 11/10/19
// Reads input file and initialises threads

import java.io.File;
import java.util.Scanner;

public class A2C {
    // Read the input file and create threads
    public static void main(String[] args) {
        File inputFile = new File (args[0]);
        Scanner read;
        try {
            read = new Scanner(inputFile);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }

        String line = read.nextLine();
        A2CClient.totalClients = Integer.parseInt(line);

        while (read.hasNextLine()) {
            line = read.nextLine();

            if (line.trim().equals("")) { // Stop reading the file when an empty line is encountered
                break;
            }

            int index = line.indexOf(" ");
            String id = line.substring(0, index);
            int brewTime = Integer.parseInt(line.substring(index + 1).trim());

            new Thread(new A2CClient(id, brewTime)).start();
        }

    }
}
