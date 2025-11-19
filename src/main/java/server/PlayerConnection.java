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

            System.out.println("[Server " + server.getServerId() + "] received textual data from client: " + in.readLine());


            try {
                System.out.println(
                        "[Server " + server.getServerId() + "] sleeping for 10 seconds to simulate a long operation");
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            System.out.println(
                    "[Server " + server.getServerId() + "] sending response to client: " + server.getTextualData());

            out.write(server.getTextualData() + "\n");
            out.flush();

            System.out.println("[Server " + server.getServerId() + "] closing connection");

        } catch (IOException e) {
            System.err.println("[Server " + server.getServerId() + "] exception: " + e);
        }

    }
}
