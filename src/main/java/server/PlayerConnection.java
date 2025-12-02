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
    private final GameManager gameManager;
    /**
     * Constructor for a new player connection.
     *
     * @param clientSocket the socket representing the client connection
     * @param server       reference to the main server
     */
    public PlayerConnection(Socket clientSocket, Server server, GameManager gameManager) {
        this.socket = clientSocket;
        this.server = server;
        this.gameManager = gameManager;
    }

    /**
     * Main thread execution for handling client commands.
     * Reads input commands from the client, processes them, and sends responses.
     */
    @Override
    public void run() {
        int money = server.BASE_MONEY;
        try (socket; // Use try-with-resources to auto-close the socket
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8))) {

            System.out.println("[Server " + server.getServerId() + "] new client connected from "
                    + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());

            out.write("[Server] " + server.getTextualData() + "\n");
            out.flush();
            String[] joinMessage = in.readLine().split(" ");

            if (joinMessage.length>1 && joinMessage[0].equals("JOIN")) {
                gameManager.joinGame(this);
                System.out.println("[Server] Player joined: " + joinMessage[1]);
                out.write("WELCOME " + server.BASE_MONEY + "\n");
                out.flush();

            } else {
                out.write("ERROR : JOIN <username> to join the game\n");
            }
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
                    case "HIT":
                        System.out.println("[Server] Player requested HIT");
                        // todo verifier si le joueur n'a pas dépassé 21
                        // todo ajouter une carte sinon
                        // si joueur
                        String card = gameManager.requestCard();
                        out.write("OK HIT " + card + "\n");
                        out.flush();
                        break;

                    case "BET":
                        System.out.println("[Server] Player requested BET");
                        int betAmount = Integer.parseInt(argument);
                        if(betAmount<=money) {
                            out.write("OK BET " + betAmount + "\n");
                            out.flush();
                            money -= betAmount;
                            gameManager.placeBet(this);
                            gameManager.waitForMyTurn(this);

                            out.write("DEAL "+ gameManager.requestCard() + " " + gameManager.requestCard()+"\n");
                            out.flush();
                            //todo ajouter 2 cartes
                        } else {
                            out.write("ERROR : BET " + betAmount + " while player only" +
                                    "has "+ money+"\n");
                        }
                        break;

                    case "STAND":
                        gameManager.nextPlayer(); // passer au joueur suivant
                        gameManager.waitForMyTurn(this);
                        // todo implémenter logique de fin
                        out.write("RESULT WIN 20\n");
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
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
