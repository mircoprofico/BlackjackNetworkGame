package ch.BJCLI.client;

import ch.BJCLI.ui.*;
import picocli.CommandLine;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;



@CommandLine.Command(name = "client", mixinStandardHelpOptions = true, version = "1.0",
        description = "Launch a new instance of clientGUI, will prompt for name")
public class clientGUI implements Runnable{
    // Server configuration

    @CommandLine.Option(names = {"-h", "--host"}, description = "The server host name")
    private String HOST = "localhost";

    @CommandLine.Option(names = {"-p", "--port"}, description = "The port to connect to")
    private int PORT = 1234;

    // Random ch.BJCLI.client ID for logging purposes
    private static final int CLIENT_ID = (int) (Math.random() * 1000000);

    // UI constants
    final static int BASE_CARD_PLACEMENT = 6;
    private static int money;

    // Message to send at the end of the game. Can be modified to handle errors
    private static String END_MESSAGE = "Thanks for playing! Come back any time !";
    private static int getTotal(ArrayList<String> cards) {
        int tot = 0;
        int a = 0;
        for(String card : cards) {
            switch (card) {
                case "J": case "Q": case "K":
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

    @Override
    public void run() {
        // get player name
        Scanner sc = new Scanner(System.in);
        System.out.print("Type a username : ");
        String username = sc.nextLine().trim();
        ArrayList<Card> renderedCards = new ArrayList<>();
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
            //System.out.println(hello);

            /**
             * UI : From Here, we create every ch.BJCLI.ui component necessary for the graphical application.
             * The application works using a custom engine.
             */
            // Engine creation and addition of objects
            CLIEngine engine = new CLIEngine();
            engine.startEngine();

            int posStrX = (80 - username.length()) / 2;

            SelectionPannel sp = new SelectionPannel(20, 33, 40, 5, new String[]{"HIT", "STAND"});
            SelectionPannel betPanel = new SelectionPannel(20, 25, 30, 5, new String[]{"↑↑↑↑", "↓↓↓↓", "ACCEPT"});
            SelectionPannel currentPanel = betPanel;
            RenderedText bets = new RenderedText(1, 1, 30, 2, " ");

            RenderedText commands = new RenderedText(60, 1, 18, 2, "-----COMMANDS-----");
            RenderedText commands1 = new RenderedText(60, 2, 18, 2, "a     :   <-");
            RenderedText commands2 = new RenderedText(60, 3, 18, 2, "d     :   ->");
            RenderedText commands3 = new RenderedText(60, 4, 18, 2, "space : confirm");
            Border commandBorder = new Border(58, 0, 21, 6);

            Border playerTagBorder = new Border(posStrX - 1, 0, username.length() + 2, 3);
            RenderedText playerTag = new RenderedText(posStrX, 1, username.length(), 1, username);

            int currentBet = 5;
            bets.update("Current bet : " + currentBet + " $");

            engine.add(commandBorder);
            engine.add(commands);
            engine.add(commands1);
            engine.add(commands2);
            engine.add(commands3);
            engine.add(bets);
            engine.add(betPanel);
            engine.add(new Border(0, 0, 80, 40));
            engine.add(playerTagBorder);
            engine.add(playerTag);

            int nextCardPlacement = BASE_CARD_PLACEMENT;

            /**
             * JOIN MESSAGE : To connect to the game, the user has to send join, followed by its username. He will
             * Be given it's balance
             *
             */
            out.write("JOIN " + username + "\n");
            out.flush();
            money = 0;
            String serverMessage = in.readLine();
            if (serverMessage.startsWith("WELCOME ")) {
                money = Integer.parseInt(serverMessage.replace("WELCOME ", ""));
            } else {
                CLIEngine.RUNNING = false;
                END_MESSAGE = "Received a message other than WELCOME after a join. Aborting...";
            }

            RenderedText moneyText = new RenderedText(1, 2, 20, 2, "Current money : " + money + " $");
            engine.add(moneyText);

            /**
             * Management of the score
             */
            ArrayList<String> hand = new ArrayList<>();
            RenderedText totalText = new RenderedText(60, 30, 12, 1, "Total : " + 0);
            engine.add(totalText);
            RenderedText lastResult = new RenderedText(2, 38, 60, 2, "");
            engine.add(lastResult);


            /**
             * SIMULATION LOOP
             */
            while (CLIEngine.RUNNING) {
                int read = System.in.read();
                char keyPressed = (char) read;
                if (read == -1) continue;
                if (keyPressed == ' ') {
                    switch (currentPanel.getCurrentOption()) {
                        /**
                         * These cases shall only be available if the current pannel is the selection panel
                         */
                        case "HIT":
                            out.write("HIT\n");
                            out.flush();
                            String[] response = in.readLine().split(" ");
                            if (response[0].equals("OK") && response[1].equals("HIT")) {
                                Card s = new Card(13, 11, response[2], response[3]);
                                renderedCards.add(s);
                                engine.add(s, nextCardPlacement, nextCardPlacement);
                                nextCardPlacement += 2;
                                hand.add(response[2]);
                                int total = getTotal(hand);
                                if(total > 21){
                                    totalText.update("BUSTED :( ");
                                    engine.update();
                                    STAND_CALL(out, in, lastResult, moneyText, hand);
                                    resetBetValue(bets);
                                    for(Card card : renderedCards){
                                        engine.remove(card);
                                    }
                                    nextCardPlacement = BASE_CARD_PLACEMENT;
                                    renderedCards.clear();

                                    engine.remove(currentPanel);
                                    currentPanel = betPanel;
                                    engine.add(currentPanel);

                                    engine.update();
                                } else {
                                    totalText.update("Total : " + total);
                                    if(total == 21){
                                        totalText.update("Total : " + total);
                                        engine.update();
                                        STAND_CALL(out, in, lastResult, moneyText, hand);
                                        resetBetValue(bets);
                                        for(Card card : renderedCards){
                                            engine.remove(card);
                                        }
                                        nextCardPlacement = BASE_CARD_PLACEMENT;
                                        renderedCards.clear();
                                        engine.remove(currentPanel);
                                        currentPanel = betPanel;
                                        engine.add(currentPanel);
                                        engine.update();
                                    }
                                }
                            } else {
                                CLIEngine.RUNNING = false;
                                END_MESSAGE = response[0];
                            }
                            break;

                        case "STAND":
                            currentPanel.changeOption(-1);
                            STAND_CALL(out, in, lastResult, moneyText, hand);
                            resetBetValue(bets);
                            for(Card card : renderedCards){
                                engine.remove(card);
                            }
                            nextCardPlacement = BASE_CARD_PLACEMENT;
                            renderedCards.clear();
                            engine.remove(currentPanel);
                            currentPanel = betPanel;
                            engine.add(currentPanel);
                            engine.update();
                            break;

                        /**
                         * These cases shall only be available if the current pannel is the bet panel
                         */
                        case "ACCEPT":
                            out.write("BET " + currentBet + "\n");
                            out.flush();
                            String msg = in.readLine();
                            if (msg.startsWith("OK BET")) {
                                money -= currentBet;
                                currentBet = 5;
                                moneyText.update("Current money : " + money + " $");
                                engine.remove(currentPanel);
                                engine.update();
                                currentPanel = sp;
                                String[] wakeUp = in.readLine().split(" ");
                                if (wakeUp[0].equals("DEAL")) {
                                    engine.add(currentPanel);
                                    Card s = new Card(13, 11, wakeUp[1], wakeUp[2]);
                                    renderedCards.add(s);
                                    engine.add(s, nextCardPlacement, nextCardPlacement);
                                    hand.add(wakeUp[1]);
                                    nextCardPlacement += 2;
                                    Card s2 = new Card(13, 11, wakeUp[3], wakeUp[4]);
                                    renderedCards.add(s2);
                                    engine.add(s2, nextCardPlacement, nextCardPlacement);
                                    hand.add(wakeUp[3]);
                                    nextCardPlacement += 2;
                                    int total  = getTotal(hand);
                                    if(total==21){
                                        totalText.update("BLACK JACK !");
                                        STAND_CALL(out, in, lastResult, moneyText, hand);
                                        resetBetValue(bets);
                                        for(Card card : renderedCards){
                                            engine.remove(card);
                                        }
                                        nextCardPlacement = BASE_CARD_PLACEMENT;
                                        renderedCards.clear();

                                        engine.remove(currentPanel);
                                        currentPanel = betPanel;
                                        engine.add(currentPanel);

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
                            if (currentBet + 5 <= money) {
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
            System.out.println("[Client " + CLIENT_ID + "] exception: " + e);
        }
    }
    private static void resetBetValue(RenderedText betText){betText.update("Current bet : " + 5 + " $");}
    private static void STAND_CALL(BufferedWriter out,
                                   BufferedReader in,
                                   RenderedText lastResult,
                                   RenderedText moneyText,
                                   ArrayList<String> hand) throws IOException
    {
        out.write("STAND\n");
        out.flush();

        String[] result = in.readLine().split(" ");
        if(!result[0].equals("RESULT")) {
            CLIEngine.RUNNING = false;
            END_MESSAGE = "ERROR : "+ result[0];
            return;
        } else {
            switch (result[1]) {
                case "WIN":
                    lastResult.update("Last round won! new balance is " + result[2] + " (dealer score was "+ result[3] +")");
                    break;
                case "LOOSE":
                    lastResult.update("Last round lost! new balance is " + result[2]  + " (dealer score was "+ result[3] +")");
                    if(result[2].equals("0")){
                        END_MESSAGE = "You run out of money :( you were kicked from the casino!";
                        CLIEngine.RUNNING = false;
                    }
                    break;

                case "TIE":
                    lastResult.update("Last round tied! new balance is " + result[2]  + " (dealer score was "+ result[3] +")");
                    break;

                default:
                    CLIEngine.RUNNING = false;
                    END_MESSAGE = "Only possible message after a result is WIN, " +
                            "LOOSE or TIE, but got " + result[1];
            }

            money = Integer.parseInt(result[2]);
            moneyText.update("Current money : " + money + " $");
            hand.clear();
        }
    }
}
