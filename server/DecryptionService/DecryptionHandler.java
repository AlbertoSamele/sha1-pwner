package DecryptionService;

import java.io.*;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.crypto.*;

import Models.RainbowTable;
import Util.CharsetIterable;
import ServerManager.*;


// Handles decryption requests
public class DecryptionHandler implements Runnable {

    // The socket relative to the running request
    private final Socket socket;
    // The id associated with the running request
    private final UUID handlerId;
    // The rainbow table to be used for the hash cracking process
    private final RainbowTable rainbowTable;


    /**
     * @param socket Socket relative to the running inbound connection
     */
    public DecryptionHandler(Socket socket, RainbowTable rainbowTable) {
        this.socket = socket;
        this.rainbowTable = rainbowTable;
        handlerId = UUID.randomUUID();
    }


    @Override
    public void run() {
        try {
            // IO streams
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            // Decoding request data
            DecryptionRequest request = readRequest(inputStream);
            File requestFile = new File(String.format("encrypted_%s", handlerId.toString()));
            FileManager.readFile(inputStream, new FileOutputStream(requestFile), request.fileLength);
            // Decrypting request data
            System.out.println("Cracking password");
            //String cleartextPassword = decryptPassword(request.hashedPassword, request.passwordLength);
            String cleartextPassword = decryptPassword(request.hashedPassword, request.passwordLength, rainbowTable);
            if (cleartextPassword == null) {
                System.out.println("Rainbow attack failed. Attempting bruteforce");
                cleartextPassword = bruteforce(request.hashedPassword, request.passwordLength);
            }
            System.out.println("Password found: " + cleartextPassword);
            SecretKey decryptionPassword = CryptoManager.getKeyFromPassword(cleartextPassword);
            File responseFile = new File(String.format("decrypted_%s", handlerId.toString()));
            CryptoManager.decryptFile(decryptionPassword, requestFile, responseFile);
            // Sending response
            outputStream.writeLong(responseFile.length());
            outputStream.flush();
            FileManager.writeFile(new FileInputStream(responseFile), outputStream);
            // Cleaning up
            requestFile.delete();
            responseFile.delete();
        } catch (
                IOException | NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException |
                        NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
    }


    /**
     * @param inputStream Stream from which to read the request
     * @return Request with information required by the server to process encrypted file
     * @throws IOException if malformed request
     */
    private DecryptionRequest readRequest(DataInputStream inputStream) throws IOException {
        byte[] hashedPassword = new byte[20];
        int count = inputStream.read(hashedPassword, 0, 20);
        if (count < 0) {
            throw new IOException("Server could not read from the stream");
        }

        int passwordLength = inputStream.readInt();
        long fileLength = inputStream.readLong();

        return new DecryptionRequest(hashedPassword, passwordLength, fileLength);
    }

    /**
     * Attempts to decrypt the given password hash first using a pre-computed rainbow table, defaulting to bruteforce if that method fails
     * @param hashedPassword the password to be decrypted
     * @param passwordLength the clear-text password length
     * @param rainbowTable the reainbow table to be used during the attack
     * @return the clear-text password
     * @throws NoSuchAlgorithmException
     */
    private String decryptPassword(byte[] hashedPassword, int passwordLength, RainbowTable rainbowTable) throws NoSuchAlgorithmException {
        String cleartextResult = null;
        // Leveraging the fact that each chain in the rainbow table only has entries 
        // the same length as the entrypoint to further reduce the set in which the hashed password
        // should be looked into
        Map<String, String> filteredTable = rainbowTable.entries.entrySet().stream()
        .filter( mapEntry -> mapEntry.getValue().length() == passwordLength)
        .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
        // Finding reduced hash matches
        byte[] targetHash = hashedPassword;
        for(int i = 0; i < rainbowTable.chainLength && cleartextResult == null; i++) {
            String reducedHash = CryptoManager.reduceHash(targetHash, i, passwordLength);
            //System.out.println(reducedHash);
            if (filteredTable.containsKey(reducedHash)) {
                // Match found, inspecting chain
                String entryPoint = filteredTable.get(reducedHash);
                for (int k = 0; k < rainbowTable.chainLength; k++) {
                    byte[] chainHash = CryptoManager.hashSHA1(entryPoint);
                    if (Arrays.equals(chainHash, hashedPassword)) {
                        cleartextResult = entryPoint;
                        break;
                    } else { entryPoint = CryptoManager.reduceHash(chainHash, k, passwordLength); }
                }
            } else { 
                // No match found, continuing search
                targetHash = CryptoManager.hashSHA1(reducedHash);
            }

        }

        return cleartextResult;
    }

    /**
     * Decrypts given hashed password into cleartext using a slow bruteforce algorithm
     *
     * @param hashedPassword the password to be decrypted
     * @param passwordLength the clear-text password length
     * @return the clear-text password
     * @throws NoSuchAlgorithmException when the bruteforce process fails
     */
    private String bruteforce(byte[] hashedPassword, int passwordLength) throws NoSuchAlgorithmException {
        // Declaring available password charset
        CharsetIterable ascii = CharsetIterable.ASCII;
        char minAsciiValue = ascii.min();
        char maxAsciiValue = ascii.max();
        // Defining bruteforce exit condition
        Function<String, Boolean> passwordFound = (password) -> {
            try {
                return Arrays.equals(CryptoManager.hashSHA1(password), hashedPassword);
            } catch (NoSuchAlgorithmException e) {
                return false;
            }
        };
        // Initializing password guess with minimum charset value
        char[] passwordGuess = new char[passwordLength];
        Arrays.fill(passwordGuess, minAsciiValue);
        // Testing out every possible password combination
        while (!passwordFound.apply(String.valueOf(passwordGuess))) {
            // Incrementing password guess from its very end
            for (int j = passwordGuess.length - 1; j >= 0; j--) {
                if (passwordGuess[j] < maxAsciiValue) {
                    passwordGuess[j]++;
                    if (j < passwordGuess.length - 1) {
                        passwordGuess[j + 1] = minAsciiValue;
                    }
                    break;
                }
            }
        }

        return String.valueOf(passwordGuess);
    }

}