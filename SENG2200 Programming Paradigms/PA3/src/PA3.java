// Conor Tumbers
// c3190729@uon.edu.au
// SENG2200, Assignment 3
// Last modified 25/06/20

public class PA3 {
    public static void main(String[] args) {
        // Default parameter values
        int M = 1000;
        int N = 1000;
        int Qmax = 7;

        // Read user input, use default values if input causes error
        try {
            M = Integer.parseInt(args[0]);
        } catch (Exception e) {
            System.out.println("Input must be integer, " + e.getMessage());
        }

        try {
            N = Integer.parseInt(args[1]);
        } catch (Exception e) {
            System.out.println("Input must be integer, " + e.getMessage());
        }

        try {
            Qmax = Integer.parseInt(args[2]);
        } catch (Exception e) {
            System.out.println("Input must be integer, " + e.getMessage());
        }

        // Set up the production line and initiate the simulation
        ProductionLine factory = new ProductionLine(M, N, Qmax, 10000000);
        factory.produce();
    }
}
