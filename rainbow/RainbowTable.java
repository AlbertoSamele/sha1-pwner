import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;
import java.util.Hashtable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RainbowTable implements Runnable {
    private Hashtable<String, String> numbers = new Hashtable<>();
    private SecureRandom rand = new SecureRandom();
    private final int length;
    public RainbowTable(int length) {
        this.length = length;
    }

    public void run() {
        try {
            writeRainbowTables();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }
        catch (NoSuchAlgorithmException e){
            System.out.println("hahaha nope");
        }
    }


    String[] tableGenerator() throws NoSuchAlgorithmException {
         long number = Math.abs(rand.nextLong());
         number = (long) (number % Math.pow(26,length));
         String startingPlaintext = "";
         String temp = "";
         for(int i = 0; i < length ; i++){
             int n = (int) (number % 26) + 97;
             char c = (char) n;
             number = number/26;
             startingPlaintext = c + startingPlaintext;
         }
         String startEnd[] = new String[2];
         startEnd[0] = startingPlaintext;
         for (int i = 0; i < 10000; i++) {
             temp = hashGenerator(startingPlaintext);
             startingPlaintext = reduceHash(temp,i);
         }
         startEnd[1] = temp;
         return startEnd;
    }


    String reduceHash(String s, int j) {
        String r = "";
        long l = Long.parseLong(s.substring(0,12), 16);
        long number = (long) ((l+j)%Math.pow(26,length));
        for(int i = 0; i < length ; i++){
            int n = (int)(number % 26) + 97;
            char c = (char) n;
            number = number/26;
            r = c + r;
        }
        return r;
    }

    String hashGenerator(String plaintext) throws NoSuchAlgorithmException{
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] b = md.digest(plaintext.getBytes());
            String result = "";
            for (int i=0; i < b.length; i++) {
                result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
            }
            return result;
    }

    void writeRainbowTables() throws FileNotFoundException, NoSuchAlgorithmException {
        Path path = Paths.get("tables"+length+".txt");
        Charset charset = Charset.forName("UTF-8");
        checkIfFileExistsElseCreateIt("tables"+length+".txt");
        String data[] = tableGenerator();
        try (BufferedWriter writer = Files.newBufferedWriter(path, charset, StandardOpenOption.APPEND)) {
            writer.write(data[0] + "  " + data[1]);
            writer.newLine();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    void checkIfFileExistsElseCreateIt(String fileName){

        File f = new File(fileName);
        if(!f.exists()) {
            System.out.println("File tables"+length+".txt wasnt found.\n File tables.txt was created");
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



    }

}
