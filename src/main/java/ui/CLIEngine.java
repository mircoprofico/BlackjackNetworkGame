package ui;

import java.io.IOException;

public class CLIEngine {
    private final CLIScreen screen;
    public static boolean RUNNING = false;

    private final int screenWidth = 80;
    private final int screenHeight = 40;

    public CLIEngine() {
        screen = new CLIScreen(screenWidth, screenHeight);
    }

    public void startEngine() throws IOException {
        System.out.print("\033[?1049h"); // alternate buffer
        RUNNING = true;
        // Turn off echo (Unix-like)
        Runtime.getRuntime().exec(new String[]{"/bin/sh","-c","stty raw -echo </dev/tty"});
    }

    public void endEngine() throws IOException {
        System.out.print("\033[?1049l"); // back to normal buffer
        RUNNING = false;
        Runtime.getRuntime().exec(new String[]{"/bin/sh","-c","stty cooked echo </dev/tty"});
    }

    public void update() {
        System.out.print("\033[H\033[2J"); // clear screen + top-left
        screen.RenderObjects();
        System.out.print(screen);
        System.out.flush();
    }

    public void add(RenderableObject object, int x, int y) {
        screen.AddObject(object, x, y);
        update();
    }

    public void add(RenderableObject object) {
        screen.AddObject(object);
        update();
    }

    public void remove(RenderableObject object) {
        screen.RemoveObject(object);
        update();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
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

        while (RUNNING) {
            waitingForNext = false;
            int read = System.in.read();
            char keyPressed = (char) read;
            if (read == -1) continue;
            if (keyPressed == ' ') {
                switch (currentPanel.getCurrentOption()){
                    // We are in the sp panel
                    case "HIT":
                        // todo envoyer message hit au serveur
                        Card s = new Card(13, 11, '7', '♤'); // todo creer carte avec réponse
                        nextCardPlacement += 2;
                        engine.add(s, nextCardPlacement, nextCardPlacement);
                        break;
                    case "STAND":
                        // todo envoyer message stand au serveur
                        currentPanel.changeOption(-1);
                        engine.remove(currentPanel);
                        waitingForNext = true;
                        break;

                    // We are in the betpanel
                    case "ACCEPT":
                        // todo envoyer le bet
                        engine.remove(currentPanel);
                        currentPanel = sp;
                        engine.add(currentPanel);
                        waitingForNext = true;
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
                RUNNING = false; // quit the application
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
    }
}
