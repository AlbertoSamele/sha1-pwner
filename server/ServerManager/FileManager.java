package ServerManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

// Handles file-related operations
public class FileManager {

    /**
     * Writes file from input stream onto target stream
     * @param inputStream the stream from which the file should be read from
     * @param outputStream the stream in which the file should be written to
     * @param fileLength the file's length the be read from input stream
     * @throws IOException
     */
    public static void readFile(InputStream inputStream, OutputStream outputStream, long fileLength) throws IOException {
        int readFromFile = 0;
        int bytesRead;
        byte[] readBuffer = new byte[64];
        while((readFromFile < fileLength)){
            bytesRead = inputStream.read(readBuffer);
            readFromFile += bytesRead;
            outputStream.write(readBuffer, 0, bytesRead);
        }
    }

    /**
     * Copies file content
     * @param inputStream where the file should be read from
     * @param outputStream where the file should be written to
     * @throws IOException
     */
    public static void writeFile(InputStream inputStream, OutputStream outputStream) throws IOException {
        int readCount;
        byte[] buffer = new byte[64];
        while ((readCount = inputStream.read(buffer)) > 0){
            outputStream.write(buffer, 0, readCount);
        }
    }
}