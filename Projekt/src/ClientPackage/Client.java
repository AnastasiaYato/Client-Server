/**
 * @Author Anastazja Noemi Lisowska
 */

package ClientPackage;

import IO.SocketListener;
import IO.SocketSender;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

import static javax.swing.JOptionPane.showMessageDialog;
/**
 * Thread for Client to receive messages
 */
class ListenToServer extends SocketListener {
    final private Client parent;

    public ListenToServer(Client parent) {
        super(parent.incomingMessage, parent.socket);
        this.parent = parent;
    }

    @Override
    public void run() {
        while (!close) {
            try {
                status = incomingMessage.readLine( ); //make sure that command is not null or empty
                if (status != null) {
                    if (!(status.equals(""))) {
                        /**
                         * Commands handling
                         */
                        System.out.println("Server command: " + status);
                        switch (status) {
                            case "Refresh" -> refreshClientsList( );
                            case "DirAndFiles" -> getDirAndFiles( );
                            case "Receive" -> downloadFile(0, null, parent.pathToSave);
                            case "Success" -> success( );
                            case "Fail" -> fail( );
                            case "Disconnect" -> disconnect( );
                            case "ForceQuit" -> forceQuit( );
                        }
                    }
                    if (status.equals("Exit")) {
                        break;
                    }
                }
            } catch (IOException e) {
                System.out.println("Client.ListenToServer Error");
                e.printStackTrace( );
                showMessageDialog(null, "Connection has been lost or Server got closed", "Connection Lost", JOptionPane.ERROR_MESSAGE);
                parent.disconnect( );
            }
        }
    }

    /**
     * Get incoming list of users online
     */
    public void refreshClientsList() {
        try {
            status = incomingMessage.readLine( );
        } catch (IOException e) {
            e.printStackTrace( );
        }
        String[] users = status.split(";");
        ArrayList<String> usersonline = new ArrayList<>(Arrays.asList(users));
        /**
         * Update GUI
         */
        parent.parent.updateOnlineUsers(usersonline);
    }

    /**
     * Get incoming list of files and directories in the current directory
     */
    public void getDirAndFiles() {
        String path = "";
        int size;
        ArrayList<String> filesList = new ArrayList<>( );
        /**
         * Get incoming path and list of files in it
         */
        try {
            path = incomingMessage.readLine( ); //path
            size = Integer.parseInt(incomingMessage.readLine( )); //how many files are we going to receive
            for (int i = 0; i < size; i++) {
                status = incomingMessage.readLine( );
                filesList.add(status);
            }
        } catch (IOException e) {
            e.printStackTrace( );
        }
        parent.parent.updateFiles(path, filesList);
    }

    /**
     * Sends a file to server
     */
    @Override
    public void sendFile(File file) {
        try {
            parent.sendCommand("Upload"); //sends command to server to prepare for file transmission
            parent.sendCommand(file.getName( )); //sends filename
            parent.sendCommand(String.valueOf(file.length( ))); //sends data about length of byte array server should create
            super.sendFile(file); //calls SocketListener to do the rest of the operation
        } catch (IOException e) {
            showMessageDialog(null, "Could not Upload the File" + '\n' + "Check if file does not already exist", "Upload Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace( );
        }
        showMessageDialog(null, "File Uploaded!", "File Uploaded", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Save a file from the server
     */
    @Override
    public void downloadFile(int fileSize, String fileName, Path path) {
        try {
            fileName = incomingMessage.readLine( );
            fileSize = Integer.parseInt(incomingMessage.readLine( ));
            super.downloadFile(fileSize, fileName, path);
        } catch (IOException e) {
            showMessageDialog(null, "Could not Save the File" + '\n' + "Check if file does not already exist", "Download Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace( );
        }
        showMessageDialog(null, "File Downloaded!", "File Downloaded", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * A method that shows message from the server about incoming stoppage
     */
    public void disconnect() {
        showMessageDialog(null, "Server is being closed within 1 minute" + '\n' + "Please finish your work and log out", "Server closing!", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * A method that force disconnects and quits our client
     */
    public void forceQuit() {
        showMessageDialog(null, "Server is being closed", "Server closing!", JOptionPane.INFORMATION_MESSAGE);
        parent.disconnect( );
    }

    /**
     * Methods for messaging a user of successful or failed operation
     */
    public void success() {
        showMessageDialog(null, "Operation Successful!", "Operation Successful", JOptionPane.INFORMATION_MESSAGE);
    }

    public void fail() {
        showMessageDialog(null, "Operation Failed!", "Operation Failed", JOptionPane.ERROR_MESSAGE);
    }
}

/**
 * for Client to send messages
 */
class SendToServer extends SocketSender {
    public SendToServer(Client parent) {
        super(parent.toSendMessage);
    }

    public void send(String command) {
        System.out.println("Sending command " + command + " to server");
        super.send(command);
    }
}

/**
 * Main Thread, it needs to be a thread so it won't lock with EDT of swing GUI
 */
public class Client extends Thread implements AutoCloseable {
    protected MainWindow parent; //access parent class for its components
    protected Socket socket;
    protected Path pathToSave;
    private final String name; //our client's name
    protected BufferedReader incomingMessage; //input
    protected PrintStream toSendMessage; //output
    private ListenToServer listenToServer; //input handling
    private SendToServer sendToServer; //output handling

    //Client constructor
    public Client(String name, MainWindow parent) {
        this.pathToSave = Path.of(System.getProperty("user.dir"));
        this.parent = parent;
        this.name = name;
    }

    @Override
    public void run() {
        System.out.println("Client started");
        start( );
    }

    public void start() {
        connect( );
        try {
            /**
             * IO
             */
            incomingMessage = new BufferedReader(new InputStreamReader(socket.getInputStream( )));
            toSendMessage = new PrintStream(socket.getOutputStream( ));
            /**
             * Threads to listen and send messages independent of each other
             */
            listenToServer = new ListenToServer(this);
            sendToServer = new SendToServer(this);
            /**
             * First message is always user's nick
             */
            toSendMessage.println(name);
            /**
             * Start thread with executor, simplifies running tasks in asynchronous mode.
             */
            listenToServer.start( );
        } catch (IOException e) {
            System.out.println("Client Start Error");
            e.printStackTrace( );
        }

    }

    public void connect() {
        try {
            socket = new Socket("localhost", 5056);
        } catch (IOException e) {
            System.out.println("Client Connect Error");
            showMessageDialog(null, "Could not connect to the Server", "Connection Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace( );
        }
    }

    /**
     * For command sending
     */
    public void sendCommand(String command) {
        sendToServer.send(command);
    }

    /**
     * For uploading a file
     */
    public void uploadFile(File file) {
        listenToServer.sendFile(file);
    }

    /**
     * A method for updating the path field
     */
    public void setDownloadsFolder(Path path) {
        pathToSave = path;
    }

    /**
     * A method for handling disconnecting
     */
    public void disconnect() {
        try {
            System.out.println("Closing");
            sendToServer.send("Exit");
            close( );
        } catch (Exception e) {
            System.out.println("Client Disconnect Error");
            e.printStackTrace( );
        }
    }

    /**
     * A method of AutoCloseable interface, to keep code logically clean
     * it contains all close commands
     */
    @Override
    public void close() throws Exception {
        listenToServer.setClose(true);
        incomingMessage.close( );
        toSendMessage.close( );
        socket.close( );
        System.out.println("Connection Closed, Exiting...");
        System.exit(0);
    }
}

