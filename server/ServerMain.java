import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import DecryptionService.DecryptionHandler;
import Models.RainbowTable;

// Server entry point
public class ServerMain {

    // Available arguments to be read from CLI on program start
    private enum CLIArgument {
        /**
         * dic: the filepath of the rainbow table to be used for the hash cracking
        */
        RAINBOW_TABLE("-rbw");
        

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
        // Parsing rainbow table
        String rainbowFilepath = userInput.get(CLIArgument.RAINBOW_TABLE);
        int rainbowChainSize = 0;
        Map<String, String> rainbowEntries = new HashMap<>();
        try (Stream<String> lines = Files.lines(Paths.get(rainbowFilepath), Charset.defaultCharset())) {
            List<String> linesList = lines.collect(Collectors.toList());
            // Assuming rainbow table is correctly formatted for the sake of this project
            for(int i = 0; i < linesList.size(); i++) {
                if (i == 0) { rainbowChainSize = Integer.parseInt(linesList.get(i)); }
                else {
                    String[] tokenizedLine = linesList.get(i).split(" ");
                    rainbowEntries.put(tokenizedLine[1], tokenizedLine[0]);
                }
            }
        }
        RainbowTable rainbowTable = new RainbowTable(rainbowChainSize, rainbowEntries);
        // Creating a server socket at target local port
        ServerSocket serverSocket = new ServerSocket(serverPort);
        System.out.println("Server started at " + serverPort);
        // Server always running unless manually terminated
        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("Inbound connection accepted");
            // Starting a new thread not to block the server's connection queue
            DecryptionHandler decryptionHandler = new DecryptionHandler(socket, rainbowTable);
            Thread decryptionThread = new Thread(decryptionHandler);
            decryptionThread.start();
        }
    }

}