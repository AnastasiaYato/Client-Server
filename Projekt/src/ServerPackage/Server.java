/**
 * @Author Anastazja Noemi Lisowska
 */

package ServerPackage;

import IO.SocketListener;
import IO.SocketSender;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.net.*;

/**
 * Thread for Server to listen for incoming messages
 */
class ClientSessionListener extends SocketListener {
    private final ClientSession parent;

    public ClientSessionListener(ClientSession parent) {
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
                        System.out.println("Client " + parent.name + " command: " + status);
                        switch (status) {
                            case "Exit" -> parent.disconnect( );
                            case "GoTo" -> goTo( );
                            case "GoBack" -> goBack( );
                            case "Download" -> sendFile(null);
                            case "Upload" -> downloadFile(0, null, parent.currentPath);
                            case "RefreshFiles" -> parent.sendCurrentPathAndFiles( );
                            case "CreateDir" -> createDir( );
                            case "Remove" -> removeDirOrFile( );
                        }
                    }
                    if (status.equals("Exit")) {
                        break;
                    }
                }
            } catch (IOException e) {
                System.out.println("Server.ClientSessionListener Error " + parent.socket);
                e.printStackTrace( );
                close = true;
                parent.disconnect( );
            }
        }
    }

    /**
     * Goes for directory selected by user
     */
    public void goTo() {
        String path = "";
        try {
            path = incomingMessage.readLine( ); //a directory we want go to
        } catch (IOException e) {
            e.printStackTrace( );
        }
        parent.currentPath = Path.of(parent.currentPath + "\\" + path); //go to current selected path created from current and selected directory
        System.out.println(parent.currentPath);
        parent.sendCurrentPathAndFiles( ); //calls a method to update user's file view
    }

    /**
     * Goes to parent directory
     */
    public void goBack() {
        parent.currentPath = parent.currentPath.getParent( ); //go to parent directory
        parent.sendCurrentPathAndFiles( ); //calls a method to update user's file view
    }

    /**
     * Sends a file selected by a user
     */
    @Override
    public void sendFile(File file) throws IOException {
        parent.working = true; //do not close interrupt this session now
        String fileToSend;
        fileToSend = incomingMessage.readLine( ); //waits for user's side message that contains what file they wants
        parent.sendCommand("Receive"); //sends command to user to prepare for file transmission
        parent.sendCommand(fileToSend); //sends file name
        file = new File(parent.currentPath + "\\" + fileToSend);
        parent.sendCommand(String.valueOf(Files.readAllBytes(Path.of(file.getAbsolutePath( ))).length)); //sends data about length of byte array user should create
        super.sendFile(file); //calls SocketListener to do the rest of the operation
        parent.working = false; //now it's possible to close this session
    }

    /**
     * Downloads a file selected by a user
     */
    @Override
    public void downloadFile(int fileSize, String fileName, Path path) throws IOException {
        parent.working = true; //do not close interrupt this session now
            fileName = incomingMessage.readLine( ); //what is the name of the file client is going to send us
            fileSize = Integer.parseInt(incomingMessage.readLine( )); //what is the size of the file
            super.downloadFile(fileSize, fileName, path); //calls SocketListener to do the rest of the operation
            parent.sendCurrentPathAndFiles( ); //calls a method to update user's file view
        parent.working = false; //now it's possible to close this session
    }

    /**
     * Creates a directory in current directory
     */
    public void createDir() {
        parent.working = true; //do not close interrupt this session now
        String directoryName;
        try {
            directoryName = incomingMessage.readLine( ); //what will be the name for the directory
            File file = new File(parent.currentPath + "\\" + directoryName);//create directory in current directory
            boolean wasCreated = file.mkdirs( );
            if (wasCreated) {
                System.out.println(parent.name + " Successfully created a new folder!");
                parent.sendCommand("DirectoryCreated");

            } else {
                System.out.println(parent.name + " Failed to created a new folder!");
                parent.sendCommand("Fail");
            }
        } catch (IOException e) {
            e.printStackTrace( );
        }
        parent.working = false; //now it's possible to close this session
    }

    /**
     * Removes a directory or a file in current directory
     * if selection is empty it will target to delete the directory
     */
    public void removeDirOrFile() {
        parent.working = true; //do not close interrupt this session now
        String name;
        String type;
        try {
            type = incomingMessage.readLine( ); //is it a file or a folder
            name = incomingMessage.readLine( );//what is it's name
            File file = null; //initialization
            if (type.equals("File")) {
                file = new File(parent.currentPath + "\\" + name);
            }
            if (type.equals("Directory")) {
                file = new File(String.valueOf(parent.currentPath));
            }
            boolean wasDeleted = false; //a flag for testing if file was deleted or not
            if (file != null) {
                wasDeleted = file.delete( );
            }
            if (wasDeleted) {
                System.out.println(parent.name + " Successfully deleted!");
                parent.sendCommand("Success"); //sends a command to make a popup message about successful operation
                parent.sendCurrentPathAndFiles( );
            } else {
                System.out.println(parent.name + " Failed to delete!");
                parent.sendCommand("Fail"); //sends a command to make a popup message about unsuccessful operation
            }
        } catch (IOException e) {
            e.printStackTrace( );
        }
        parent.working = false; //now it's possible to close this session
    }
}

