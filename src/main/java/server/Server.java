package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Main server class for the Blackjack network game.
 * Listens for incoming client connections and starts a PlayerConnection thread for each client.
 */
public class Server {

    private final int port;  // Port number where the server listens
    private final int serverId = (int) (Math.random() * 1000000); // Random ID used for logging

    // Textual data to send to clients (can be used for testing or as welcome message)
    private final String textualData = "👋 from Croupier " + serverId;

    /**
     * Constructor allowing dynamic port configuration.
     *
     * @param port server listening port
     */
    public Server(int port) {
        this.port = port;
    }

    /**
     * Getter for the server ID.
     * Used by PlayerConnection for logging.
     *
     * @return the server ID
     */
    public int getServerId() {
        return serverId;
    }

    /**
     * Getter for textual data sent to clients.
     *
     * @return the textual message
     */
    public String getTextualData() {
        return textualData;
    }

    /**
     * Starts the server: listens for client connections and handles
     * each connection in a separate PlayerConnection thread.
     */
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("[Server " + serverId + "] starting on port " + port);

            // Main loop: accept clients indefinitely
            while (true) {
                Socket clientSocket = serverSocket.accept(); // blocks until a client connects

                System.out.println("[Server " + serverId + "] new client connected: " +
                        clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort());

                // Start a new thread to handle the client
                new Thread(new PlayerConnection(clientSocket, this)).start();
            }

        } catch (IOException e) {
            // Handle server socket errors
            System.out.println("[Server " + serverId + "] exception: " + e);
        }
    }
}
