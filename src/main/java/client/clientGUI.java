package client;

import ui.*;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;

public class clientGUI {
    // Server configuration
    private static final String HOST = "localhost";
    private static final int PORT = 1234;

    // Random client ID for logging purposes
    private static final int CLIENT_ID = (int) (Math.random() * 1000000);
    private static final String TEXTUAL_DATA = "👋 from Player " + CLIENT_ID;

    // Message to send at the end of the game. Can be modified to handle errors
    private static String END_MESSAGE = "Thanks for playing! Come back any time !";
    private static int getTotal(ArrayList<String> cards) {
        int tot = 0;
        int a = 0;
        for(String card : cards) {
            switch (card) {
                case "J":
                case "Q":
                case "K":
                    tot +=10;
                    break;
                case "A":
                    tot +=11;
                    a++;
                    break;
                default:
                    tot +=Integer.parseInt(card);
            }
        }
        while (tot > 21 && a > 0) {
            tot -=10;
            a -= 1;
        }
        return tot;
    }
    /**
     * Main entry point for the client application.
     * Connects to the server and allows the user to send commands interactively.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {

        // get player name
        Scanner sc = new Scanner(System.in);
        System.out.print("Type a username : ");
        String username = sc.nextLine().trim();
        while (username.isEmpty()) {
            System.out.println("Username can't be empty Type a valid username: ");
            username = sc.nextLine().trim();
        }

        // Try-with-resources to automatically close socket, reader, writer, and scanner
        try (Socket socket = new Socket(HOST, PORT);
             BufferedReader in = new BufferedReader(
                     new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             BufferedWriter out = new BufferedWriter(
                     new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
             Scanner scanner = new Scanner(System.in)) {
            String hello = in.readLine();
            System.out.println(hello);

            // Engine creation and addition of objects
            CLIEngine engine = new CLIEngine();
            engine.startEngine();

            // For action selection
            SelectionPannel sp = new SelectionPannel(20, 33, 40, 5, new String[]{"HIT", "STAND"});
            int totalValue = 0;

            // For bets
            SelectionPannel betPanel = new SelectionPannel(20, 25, 30, 5, new String[]{"↑↑↑↑", "↓↓↓↓", "ACCEPT"});
            RenderedText bets = new RenderedText(1, 1, 30, 2, " ");
            int currentBet = 5;
            bets.update("Current bet : " + currentBet + " $");
            engine.add(bets);
            engine.add(betPanel);


            SelectionPannel currentPanel = betPanel;
            engine.add(new Border(0, 0, 80, 40));

            int nextCardPlacement = 6;

            // Name rendering
            int posStrX = (80 - username.length()) / 2;
            Border playerTagBorder = new Border(posStrX - 1, 0, username.length() + 2, 3);
            RenderedText playerTag = new RenderedText(posStrX, 1, username.length(), 1, username);

            engine.add(playerTagBorder);
            engine.add(playerTag);

            // JOIN the game, done only once
            out.write("JOIN " + username + "\n");
            out.flush();
            int money = -1;
            String serverMessage = in.readLine();
            if (serverMessage.startsWith("WELCOME ")) {
                money = Integer.parseInt(serverMessage.replace("WELCOME ", ""));
            }
            RenderedText moneyText = new RenderedText(1, 2, 20, 2, "Current money : " + money + " $");
            engine.add(moneyText);

            /**
             * Management of the score
             */
            ArrayList<String> hand = new ArrayList<>();
            RenderedText totalText = new RenderedText(60, 30, 12, 1, "Total : " + 0);
            engine.add(totalText);
            RenderedText lastResult = new RenderedText(60, 2, 19, 1, "Last round ");
            while (CLIEngine.RUNNING) {
                int read = System.in.read();
                char keyPressed = (char) read;
                if (read == -1) continue;

                /**
                 * LOOP IF A KEY HAS BEEN PRESSED
                 */
                if (keyPressed == ' ') {
                    switch (currentPanel.getCurrentOption()) {
                        // We are in the sp panel
                        case "HIT":
                            out.write("HIT\n");
                            out.flush();
                            String[] response = in.readLine().split(" ");
                            if (response[0].equals("OK") && response[1].equals("HIT")) {
                                Card s = new Card(13, 11, response[2], response[3]);

                                engine.add(s, nextCardPlacement, nextCardPlacement);
                                nextCardPlacement += 2;
                                hand.add(response[2]);
                                int total = getTotal(hand);
                                if(total > 21){
                                    totalText.update("BUSTED :( ");
                                    engine.update();
                                    STAND_CALL(out, in, lastResult, moneyText, hand);
                                    engine.update();
                                } else {
                                    totalText.update("Total : " + total);
                                    if(total == 21){
                                        totalText.update("Total : " + total);
                                        engine.update();
                                        STAND_CALL(out, in, lastResult, moneyText, hand);
                                        engine.update();
                                    }
                                }
                            } else {
                                CLIEngine.RUNNING = false;
                                END_MESSAGE = "ERROR : Can't hit";
                            }
                            break;

                        case "STAND":
                            currentPanel.changeOption(-1);
                            STAND_CALL(out, in, lastResult, moneyText, hand);
                            engine.update();

                            break;

                        /**
                         * This is where we check for the bet panel
                         */
                        case "ACCEPT":
                            out.write("BET " + currentBet + "\n");
                            out.flush();
                            String msg = in.readLine();
                            if (msg.startsWith("OK BET")) {
                                money -= currentBet;
                                moneyText.update("Current money : " + money + " $");
                                engine.remove(currentPanel);
                                currentPanel = sp;
                                String[] wakeUp = in.readLine().split(" ");
                                if (wakeUp[0].equals("DEAL")) {
                                    engine.add(currentPanel);
                                    Card s = new Card(13, 11, wakeUp[1], wakeUp[2]);
                                    engine.add(s, nextCardPlacement, nextCardPlacement);
                                    hand.add(wakeUp[1]);
                                    nextCardPlacement += 2;
                                    Card s2 = new Card(13, 11, wakeUp[3], wakeUp[4]);
                                    engine.add(s2, nextCardPlacement, nextCardPlacement);
                                    hand.add(wakeUp[3]);
                                    nextCardPlacement += 2;
                                    int total  = getTotal(hand);
                                    if(total==21){
                                        totalText.update("BLACK JACK !");
                                        STAND_CALL(out, in, lastResult, moneyText, hand);
                                        engine.update();

                                    } else {
                                        totalText.update("Total : " + total);
                                    }
                                } else {
                                    CLIEngine.RUNNING = false;
                                    END_MESSAGE = "Never received the DEAL keyword after the BET. instead : " + wakeUp;
                                }
                            } else {
                                CLIEngine.RUNNING = false;
                                END_MESSAGE = msg;
                            }
                            break;

                        case "↑↑↑↑":
                            if (currentBet + 5 < money) {
                                currentBet += 5;
                                bets.update("Current bet : " + currentBet + " $");
                            }
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
                /**
                 * END OF LOOP IF A KEY HAS BEEN PRESSED
                 */
                engine.update();
            }
            engine.endEngine();
            System.out.println(END_MESSAGE);

        } catch (IOException e) {
            // Handle connection or I/O errors
            System.out.println("[Client " + CLIENT_ID + "] exception: " + e);
        }
    }
    private static void STAND_CALL(
            BufferedWriter out,
            BufferedReader in,
            RenderedText lastResult,
            RenderedText moneyText,
            ArrayList<String> hand
    ) throws IOException
    {
        out.write("STAND\n");
        out.flush();

        //String retMsg = in.readLine();

        String[] result = in.readLine().split(" ");
        if(!result[0].equals("RESULT")) {
            CLIEngine.RUNNING = false;
            END_MESSAGE = "ERROR : "+ result[0];
            return;
        } else {
            switch (result[1]) {
                case "WIN":
                    lastResult.update("Last round won! new balance is " + result[2]);
                    break;
                case "LOOSE":
                    lastResult.update("Last round lost! new balance is " + result[2]);
                    break;

                case "TIE":
                    lastResult.update("Last round tied! new balance is " + result[2]);
                    break;

                default:
                    CLIEngine.RUNNING = false;
                    END_MESSAGE = "Only possible message after a result is WIN, " +
                            "LOOSE or TIE, but got " + result[1];
            }

            int money = Integer.parseInt(result[2]);
            moneyText.update("Current money : " + money + " $");
            hand.clear();
        }
    }
}
