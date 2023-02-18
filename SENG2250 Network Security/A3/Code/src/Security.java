// Conor Tumbers
// c3190729@uon.edu.au
// SENG2250, Assignment 3
// Last modified 15/11/2020

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

public class Security {
    // Fixed size of blocks used for CBC
    private static final int BLOCKSIZE = 16;

    // RSA components
    private final BigInteger p, q, n, e, d;

    // Constants used for HMAC
    private final BigInteger opad, ipad;

    // Cipher used in CBC mode
    private final Cipher AES;

    // Hash function
    private MessageDigest md;

    public Security() throws NoSuchPaddingException, NoSuchAlgorithmException {
        // RSA variables
        p = genPrime();
        q = genPrime();

        n = p.multiply(q);

        BigInteger p_1 = p.subtract(BigInteger.ONE);
        BigInteger q_1 = q.subtract(BigInteger.ONE);

        BigInteger m = p_1.multiply(q_1);

        e = new BigInteger("65537");

        d = e.modInverse(m);

        // HMAC variables, each is 256 bits = 32 bytes long, the same as the shared session key k
        opad = new BigInteger("5c5c5c5c5c5c5c5c5c5c5c5c5c5c5c5c5c5c5c5c5c5c5c5c5c5c5c5c5c5c5c5c", 16);
        ipad = new BigInteger("3636363636363636363636363636363636363636363636363636363636363636", 16);

        // CBC variables
        AES = Cipher.getInstance("AES/ECB/NoPadding");

        // Select hash function
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (Exception exc) {
            System.err.println(exc.getMessage());
        }
    }

    // Sign a message using RSA
    public BigInteger rsaSign(BigInteger message){
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (Exception exc) {
            System.err.println(exc.getMessage());
            return BigInteger.ZERO;
        }

        // Hash the message for signature generation
        md.update(message.toByteArray());

        byte[] hashBytes = md.digest();
        BigInteger hash = new BigInteger(hashBytes);

        // Generate and return the signature
        return powmod(hash, d, n);
    }

    // Verify an RSA signature
    public Boolean rsaVerify(BigInteger message, BigInteger signature, BigInteger e, BigInteger n){
        // Hash the message for signature verification
        md.update(message.toByteArray());

        byte[] hashBytes = md.digest();
        BigInteger hash = new BigInteger(hashBytes);

        // Take the modulo of the hash in order to handle hash values outside the modulo group (such as negatives)
        hash = hash.mod(n);

        // Compare the hash to the result of the powmod operation
        BigInteger result = powmod(signature, e, n);
        return hash.equals(result);
    }

    // Retrieves public key components
    BigInteger [] getPublicKey(){
        return new BigInteger[]{n, e};
    }

    // 1024 bits random primes
    private BigInteger genPrime() {
        return BigInteger.probablePrime(1024, new Random());
    }

    // Fast modular exponentiation
    public BigInteger powmod(BigInteger base, BigInteger exp, BigInteger mod) {
        if (mod.equals(BigInteger.ONE)) {
            return BigInteger.ZERO;
        }

        BigInteger result = BigInteger.ONE;
        while (exp.compareTo(BigInteger.ZERO) > 0) {
            if (exp.and(BigInteger.ONE).equals(BigInteger.ONE)) {
                result = result.multiply(base).mod(mod);
            }

            exp = exp.shiftRight(1);
            base = base.multiply(base).mod(mod);
        }

        return result;
    }

    // Hash the input using SHA-256
    public BigInteger hash(BigInteger input) {
        // Hash the message for signature verification
        md.update(input.toByteArray());

        byte[] hashBytes = md.digest();
        return new BigInteger(hashBytes);
    }

