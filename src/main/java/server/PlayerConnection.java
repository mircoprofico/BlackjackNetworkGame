package server;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class PlayerConnection implements Runnable { // implements Runnable -> promet que la classe va définir une méthode run()
    private final Socket socket;
    private final Server server;

    public PlayerConnection(Socket clientSocket, Server server) {
        this.socket = clientSocket;
        this.server = server;
    }

    @Override
    public void run() {
        try (socket; // use try-with-resources
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8))) {
            System.out.println("[Server " + server.getServerId() + "] new client connected from "
                            + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());


            System.out.println("[Server " + server.getServerId() + "] new client connected from "
                    + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());


            // Lecture de la commande envoyé par le client

            String line = in.readLine();

            // Découper la commande
            String[] parts = line.split(" ", 2);
            String command = parts[0];
            String argument = parts.length > 1 ? parts[1] : "";

            System.out.println("[Server] received command: " + command);

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
                        int betAmount = Integer.parseInt(argument); // conversion
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
                    out.write("ERROR Unknown command\n");
                    out.flush();
                    break;
            }

            System.out.println("[Server " + server.getServerId() + "] closing connection");

        } catch (IOException e) {
            System.err.println("[Server " + server.getServerId() + "] exception: " + e);
        }

    }
}
