import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.stream.Stream;
import javax.crypto.*;

import Util.*;
import ClientManager.CryptoManager;
import Network.Decryption.*;


// Client entry point
public class ClientMain {

    // Available arguments to be read from CLI on program start
    private enum CLIArgument {
        /**
         * DIR: the directory in which the test clear-text files should be read from
         * PWD: the file containing the password pool to be used for file encryption
        */
        DIR("-dir"), PWD("-pwd");
        

        // The expected argument format
        public final String argId;

        /**
         * @param argId the expected argument format
         */
        CLIArgument(String argId) {
            this.argId = argId;
        }
    }


    // Server's port
    private static int serverPort = 9000;

    
    public static void main(String[] args) 
    throws IllegalArgumentException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException,
     InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {

        // Gathering user input
        if (args.length < CLIArgument.values().length * 2) { throw new IllegalArgumentException(); }
        Map<CLIArgument, String> userInput = new HashMap<>();
        for (int i = 0; i < CLIArgument.values().length; i++) {
            CLIArgument expectedArgument = CLIArgument.values()[i];
            String userArgument = args[2*i];
            if (!userArgument.equals(expectedArgument.argId)) { throw new IllegalArgumentException(); }
            userInput.put(expectedArgument, args[2*i + 1]);
        }
        // Gathering clear-text files
        String folderPath = userInput.get(CLIArgument.DIR);
        File targetFolder = new File(folderPath);
        File[] clearFiles = targetFolder.listFiles();
        // Parsing available passwords
        String passwordFilepath = userInput.get(CLIArgument.PWD);
        DrawList<String> passwordPool = new DrawList<String>();
        try (Stream<String> lines = Files.lines(Paths.get(passwordFilepath), Charset.defaultCharset())) {
            lines.forEachOrdered( line -> passwordPool.add(line) );
        }
        // Creating input(testing) and output folders
        File encryptedFolder = new File("Encrypted/");
        encryptedFolder.mkdir();
        File decryptedFolder = new File("Decrypted/");
        decryptedFolder.mkdir();
        // Processing input
        for (File file : clearFiles) {
            // Generatiing passwords
            String clearPassword = passwordPool.draw();
            SecretKey encryptionPassword = CryptoManager.getKeyFromPassword(clearPassword);
            byte[] hashedPassword = CryptoManager.hashSHA1(clearPassword);
            // Encrypting file
            String baseFilename = file.getName().substring(0, file.getName().lastIndexOf("."));
            String fileExtension = file.getName().substring(file.getName().lastIndexOf('.') + 1);
            File encryptedFile = new File(String.format("Encrypted/%s.%s", baseFilename, fileExtension));
            CryptoManager.encryptFile(encryptionPassword, file, encryptedFile);
            // Sending network request
            DecryptionRequest request = new DecryptionRequest(hashedPassword, clearPassword.length(), encryptedFile.length());
            DecryptionHandler handler = new DecryptionHandler(
                new Socket("localhost", serverPort),
                request,
                baseFilename, 
                fileExtension, 
                encryptedFile
            );
            Thread networkThread = new Thread(handler);
            networkThread.start();
        }
        
    }

}