/**
 * for Server to send messages to client
 */
class ClientSessionSender extends SocketSender {
    private final ClientSession parent;

    public ClientSessionSender(ClientSession parent) {
        super(parent.toSendMessage);
        this.parent = parent;
    }

    @Override
    public void send(String command) {
        System.out.println("Sending " + command + " to client " + parent.name);
        super.send(command); //calls SocketSender to do the rest of the operation
    }
}


/**
 * Main and core class for clients handling, contains listening thread
 */
class ClientSession implements AutoCloseable { //implements close method
    protected Path currentPath; //path user is in now
    protected BufferedReader incomingMessage; //input
    protected PrintStream toSendMessage; //output
    private ClientSessionSender clientSessionSender; //output handling
    private ClientSessionListener clientSessionListener; //input handling
    protected Socket socket;
    protected String name; //name of the client
    private static ArrayList<ClientSession> clients;
    protected boolean working; //a flag that makes sure the we won't interrupt a saving/uploading of a file

    //ClientSession constructor
    public ClientSession(Socket socket, ArrayList<ClientSession> users) {
        clients = users;
        working = false;
        this.socket = socket;
        currentPath = Path.of(System.getProperty("user.dir")).getParent();
    }

    public String getUserName() {
        return name;
    }

    public void run() {
        System.out.println("New Connection with " + socket);
        try {
            clients.get(0).
            /**
             * IO
             */
            incomingMessage = new BufferedReader(new InputStreamReader(socket.getInputStream( )));
            toSendMessage = new PrintStream(socket.getOutputStream( ));
            /**
             * Threads to listen and send messages independent of each other
             */
            clientSessionSender = new ClientSessionSender(this);
            clientSessionListener = new ClientSessionListener(this);
            /**
             * First message is always user's nick
             */
            name = incomingMessage.readLine( );
            System.out.println("ClientPackage.Client name is " + name);
            clients.add(this); //add to clients list
            refresh( );
            /**
             * Start thread with executor, simplifies running tasks in asynchronous mode.
             */
            clientSessionListener.start( );
            sendCurrentPathAndFiles( );
        } catch (IOException e) {
            System.out.println("Server.ClientSession Error " + socket);
            e.printStackTrace( );
            disconnect( );
        }
    }

    /**
     * Disconnect with this user
     */
    public void disconnect() {
        System.out.println("Closing the connection with " + socket);
        clients.remove(this);
        System.out.println("Connection closed with " + socket);
        System.out.println("Goodbye, " + name);
        refresh( );
        try {
            close( );
        } catch (Exception e) {
            System.out.println("Server.ClientSession.disconnect() Error" + socket);
            e.printStackTrace( );
        }
    }

    /**
     * Sends a string of users name and call a refresh in all clients to update their online users list
     */
    public void refresh() {
        StringBuilder users = new StringBuilder( );
        for (ClientSession client : clients) users.append(client.getUserName( )).append(";");
        for (ClientSession client : clients) {
            client.clientSessionSender.send("Refresh");
            client.clientSessionSender.send(users.toString( ));
        }
    }

    /**
     * Updates user's view with current path and files/directories in it
     */
    public void sendCurrentPathAndFiles() {
        clientSessionSender.send("DirAndFiles"); //sends to client command that server will send path and then files to show
        clientSessionSender.send(String.valueOf(currentPath)); //sends current path
        File file = new File(String.valueOf(currentPath));
        File[] files = file.listFiles(selectedFile -> !selectedFile.isHidden( ));
        int size = 0;
        if (files != null) {
            size = files.length;
            Arrays.sort(files, Comparator.comparing(File::isDirectory).reversed( )); //sorts so that directories are first
        }
        clientSessionSender.send(String.valueOf(size)); //sends how many files there are
        if (files != null) {
            for (File value : files) {
                clientSessionSender.send(value.getName( )); //sends each "file"
            }
        }
    }

    public void sendCommand(String command) {
        clientSessionSender.send(command);
    }

    /**
     * A method of AutoCloseable interface, to keep code logically clean
     * it contains all close commands
     */
    @Override
    public void close() throws Exception {
        clientSessionListener.setClose(true);
        incomingMessage.close( );
        toSendMessage.close( );
        this.socket.close( );
        System.out.println("Successful Disconnection");
    }
}

/**
 * Main host, creates a server at a socket
 */
