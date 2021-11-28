import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import DecryptionService.DecryptionHandler;

// Server entry point
public class ServerMain {

    // Server's port
    private static int serverPort = 9000;

    public static void main(String[] args) throws IOException {
        // Creating a server socket at target local port
        ServerSocket serverSocket = new ServerSocket(serverPort);
        System.out.println("Server started at " + serverPort);
        // Server always running unless manually terminated
        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("Inbound connection accepted");
            // Starting a new thread not to block the server's connection queue
            DecryptionHandler decryptionHandler = new DecryptionHandler(socket);
            Thread decryptionThread = new Thread(decryptionHandler);
            decryptionThread.start();
        }
    }

}