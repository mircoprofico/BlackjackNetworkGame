package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private static final int PORT = 1234;
    private static final int SERVER_ID = (int) (Math.random() * 1000000);
    private static final String TEXTUAL_DATA = "👋 from Croupier " + SERVER_ID;

    // Accesseurs pour PlayerConnection
    public int getServerId() { return SERVER_ID; }
    public String getTextualData() { return TEXTUAL_DATA; }

    // Méthode principale pour démarrer le serveur
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("[Server " + SERVER_ID + "] starting with id " + SERVER_ID);
            System.out.println("[Server " + SERVER_ID + "] listening on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept(); // bloque jusqu'à qu'un client se connecte
                System.out.println("[Server " + SERVER_ID + "] new client connected: " +
                        clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort());

                // Crée un thread pour gérer ce client
                new Thread(new PlayerConnection(clientSocket, this)).start();
            }

        } catch (IOException e) {
            System.out.println("[Server " + SERVER_ID + "] exception: " + e);
        }
    }


    public static void main(String[] args) {
        new Server().start();
    }
}
