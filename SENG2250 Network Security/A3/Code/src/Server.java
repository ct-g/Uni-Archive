// Conor Tumbers
// c3190729@uon.edu.au
// SENG2250, Assignment 3
// Last modified 15/11/2020

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class Server implements Runnable {
    // Sockets for message transmission
    ServerSocket serverSocket;
    Socket clientSocket;

    // ID
    BigInteger IDs; // Server
    BigInteger SID; // Session
    BigInteger IDc; // Client

    // Fixed Diffie-Hellman parameters
    private final BigInteger dh_p;
    private final BigInteger dh_g;

    public Server (int port) throws IOException {
        serverSocket = new ServerSocket(port);

        IDs = new BigInteger(64, new Random());
        SID = new BigInteger(64, new Random());

        dh_p = new BigInteger("178011905478542266528237562450159990145232156369120674273274450314442865788737020770612695252123463079567156784778466449970650770920727857050009668388144034129745221171818506047231150039301079959358067395348717066319802262019714966524135060945913707594956514672855690606794135837542707371727429551343320695239");
        dh_g = new BigInteger("174068207532402095185811980123523436538604490794561350978495831040599953488455823147851597408940950725307797094915759492368300574252438761037084473467180148876118103083043754985190983472601550494691329488083395492313850000361646482644608492304078721818959999056496097769368017749273708962006689187956744210730");
    }

    @Override
    public void run() {
        try {
            clientSocket = serverSocket.accept();
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in =  new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            Security security = new Security();
            BigInteger[] pubKey = security.getPublicKey();

            // Wait for session request and respond with RSA public key
            String input;
            while ((input = in.readLine()) != null) {
                if (input.equals("Hello")) {
                    System.out.println("Server to Client: RSA_PK=" + pubKey[0] + "\n");
                    out.println("Key");
                    out.println(pubKey[0]); // n
                    out.println(pubKey[1]); // e
                    break;
                }
                if (input.equals("Exit")) {
                    return;
                }
            }

            IDc = new BigInteger(in.readLine());

            System.out.println("Server to Client: IDs=" + IDs + ", SID=" + SID);
            out.println(IDs);
            out.println(security.rsaSign(IDs));
            out.println(SID);
            out.println(security.rsaSign(SID));

            // Diffie-Hellman key exchange

            // Generate random number x, less than p
            BigInteger xs = new BigInteger(1024, new Random());
            while (xs.compareTo(dh_p) >= 0) {
                xs = new BigInteger(1024, new Random());
            }

            BigInteger Yc = new BigInteger(in.readLine());

            // Generate Ys
            BigInteger Ys = security.powmod(dh_g, xs, dh_p);
            System.out.println("Server to Client: Ys=" + Ys);
            out.println(Ys);
            out.println(security.rsaSign(Ys));

            // Generate the shared session key k
            BigInteger k = security.powmod(Yc, xs, dh_p);

            // Generate the authentication key k'
            BigInteger ka = security.hash(k);
            System.out.println("Server authentication key: " + ka);

            // 16 byte Initialisation Vector
            BigInteger IV = new BigInteger(127, new Random());
            out.println(IV);
            out.println(security.rsaSign(IV));

            // Data Exchange 1 - Client to Server
            BigInteger clientText = new BigInteger(in.readLine());
            BigInteger tag = new BigInteger(in.readLine());

            Boolean result = security.verifyHmac(ka, clientText, tag);
            if (!result) {
                System.out.println("Server: Client HMAC verification failed");
                return;
            } else {
                System.out.println("Server: Client HMAC successfully verified");
            }
            BigInteger plaintext = security.decrypt(clientText, IV, ka);
            System.out.println("Server: plaintext=" + plaintext);

            // Data Exchange 2 - Server to Client
            // 127 digit hexadecimal number + 1 sign bit maps to a 64 byte array (509 bits)
            String hex = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
            BigInteger dataS = new BigInteger(hex, 16);

            System.out.println();
            System.out.println("Data Exchange 2");
            System.out.println("---------------");

            BigInteger ciphertext = security.encrypt(dataS, IV, ka);
            BigInteger tag2 = security.hmac(ka, ciphertext); // HMAC tag

            // Output texts
            System.out.println("Server: Plaintext=" + dataS);
            System.out.println("Server to Client: Ciphertext=" + ciphertext);
            System.out.println("Server to Client: HMAC=" + tag2);
            System.out.println();

            // Send texts to socket
            out.println(ciphertext);
            out.println(tag);
        } catch (Exception e) {
            System.out.println("Server error, " + e.getMessage());
        }
    }
}
