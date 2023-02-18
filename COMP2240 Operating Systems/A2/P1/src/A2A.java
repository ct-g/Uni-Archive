// Conor Tumbers
// c3190729@uon.edu.au
// COMP2240, Assignment 2, Problem A
// Last modified 11/10/19
// Reads input and initialises threads

import java.util.Scanner;

public class A2A {

    public static void main(String[] args) throws InterruptedException {
        // Default numbers in the case of no input
        int southNum = 5;
        int northNum = 5;

        for (String arg : args) {
            // Get the number
            int num = Integer.parseInt(arg.substring(2).trim());
            // Assign the number to the corresponding island
            if (arg.contains("N")) {
                northNum = num;
            } else if (arg.contains("S")) {
                southNum = num;
            }
        }

        // Farmers starting on the north island
        for (int i = 1; i <= northNum; i++) {
            new Thread(new A2AFarmer(i, A2AIsland.NORTH)).start();
        }

        // Farmers starting on the island
        for (int i = 1; i <= southNum; i++) {
            new Thread(new A2AFarmer(i, A2AIsland.SOUTH)).start();
        }

        Scanner keyboard = new Scanner(System.in);
        String input = keyboard.nextLine();

        if (input != null) {
            A2AFarmer.keepRunning = false;
        }
    }
}
