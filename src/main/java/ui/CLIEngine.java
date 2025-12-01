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
        SelectionPannel sp = new SelectionPannel(20, 33, 40, 5, new String[]{"HIT", "STAND", "LEAVE"});
        engine.startEngine();
        engine.add(new Border(0, 0, 80, 40));

        int nextCardPlacement = 6;
        engine.add(sp);
        engine.add(new Card(13, 11, '4', '♡'), nextCardPlacement, nextCardPlacement);


        while (RUNNING) {
            int read = System.in.read();
            char keyPressed = (char) read;
            if (read == -1) continue;

            if (keyPressed == ' ') {
                engine.remove(sp); // first we hide the selection panel
                switch (sp.getCurrentOption()){
                    case "HIT":
                        // todo envoyer message hit au serveur
                        Card s = new Card(13, 11, '7', '♤');
                        nextCardPlacement += 2;
                        engine.add(s, nextCardPlacement, nextCardPlacement);
                        // todo attendre réponse et agir en conséquence
                        break;
                    case "STAND":
                        // todo envoyer message stand au serveur
                        // todo attendre réponse et agir en conséquence
                        break;
                    case "LEAVE":
                        RUNNING = false;
                        break;
                }

                engine.add(sp); // we add the selection panel back, so that we can choose next move
            } else if (keyPressed == 'q') {
                RUNNING = false; // quit the application
            } else if (keyPressed == 'a') {
                sp.changeOption(-1);
            } else if (keyPressed == 'd') {
                sp.changeOption(1);
            }

            engine.update();
        }
        engine.endEngine();
    }
}
