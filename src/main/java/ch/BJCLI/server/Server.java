package ch.BJCLI.server;

import picocli.CommandLine;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


@CommandLine.Command(name = "server", mixinStandardHelpOptions = true, version = "1.0",
        description = "Launch a new instance of a Server, will print any interactions with players")
public class Server implements Runnable {

    @CommandLine.Option(names = {"-p", "--port"}, description = "The port to connect to")
    private int PORT = 1234; // Port number where the ch.BJCLI.server will listen for connections
    private static final int SERVER_ID = (int) (Math.random() * 1000000); // Random ch.BJCLI.server ID used for logging purposes
    private final GameManager gameManager = new GameManager();
    // Textual data to send to clients (can be used for testing or welcome message)
    private static final String TEXTUAL_DATA = "👋 from Croupier " + SERVER_ID;

    public final int BASE_MONEY = 50;
    /**
     * Getter for the ch.BJCLI.server ID.
     * Used by PlayerConnection for logging.
     *
     * @return the ch.BJCLI.server ID
     */
    public int getServerId() { return SERVER_ID; }

    /**
     * Getter for textual data.
     * Used by PlayerConnection to send a response to clients.
     *
     * @return the textual message
     */
    public String getTextualData() { return TEXTUAL_DATA; }

    /**
     * Starts the ch.BJCLI.server: listens for ch.BJCLI.client connections and handles each connection in a separate thread.
     */
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            System.out.println("[Server " + SERVER_ID + "] starting with id " + SERVER_ID + " listening on port " + PORT);

            // ch.BJCLI.Main loop: accept clients indefinitely
            while (true) {
                Socket clientSocket = serverSocket.accept(); // blocks until a ch.BJCLI.client connects
                System.out.println("[Server " + SERVER_ID + "] new ch.BJCLI.client connected: " +
                        clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort());

                // Start a new thread to handle this ch.BJCLI.client
                new Thread(new PlayerConnection(clientSocket, this, gameManager)).start();
            }

        } catch (IOException e) {
            // Handle ch.BJCLI.server socket errors
            System.out.println("[Server " + SERVER_ID + "] exception: " + e);
        }
    }

    @Override
    public void run() {new Server().start();}
}
