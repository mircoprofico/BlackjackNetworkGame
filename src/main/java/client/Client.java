package client;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.Callable;

/**
 * TCP client used to connect to the Blackjack server.
 * Each instance creates a player identified with a random ID.
 * Interactive mode handled via Picocli subcommands: JOIN, BET, HIT.
 */
public class Client {

    // Hostname or IP address of the server to connect to
    private final String HOST;

    // TCP port of the server
    private final int PORT;

    // Random client ID for logging purposes
    private static final int CLIENT_ID = (int) (Math.random() * 1000000);

    // Textual identification message for demonstration
    private final String TEXTUAL_DATA = "👋 from Player " + CLIENT_ID;

    // TCP input and output streams
    private BufferedReader in;
    private BufferedWriter out;

    /**
     * Constructor for the TCP client.
     *
     * @param host server hostname
     * @param port server port
     */
    public Client(String host, int port) {
        this.HOST = host;
        this.PORT = port;
    }

    /**
     * Starts the interactive TCP client.
     * Connects to the server, prints the welcome message,
     * then enters a terminal loop to send commands typed by the user.
     */
    public void start() {
        System.out.println("[Client " + CLIENT_ID + "] starting with id " + CLIENT_ID);
        System.out.println("[Client " + CLIENT_ID + "] connecting to " + HOST + ":" + PORT);

        // Try-with-resources automatically closes the socket, input and output streams, and the scanner
        try (Socket socket = new Socket(HOST, PORT)) {

            // Initialize TCP streams
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));

            System.out.println("[Client " + CLIENT_ID + "] connected");

            // Read server welcome message (first line sent by the server)
            String hello = in.readLine();
            System.out.println(hello);

            // Prepare Picocli command handler
            ClientCommands commands = new ClientCommands(this);
            CommandLine cli = new CommandLine(commands);

            Scanner scanner = new Scanner(System.in);
            String line;

            // Interactive loop
            while (true) {
                System.out.print("> ");
                line = scanner.nextLine();

                // If the user types QUIT, disconnect the client
                if (line.equalsIgnoreCase("QUIT")) {
                    System.out.println("[Client] closing...");
                    break;
                }

                // Execute the typed command with Picocli
                String[] args = line.split(" ");
                cli.execute(args);
            }

        } catch (IOException e) {
            // Handle connection or I/O errors
            System.out.println("[Client " + CLIENT_ID + "] exception: " + e);
        }
    }

    /**
     * Sends a command to the server and waits for the response.
     *
     * @param command The command to send (ex: "JOIN", "BET 50", "HIT")
     * @return The server's response as a String
     * @throws IOException If a network or I/O error occurs
     */
    public String send(String command) throws IOException {
        if (out == null || in == null) {
            throw new IOException("Connection not established. Make sure start() has been called.");
        }

        // Send command
        out.write(command + "\n");
        out.flush();

        // Read first line of response
        String response = in.readLine();
        if (response == null) {
            throw new IOException("Server closed the connection unexpectedly.");
        }

        // Read all additional available lines (like in start())
        while (in.ready()) {
            String extraLine = in.readLine();
            response += "\n" + extraLine;
        }

        return response;
    }

    /**
     * Picocli interactive commands for the client.
     */
    @Command(
            name = "client",
            description = "Interactive Blackjack client commands",
            mixinStandardHelpOptions = true,
            subcommands = {
                    ClientCommands.Join.class
            }
    )
    public static class ClientCommands implements Runnable {

        // TCP client instance to use for sending commands
        public Client client;

        /**
         * Constructor linking ClientCommands to TCP client.
         *
         * @param client TCP client instance
         */
        public ClientCommands(Client client) {
            this.client = client;
        }

        @Override
        public void run() {
            System.out.println("Interactive client. Use subcommands: join, bet, hit.");
        }

        /**
         * JOIN command: join the Blackjack game
         */
        @Command(name = "join", description = "Join the Blackjack game")
        public static class Join implements Callable<Integer> {

            @ParentCommand
            ClientCommands parent;

            @Override
            public Integer call() throws Exception {
                String response = parent.client.send("JOIN");
                System.out.println(response);
                return 0;
            }
        }

        /**
         * BET command: place a bet
         */
        @Command(name = "bet", description = "Place a bet")
        public static class Bet implements Callable<Integer> {

            @ParentCommand
            ClientCommands parent;

            @Parameters(index = "0", description = "Amount to bet")
            int amount;

            @Override
            public Integer call() throws Exception {
                String response = parent.client.send("BET " + amount);
                System.out.println(response);
                return 0;
            }
        }

        /**
         * HIT command: request a card from the server
         */
        @Command(name = "hit", description = "Request a card")
        public static class Hit implements Callable<Integer> {

            @ParentCommand
            ClientCommands parent;

            @Override
            public Integer call() throws Exception {
                String response = parent.client.send("HIT");
                System.out.println(response);
                return 0;
            }
        }
    }
}
