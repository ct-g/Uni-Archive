// Conor Tumbers
// c3190729@uon.edu.au
// SENG2250, Assignment 3
// Last modified 15/11/2020

public class Main {

    public static void main(String[] args) {
        // Setup sockets for client-server communication
        int port = 4444;
        Server server;
        Client client;
        try {
            server = new Server(port); // Setup server before client
            client = new Client(server.serverSocket.getInetAddress(), port);
        } catch (Exception exc) {
            System.err.println(exc.getMessage());
            return;
        }

        // Simulate data exchange between a client and a server by running these on separate threads
        new Thread(server).start();
        new Thread(client).start();
    }
}
