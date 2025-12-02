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
}
