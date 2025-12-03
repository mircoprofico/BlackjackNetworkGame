package server;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Handles a single player's connection to the server.
 * Each instance runs in its own thread.
 */
public class PlayerConnection implements Runnable {
    private final Socket socket;   // Socket associated with the connected client
    private final Server server;   // Reference to the main server
    private final BufferedWriter out; // Writer for sending messages
    private final BufferedReader in;  // Reader for receiving messages
    private String playerName;

    /**
     * Constructor for a new player connection.
     *
     * @param clientSocket the socket representing the client connection
     * @param server       reference to the main server
     */
    public PlayerConnection(Socket clientSocket, Server server) throws IOException {
        this.socket = clientSocket;
        this.server = server;
        this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
    }

    /**
     * Main thread execution for handling client commands.
     * Reads input commands from the client, processes them, and sends responses.
     */
    @Override
    public void run() {
        try (socket) { // auto-close socket at the end
            System.out.println("[Server " + server.getServerId() + "] new client connected from "
                    + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());

            sendPacket("[Server] " + server.getTextualData());

            // Read commands sent by the client in a loop
            String line;
            while ((line = in.readLine()) != null) {
                // Split the command into action and optional argument
                String[] parts = line.split(" ", 2);
                String command = parts[0];
                String argument = parts.length > 1 ? parts[1] : "";

                // Process the command using a switch statement
                switch (command.toUpperCase()) {
                    case "JOIN":
                        // Call the JOIN command from server.serverCommands
                        new server.serverCommands.Join(this, argument).call();
                        break;

                    case "HIT":
                        System.out.println("[Server] Player requested HIT");
                        sendPacket("OK HIT");
                        break;

                    case "BET":
                        try {
                            int betAmount = Integer.parseInt(argument); // Convert string to integer
                            System.out.println("[Server] Player bet: " + betAmount);
                            sendPacket("BET_ACCEPTED " + betAmount);
                        } catch (NumberFormatException e) {
                            sendPacket("ERROR Invalid bet amount");
                        }
                        break;

                    case "STAND":
                        System.out.println("[Server] Player requested STAND");
                        sendPacket("OK STAND");
                        break;

                    default:
                        sendPacket("ERROR Unknown command");
                        break;
                }
            }

            System.out.println("[Server " + server.getServerId() + "] closing connection");

        } catch (IOException e) {
            // Handle exceptions such as client disconnects or I/O errors
            System.err.println("[Server " + server.getServerId() + "] exception: " + e);
        }
    }

    /**
     * Sends a message to the client.
     *
     * @param message text to send
     * @throws IOException if writing fails
     */
    public void sendPacket(String message) throws IOException {
        out.write(message + "\n");
        out.flush();
    }

    /**
     * Getter / Setter for playerName
     */
    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String name) {
        this.playerName = name;
    }
}