    // XOR two byte arrays
    private byte[] xor(byte[] arr1, byte[] arr2) {
        try {
            if (arr1.length != arr2.length) {
                System.out.println(arr1.length + "" + arr2.length);
                throw new Exception("Cannot XOR, byte arrays are of different size");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new byte[0];
        }

        byte[] result = new byte[arr1.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = (byte) (arr1[i] ^ arr2[i]);
        }

        return result;
    }

    // Generate a HMAC tag
    public BigInteger hmac(BigInteger k, BigInteger message) {
        byte[] oData = xor(opad.toByteArray(), k.toByteArray()); // k xor opad
        byte[] iData = xor(ipad.toByteArray(), k.toByteArray()); // k xor ipad

        md.update(iData);
        byte[] iHash = md.digest(); // Hash(k xor ipad)

        byte[] messageBytes = message.toByteArray();

        // (k xor opad) || H(k xor ipad) || message
        byte[] input = new byte[oData.length + iHash.length + messageBytes.length];

        byte[] result = hash(new BigInteger(input)).toByteArray(); // H((k xor opad) || H(k xor ipad) || message)

        return new BigInteger(result);
    }

    // Verify a HMAC tag
    public Boolean verifyHmac(BigInteger k, BigInteger message, BigInteger tag) {
        BigInteger result = hmac(k, message);
        return tag.equals(result);
    }

    // Encrypt a message using CBC with AES - assumes messages are a multiple of 16 byte block
    public BigInteger encrypt(BigInteger plaintext, BigInteger IV, BigInteger k) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        // Get key and set cipher to encrypt mode
        Key key = new SecretKeySpec(k.toByteArray(), "AES");
        AES.init(Cipher.ENCRYPT_MODE, key);

        byte[] plaintextBytes = plaintext.toByteArray();
        //System.out.println("P=" + Arrays.toString(plaintextBytes));

        // Input can be assumed to be a multiple of BLOCKSIZE per assignment spec
        byte[] ciphertextBytes = new byte[plaintextBytes.length];

        LinkedList<byte[]> blocks = new LinkedList<>();

        int start = 0;
        for (int i = 0; i < 4; i++){
            // Retrieve block
            byte[] block = new byte[BLOCKSIZE];
            System.arraycopy(plaintextBytes, start, block, 0, BLOCKSIZE);

            // XOR block
            byte[] xorWith;
            if (i == 0) { // First, determine whether to XOR with IV or previous ciphertext
                xorWith = IV.toByteArray();
            } else {
                xorWith = blocks.get(i - 1);
            }
            block = xor(block, xorWith);

            // Encrypt block
            byte[] output = AES.doFinal(block);

            // Store encryption output for later XORing
            blocks.add(output);

            // Append to ciphertext
            System.arraycopy(output, 0, ciphertextBytes, start, BLOCKSIZE);

            // Update plaintext index to the start of the next block
            start += BLOCKSIZE;
        }

        //System.out.println("C=" + Arrays.toString(ciphertextBytes));

        return new BigInteger(ciphertextBytes);
    }

    // Decrypt a message using CBC with AES - assumes messages are a multiple of 16 byte block
    public BigInteger decrypt(BigInteger ciphertext, BigInteger IV, BigInteger k) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        // Get key and set cipher to decrypt mode
        Key key = new SecretKeySpec(k.toByteArray(), "AES");
        AES.init(Cipher.DECRYPT_MODE, key);

        byte[] ciphertextBytes = ciphertext.toByteArray();
        //System.out.println("C=" + Arrays.toString(ciphertextBytes));

        // Input can be assumed to be a multiple of BLOCKSIZE per assignment spec
        byte[] plaintextBytes = new byte[ciphertextBytes.length];

        LinkedList<byte[]> blocks = new LinkedList<>();

        int start = 0;
        for (int i = 0; i < 4; i++) {
            // Retrieve block
            byte[] block = new byte[BLOCKSIZE];
            System.arraycopy(ciphertextBytes, start, block, 0, BLOCKSIZE);
            blocks.add(block);

            // Decrypt block using AES
            byte[] output = AES.doFinal(block);

            // XOR block with IV or previous plaintext
            byte[] xorWith;
            if (i == 0) { // First, determine whether to XOR with IV or previous ciphertext
                xorWith = IV.toByteArray();
            } else {
                xorWith = blocks.get(i-1);
            }
            output = xor(output, xorWith);

            // Append to plaintext
            System.arraycopy(output, 0, plaintextBytes, start, BLOCKSIZE);

            // Update ciphertext index to the start of the next block
            start += BLOCKSIZE;
        }

        //System.out.println("P=" + Arrays.toString(plaintextBytes));

        return new BigInteger(plaintextBytes);
    }
}
