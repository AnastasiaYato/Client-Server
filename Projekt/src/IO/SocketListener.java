/**
 * @Author Anastazja Noemi Lisowska
 */

package IO;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Thread for Client or Server to receive messages, Class was created to reduce code repeating
 */
public class SocketListener extends Thread {
    protected BufferedReader incomingMessage;
    protected boolean close; //a "flag" to end a thread
    protected String status; //what listener receive
    private final Socket socket;

    //SocketListener constructor
    public SocketListener(BufferedReader bufferedReader, Socket socket){
        close = false;
        status = "";
        incomingMessage = bufferedReader;
        this.socket = socket;

    }

    /**
     * For thread closing
     */
    public void setClose(boolean close) {
        this.close = close;
    }

    /**
     * For file sending
     */
    public void sendFile(File file) throws IOException {
        byte[] fileByteArray = Files.readAllBytes(Path.of(file.getAbsolutePath( ))); //create a byte array
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream( )); //prepare output stream
        dataOutputStream.write(fileByteArray); //send the array
    }

    /**
     * For file downloading
     */
    public void downloadFile(int fileSize, String fileName, Path pathToSave) throws IOException {
        byte[] fileByteArray = new byte[fileSize]; //prepare array
        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream( )); //prepare input stream
        dataInputStream.readFully(fileByteArray, 0, fileByteArray.length); //read data
        FileOutputStream outputStream = new FileOutputStream(pathToSave + "\\" + fileName); //prepare output stream to save the file
        outputStream.write(fileByteArray); //save the file
        outputStream.flush( );
        System.out.println("File Saved");
        outputStream.close( );
    }
}
