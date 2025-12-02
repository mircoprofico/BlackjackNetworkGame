package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Main server class for the Blackjack network game.
 * Listens for incoming client connections and starts a PlayerConnection thread for each client.
 */
public class Server {

    private static final int PORT = 1234; // Port number where the server will listen for connections
    private static final int SERVER_ID = (int) (Math.random() * 1000000); // Random server ID used for logging purposes
    private final GameManager gameManager = new GameManager();
    // Textual data to send to clients (can be used for testing or welcome message)
    private static final String TEXTUAL_DATA = "👋 from Croupier " + SERVER_ID;

    public final int BASE_MONEY = 50;
    /**
     * Getter for the server ID.
     * Used by PlayerConnection for logging.
     *
     * @return the server ID
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
     * Starts the server: listens for client connections and handles each connection in a separate thread.
     */
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            System.out.println("[Server " + SERVER_ID + "] starting with id " + SERVER_ID + " listening on port " + PORT);

            // Main loop: accept clients indefinitely
            while (true) {
                Socket clientSocket = serverSocket.accept(); // blocks until a client connects
                System.out.println("[Server " + SERVER_ID + "] new client connected: " +
                        clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort());

                // Start a new thread to handle this client
                new Thread(new PlayerConnection(clientSocket, this, gameManager)).start();
            }

        } catch (IOException e) {
            // Handle server socket errors
            System.out.println("[Server " + SERVER_ID + "] exception: " + e);
        }
    }

    /**
     * Main entry point for the server program.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        new Server().start();
    }
}
