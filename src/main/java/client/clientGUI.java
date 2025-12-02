package client;

import ui.*;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class clientGUI {

    // Server configuration
    private static final String HOST = "localhost";
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

        // Try-with-resources to automatically close socket, reader, writer, and scanner
        try (Socket socket = new Socket(HOST, PORT);
             BufferedReader in = new BufferedReader(
                     new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             BufferedWriter out = new BufferedWriter(
                     new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
             Scanner scanner = new Scanner(System.in)) {

            String hello = in.readLine();
            System.out.println(hello);

            CLIEngine engine = new CLIEngine();
            engine.startEngine();

            // For action selection
            SelectionPannel sp = new SelectionPannel(20, 33, 40, 5, new String[]{"HIT", "STAND"});
            int totalValue = 0;
            // For bets
            SelectionPannel betPanel = new SelectionPannel(20, 25, 30, 5, new String[]{"↑↑↑↑", "↓↓↓↓", "ACCEPT"});
            RenderedText bets = new RenderedText(1, 1, 20, 2, " ");
            int currentBet = 5;
            bets.update("Current bet : " + currentBet + " $");
            engine.add(bets);
            engine.add(betPanel);


            SelectionPannel currentPanel = betPanel;
            engine.add(new Border(0, 0, 80, 40));

            int nextCardPlacement = 6;
            engine.add(new Card(13, 11, '4', '♡'), nextCardPlacement, nextCardPlacement);
            boolean waitingForNext = false;

            while (CLIEngine.RUNNING) {
                waitingForNext = false;
                int read = System.in.read();
                char keyPressed = (char) read;
                if (read == -1) continue;
                if (keyPressed == ' ') {
                    switch (currentPanel.getCurrentOption()){
                        // We are in the sp panel
                        case "HIT":
                            out.write("HIT\n");
                            out.flush();
                            String response = in.readLine();
                            if(response.startsWith("OK HIT")){
                                Card s = new Card(13, 11, response.charAt(7), response.charAt(9));
                                nextCardPlacement += 2;
                                engine.add(s, nextCardPlacement, nextCardPlacement);
                            } else {
                                // todo throw error can't hit
                                throw new RuntimeException();
                            }
                            break;
                        case "STAND":
                            currentPanel.changeOption(-1);
                            out.write("STAND\n");
                            out.flush();
                            if(in.readLine().startsWith("OK HIT")) {
                                engine.remove(currentPanel);
                                waitingForNext = true;
                            } else {
                                // todo throw error can't stand
                                throw new RuntimeException();
                            }
                            break;

                        // We are in the betpanel
                        case "ACCEPT":
                            out.write("BET " + currentBet + "\n");
                            out.flush();
                            if(in.readLine().startsWith("OK HIT")) {
                                engine.remove(currentPanel);
                                currentPanel = sp;
                                engine.add(currentPanel);
                                waitingForNext = true;
                            } else {
                                // todo throw error can't bet that
                                throw new RuntimeException();
                            }

                            break;

                        case "↑↑↑↑":
                            currentBet += 5;
                            bets.update("Current bet : " + currentBet + " $");
                            break;
                        case "↓↓↓↓":
                            currentBet -= (currentBet <= 5) ? 0 : 5;
                            bets.update("Current bet : " + currentBet + " $");
                            break;
                    }

                } else if (keyPressed == 'q') {
                    CLIEngine.RUNNING = false; // quit the application
                } else if (keyPressed == 'a') {
                    currentPanel.changeOption(-1);
                } else if (keyPressed == 'd') {
                    currentPanel.changeOption(1);
                }

                engine.update();
                if(waitingForNext) {
                    // TODO PLAY WHEN TURN AFTER STAND
                    // TODO PLAY WHEN TURN AFTER BET
                    // TODO receive info from game when other plays
                }
            }
            engine.endEngine();

        } catch (IOException e) {
            // Handle connection or I/O errors
            System.out.println("[Client " + CLIENT_ID + "] exception: " + e);
        }
    }
}
