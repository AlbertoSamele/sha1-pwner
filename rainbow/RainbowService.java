import java.io.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import Managers.CryptoManager;
import java.security.*;

public class RainbowService {

    private class Chain {
        // The chain's entry point
        String head;
        // The chain's endpoint
        String tail;

        Chain(String head, String tail) {
            this.head = head;
            this.tail = tail;
        }
    }


    // The table chain size
    private final int size;
    // The list of passwords to be used to generated the rainbow table 
    private final List<String> passwordPool;


    /**
     * @param size The table chain size
     * @param passwordPool The list of passwords to be used to generated the rainbow table 
     */
    public RainbowService(List<String> passwordPool, int size) {
        this.size = size;
        this.passwordPool = passwordPool;
    }

    public void run() {
        try {
            // Creating file where rainbow table will be saved to
            String filename = "rainbow-"+ UUID.randomUUID() + ".txt";
            Path filepath = Paths.get(filename);
            File file = new File(filename);
            file.createNewFile();
            // Writing rainbow "headers"
            write(filepath, Integer.toString(size));
            // Generating rainbow table
            for (String cleartext : passwordPool) {
                Chain chain = generateChain(cleartext);
                // Writing  chain to file
                write(filepath, chain.head + " " + chain.tail);
            }
        } catch (IOException | NoSuchAlgorithmException e) { e.printStackTrace(); }
    }


    /** 
     * Generates one rainbow table chaing starting off from the given entry
     * @param password the chain starting point
     * @return the chain generated from the given password
     * @throws NoSuchAlgorithmException
     */
    Chain generateChain(String password) throws NoSuchAlgorithmException {
        String chainEndpoint = password; // End of the chain, starts off the given cleartext password to be hashed and reduced
        for (int i = 0; i < size; i++) {
           byte[] temp = CryptoManager.hashSHA1(chainEndpoint);
           chainEndpoint = CryptoManager.reduceHash(temp, i, password.length());
        }
        return new Chain(password, chainEndpoint);
    }

    /**
     * Writes to given filepath, automatically applying a carriage return at the end
     * @param filepPath the filepath where the content should be written to
     * @param content the content to be written
     */
    void write(Path filepPath, String content) throws IOException {
        BufferedWriter writer = Files.newBufferedWriter(filepPath, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        writer.write(content);
        writer.newLine();
        writer.close();
    }

}
