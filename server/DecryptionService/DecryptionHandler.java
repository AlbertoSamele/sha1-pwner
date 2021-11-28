package DecryptionService;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import Manager.FileManager;
import Manager.CryptoManager;

// Handles decryption requests
public class DecryptionHandler implements Runnable {

    // The socket relative to the running request
    private final Socket socket;
    // The id associated with the running request
    private final UUID handlerId;


    /** 
     * @param socket Socket relative to the running inbound connection
    */
    public DecryptionHandler(Socket socket) {
        this.socket = socket;
        handlerId = UUID.randomUUID();
    }


    @Override
    public void run () {
        try {
            // IO streams
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            // Decoding request data
            DecryptionRequest request = readRequest(inputStream);
            File requestFile = new File(String.format("encrypted_%s", handlerId.toString()));
            FileManager.readFile(inputStream, new FileOutputStream(requestFile), request.fileLength);
            // Decrypting request data
            String cleartextPassword = decryptPassword(request.hashedPassword, request.passwordLength);
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
        byte [] hashedPassword = new byte[20];
        int count = inputStream.read(hashedPassword,0, 20);
        if (count < 0){ throw new IOException("Server could not read from the stream"); }

        int passwordLength = inputStream.readInt();
        long fileLength = inputStream.readLong();

        return new DecryptionRequest(hashedPassword, passwordLength, fileLength);
    }

    /**
     * Decrypts given hashed password into cleartext
     * @param hashedPassword the password to be decrypted
     * @param passwordLength the clear-text password length
     * @return the clear-text password
     */
    private String decryptPassword(byte[] hashedPassword, int passwordLength) {
        return "test";
    }
}