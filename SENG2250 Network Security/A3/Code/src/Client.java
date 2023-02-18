// Conor Tumbers
// c3190729@uon.edu.au
// SENG2250, Assignment 3
// Last modified 15/11/2020

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Random;

public class Client implements Runnable {
    // Socket used to communicate with server
    Socket socket;

    // ID
    BigInteger IDc; // Client
    BigInteger IDs; // Server
    BigInteger SID; // Session

    // Server's RSA public key, used to verify signatures
    private BigInteger rsa_e;
    private BigInteger rsa_n;

    // Fixed Diffie-Hellman paramters
    private final BigInteger dh_p;
    private final BigInteger dh_g;

    public Client(InetAddress hostname, int port) throws IOException {
        socket = new Socket(hostname, port);

        IDc = new BigInteger(64, new Random());

        // 128 byte each
        dh_p = new BigInteger("178011905478542266528237562450159990145232156369120674273274450314442865788737020770612695252123463079567156784778466449970650770920727857050009668388144034129745221171818506047231150039301079959358067395348717066319802262019714966524135060945913707594956514672855690606794135837542707371727429551343320695239");
        dh_g = new BigInteger("174068207532402095185811980123523436538604490794561350978495831040599953488455823147851597408940950725307797094915759492368300574252438761037084473467180148876118103083043754985190983472601550494691329488083395492313850000361646482644608492304078721818959999056496097769368017749273708962006689187956744210730");
    }

    @Override
    public void run() {
        try {
            // Set up writer and reader for socket acccess
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Library with security functions
            Security security = new Security();

            // Send session request to server
            System.out.println("Setup Phase");
            System.out.println("-----------");
            System.out.println("Client to Server: Hello");
            out.println("Hello");

            // Wait for RSA public key
            String input;
            while ((input = in.readLine()) != null) {
                if (input.equals("Key")) {
                    rsa_n = new BigInteger(in.readLine());
                    rsa_e = new BigInteger(in.readLine());

                    // Begin handshake phase by sending client ID
                    System.out.println("Handshake Phase");
                    System.out.println("-------------");
                    System.out.println("Client to Server: IDc=" + IDc);
                    out.println(IDc);
                    break;
                }
            }

            // Receive server and session ID from socket, verifying the signature for both
            IDs = new BigInteger(in.readLine());
            BigInteger signature = new BigInteger(in.readLine());

            if (!security.rsaVerify(IDs, signature, rsa_e, rsa_n)) {
                System.out.println("Server ID signature failed to be verified");
                socket.close();
                return;
            }

            SID = new BigInteger(in.readLine());
            signature = new BigInteger(in.readLine());

            if (!security.rsaVerify(SID, signature, rsa_e, rsa_n)) {
                System.out.println("Session ID signature failed to be verified");
                socket.close();
                return;
            }

            // Diffie-Hellman key exchange
            System.out.println();
            System.out.println("Diffie-Hellman key exchange");

            // Generate random number x, less than p
            BigInteger xc = new BigInteger(1024, new Random());
            while (xc.compareTo(dh_p) >= 0) {
                xc = new BigInteger(1024, new Random());
            }

            // Generate Yc
            BigInteger Yc = security.powmod(dh_g, xc, dh_p);
            System.out.println("Client to server: Yc=" + Yc);
            out.println(Yc);

            BigInteger Ys = new BigInteger(in.readLine());
            signature = new BigInteger(in.readLine());

            System.out.println();
            if (security.rsaVerify(Ys, signature, rsa_e, rsa_n)) {
                System.out.println("Ys signature successfully verified\n");
            } else {
                System.out.println("Ys signature failed to be verified");
                socket.close();
                return;
            }

            // Generate the shared session key k, 128 bytes long
            BigInteger k = security.powmod(Ys, xc, dh_p);

            // Generate the authentication key to be used in data exchange
            BigInteger ka = security.hash(k); // Authentication key, 32 bytes long
            System.out.println("Client authentication key: " + ka);

            // Data Exchange 1 - Client to Server
            // 127 digit hexadecimal number
            // When converted to BigInteger, this will have a 64 byte representation including the sign bit
            String hex = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF" +
                    "FFFFFFFFFFFFFFFFFFFFFFFFAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";

            BigInteger dataC = new BigInteger(hex, 16);

            // Retrieve IV for CBC encryption/decryption
            BigInteger IV = new BigInteger(in.readLine());
            System.out.println("IV=" + IV);
            signature = new BigInteger(in.readLine());
            if (security.rsaVerify(IV, signature, rsa_e, rsa_n)) {
                System.out.println("IV signature successfully verified\n");
            } else {
                System.out.println("IV signature failed to be verified");
                socket.close();
                return;
            }

            System.out.println();
            System.out.println("Data Exchange 1");
            System.out.println("---------------");

            BigInteger ciphertext = security.encrypt(dataC, IV, ka);
            BigInteger tag = security.hmac(ka, ciphertext); // HMAC tag

            // Output texts
            System.out.println("Client: Plaintext=" + dataC);
            System.out.println("Client to Server: Ciphertext=" + ciphertext);
            System.out.println("Client to Server: HMAC=" + tag);
            System.out.println();

            // Send texts to socket
            out.println(ciphertext);
            out.println(tag);

            // Data Exchange 2 - Server to Client
            BigInteger serverText = new BigInteger(in.readLine());
            BigInteger tag2 = new BigInteger(in.readLine());
            Boolean result = security.verifyHmac(ka, serverText, tag2);
            if (!result) {
                System.out.println("Client: Server HMAC verification failed");
                socket.close();
                return;
            } else {
                System.out.println("Client: Server HMAC successfully verified");
            }
            BigInteger plaintext = security.decrypt(serverText, IV, ka);
            System.out.println("Client: plaintext=" + plaintext);
        } catch (Exception e) {
            System.err.println("Client error, " + e.getMessage());
        }
    }
}
