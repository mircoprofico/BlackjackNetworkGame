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
    private final Server server;   // Reference to the main server instance

    /**
     * Constructor for a new player connection.
     *
     * @param clientSocket the socket representing the client connection
     * @param server       reference to the main server
     */
    public PlayerConnection(Socket clientSocket, Server server) {
        this.socket = clientSocket;
        this.server = server;
    }

    /**
     * Main thread execution for handling client commands.
     * Reads input commands from the client, processes them, and sends responses.
     */
    @Override
    public void run() {
        try (socket; // Use try-with-resources to auto-close the socket
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8))) {

            System.out.println("[Server " + server.getServerId() + "] new client connected from "
                    + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());

            out.write("[Server] " + server.getTextualData() + "\n");
            out.flush();


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
                        System.out.println("[Server] Player joined: " + argument);
                        out.write("WELCOME " + argument + "\n");
                        out.flush();
                        break;

                    case "HIT":
                        System.out.println("[Server] Player requested HIT");
                        out.write("OK HIT\n");
                        out.flush();
                        break;

                    case "BET":
                        try {
                            int betAmount = Integer.parseInt(argument); // Convert string to integer
                            System.out.println("[Server] Player bet: " + betAmount);

                            out.write("BET_ACCEPTED " + betAmount + "\n");
                            out.flush();
                        } catch (NumberFormatException e) {
                            out.write("ERROR Invalid bet amount\n");
                            out.flush();
                        }
                        break;

                    case "STAND":
                        System.out.println("[Server] Player requested STAND");
                        out.write("OK STAND\n");
                        out.flush();
                        break;

                    default:
                        // Unknown command received
                        out.write("ERROR Unknown command\n");
                        out.flush();
                        break;
                }
            }

            System.out.println("[Server " + server.getServerId() + "] closing connection");

        } catch (IOException e) {
            // Handle exceptions such as client disconnects or I/O errors
            System.err.println("[Server " + server.getServerId() + "] exception: " + e);
        }
    }
}
