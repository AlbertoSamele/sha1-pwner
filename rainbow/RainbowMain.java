import java.io.IOException;
import java.nio.file.*;
import java.util.*;


public class RainbowMain {

    // Available arguments to be read from CLI on program start
    private enum CLIArgument {
        /**
         * dic: the password dictionary filepath to be used to genereate the rainbow table
        */
        DICTIONARY("-dic");
        

        // The expected argument format
        public final String argId;

        /**
         * @param argId the expected argument format
         */
        CLIArgument(String argId) {
            this.argId = argId;
        }
    }


    // Rainbow table chain size
    private static int chainSize = 1000;

    
    public static void main(String[] args) throws IOException {
        // Gathering user input
        if (args.length < CLIArgument.values().length * 2) { throw new IllegalArgumentException(); }
        Map<CLIArgument, String> userInput = new HashMap<>();
        for (int i = 0; i < CLIArgument.values().length; i++) {
            CLIArgument expectedArgument = CLIArgument.values()[i];
            String userArgument = args[2*i];
            if (!userArgument.equals(expectedArgument.argId)) { throw new IllegalArgumentException(); }
            userInput.put(expectedArgument, args[2*i + 1]);
        }
        // Gathering password pool datasource
        Path passwordPoolFilepath = Paths.get(userInput.get(CLIArgument.DICTIONARY));
        List<String> passwordPool = Files.readAllLines(passwordPoolFilepath);
        // Generating table 
        System.out.println("Generating rainbow table. Please hold on");
        RainbowService rainbowService = new RainbowService(passwordPool, chainSize);
        rainbowService.run();
        // Table generated
        System.out.println("Rainbow table generated");

    }
}