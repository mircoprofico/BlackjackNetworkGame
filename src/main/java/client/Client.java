package client;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client {

    // Server configuration
    private static final String HOST = "server";
    private static final int PORT = 1234;

    // Random client ID for logging purposes
    private static final int CLIENT_ID = (int) (Math.random() * 1000000);
    private static final String TEXTUAL_DATA = "👋 from Player " + CLIENT_ID;

    /**
     * Main entry point for the client application.
     * Connects to the server and allows the user to send commands interactively.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("[Client " + CLIENT_ID + "] starting with id " + CLIENT_ID);
        System.out.println("[Client " + CLIENT_ID + "] connecting to " + HOST + ":" + PORT);

        // Try-with-resources to automatically close socket, reader, writer, and scanner
        try (Socket socket = new Socket(HOST, PORT);
             BufferedReader in = new BufferedReader(
                     new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             BufferedWriter out = new BufferedWriter(
                     new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("[Client " + CLIENT_ID + "] connected");

            // read server welcome message
            String hello = in.readLine();
            System.out.println(hello);


            // Main loop: read user input and send commands to the server
            while (true) {
                System.out.print("> ");
                String command = scanner.nextLine();

                // Exit the client if the user types QUIT
                if (command.equalsIgnoreCase("QUIT")) {
                    System.out.println("[Client] closing...");
                    break;
                }

                // Send the command to the server
                out.write(command + "\n");
                out.flush();

                // Wait for the server response and print it
                String response = in.readLine();
                System.out.println("[Server] " + response);
            }

        } catch (IOException e) {
            // Handle connection or I/O errors
            System.out.println("[Client " + CLIENT_ID + "] exception: " + e);
        }
    }
}
