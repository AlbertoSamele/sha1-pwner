package Network.Decryption;
import java.io.*;
import java.net.Socket;

import ClientManager.FileManager;

public class DecryptionHandler implements Runnable {
    
    // The socket relative to the running request
    private final Socket socket;
    // The request to be made to the server
    private final DecryptionRequest request;
    // The encrypted file to be decrypted
    private final File encryptedFile;
    // The base file name
    private final String fileName;
    // The base file extension
    private final String fileExtension;


    public DecryptionHandler(
        Socket socket,
        DecryptionRequest request, 
        String fileName, 
        String fileExtension, 
        File encryptedFile) {
        this.socket = socket;
        this.request = request;
        this.fileName = fileName;
        this.fileExtension = fileExtension;
        this.encryptedFile = encryptedFile;
    }
    

    @Override
    public void run() {
        try {
            // IO streams
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            // Sending network request
            sendRequest(outputStream, request.hashedPassword, request.passwordLength, request.fileLength);
            outputStream.flush();
            FileManager.writeFile(new FileInputStream(encryptedFile), outputStream);
            // Recieving network response
            long decryptedFileLength = inputStream.readLong();
            File decryptedFile = new File(String.format("Decrypted/%s.%s", fileName, fileExtension));
            FileManager.readFile(inputStream, new FileOutputStream(decryptedFile), decryptedFileLength);
            // Cleaning up
            inputStream.close();
            outputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
         }
    }

    /**
     * Sends network request to the server
     * @param out socket output stream
     * @param hashPwd SHA-1 hash of the password used to derive the key of the encryption
     * @param pwdLength length of the clear password
     * @param fileLength length of the encrypted file
     */
    private void sendRequest(DataOutputStream out, byte[] hashPwd, int pwdLength, long fileLength) throws IOException {
        out.write(hashPwd,0, 20);
        out.writeInt(pwdLength);
        out.writeLong(fileLength);
    }
}