class ServerHost extends Thread {
    public static final int serverPort = 5056;
    private boolean exit;
    ServerSocket s;
    protected static ArrayList<ClientSession> clients = new ArrayList<>( ); //to hold ClientSession threads

    /**
     * Get and return string of names of current logged users
     */
    public static String getClients() {
        StringBuilder clientsToPrint = new StringBuilder( );
        for (ClientSession client : clients) {
            clientsToPrint.append(client.name).append(";");
        }
        return clientsToPrint.toString( );
    }

    //ServerHost constructor
    public ServerHost() {
        exit = false;
        try {
            s = new ServerSocket(serverPort);
            System.out.println("Server created and ready!");
        } catch (Exception e) {
            System.out.println("Server Error Socket creating failed");
            System.exit(1);
        }
    }

    /**
     * Starts the server
     */
    public void run() {
        System.out.println("Server is running!");
        while (true) { //accepts every new connection in a loop
            if (!exit) {
                Socket socket = null;
                try {
                    socket = s.accept( );
                } catch (IOException ignored) { //it will always throw exception when closing the server and that does not matter
                }
                if (socket != null) {
                    ClientSession clientSession = new ClientSession(socket, clients); //creating a new thread
                    clientSession.run( ); //starting a thread
                }
            } else {
                break;
            }
        }
    }

    /**
     * For server closing
     */
    public void quit() throws Exception {
        exit = true;
        System.out.println("Stopping new connections acceptance");
        /**
         * After exit bool is true, Server will not longer accept new clients
         * It will send a message to current online users to save their work and logout within a minute
         */
        for (ClientSession client : clients) {
            client.sendCommand("Disconnect");
        }
        //if all clients disconnected, just skip the waiting
        byte time=6; //wait for a minute and then shut off clients' sessions
        while(clients.size( ) > 0 && time>0)
            try {
                sleep(10000); //check every 10 seconds if all clients logged off
                time--;
            } catch (InterruptedException e) {
                e.printStackTrace( );
            }
        /**
         * Force quit users but wait till they finish their upload/download
         */
        for (ClientSession client : clients) {
            if (client.working) {
                while (client.working) //while client is still working check when he will finish
                {
                    System.out.println("Waiting for " + client.name + " to finish work");
                    try {
                        wait(3000); //wait 3 seconds and check again
                    } catch (InterruptedException e) {
                        e.printStackTrace( );
                    }
                }
            }
            client.sendCommand("ForceQuit");
        }
        System.out.println("All clients disconnected!");
        s.close( ); //close Server socket
        System.out.println("Server stopped!");
    }

}

/**
 * Class that holds a thread for accepting new clients and allows us to control the server
 */
class ServerControl {
    private final ServerHost controlledServer; //
    private final Scanner scanner;
    private String command;
    private boolean isWorking;

    public ServerControl(ServerHost controlledServer) {
        isWorking = false;
        this.controlledServer = controlledServer;
        scanner = new Scanner(System.in);
        command = "";
    }

    public void work() throws Exception {
        while (!command.equals("Exit")) {
            /**
             * Commands handling
             */
            command = scanner.nextLine( );
            switch (command) {
                case "Start" -> startTheServer( ); //start the server
                case "Exit" -> System.out.println("Stopping the server");
                case "Users" -> showUsers( );
                case "Authors" -> System.out.println("Author: Anastazja Noemi Lisowska");
                case "Help" -> System.out.println("Commands: Start;Exit;Users;Authors");
                default -> System.out.println("Unknown command");
            }
        }

        /**
         * Stop the server
         */
        System.out.println("Stopping the Host");
        controlledServer.quit( );
    }

    public void startTheServer() {
        if (!isWorking) {
            controlledServer.start( );
            isWorking = true;
        } else System.out.println("Server is already running!");
    }

    public void showUsers() {
        if (ServerHost.getClients( ).length( ) > 0) System.out.println(ServerHost.getClients( ));
        else System.out.println("No users logged in");
    }
}

/**
 * Main - Welcome screen
 */
public class Server {
    public static void main(String[] args) throws Exception {
        System.out.println("#######################################");
        System.out.println("Welcome!");
        System.out.println("The server is launched at port 5056");
        System.out.println("To start a server Type-in: Start");
        System.out.println("For more commands Type-in: Help");
        System.out.println("#######################################");
        ServerHost server = new ServerHost( ); //creates ServerHost thread
        ServerControl serverControl = new ServerControl(server); //creates ServerControl class
        System.out.println("Waiting for user's input");
        System.out.println( );
        serverControl.work( ); //run a method that will now be handled by main thread
        System.out.println("Press any key to close the window");
        System.in.read();
    }
}
/**
 * Logic behind the code is as follows:
 * we use main thread to control the server and we create another thread for client's acceptance
 * that's why we don't have another thread for ServerHost
 * having both threads running allows us to interfere
 */

