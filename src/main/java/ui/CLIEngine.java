package ui;

import static java.lang.Thread.sleep;

public class CLIEngine {
    // TODO determiner largeur et hauteur
    private final CLIScreen screen;
    private static boolean RUNNING = false;

    private int screenWidth = 80;
    private int screenHeight = 40;

    public CLIEngine(){
        screen = new CLIScreen(screenWidth, screenHeight);
        screen.fillAll(' ');
    }

    public void startEngine(){
        System.out.print("\033[?1049h");
        RUNNING = true;
    }

    public void endEngine(){
        System.out.print("\033[?1049l");
        RUNNING = false;
    }

    public void update(){
        screen.RenderObjects();
        System.out.println(screen);
    }

    public void add(RenderableObject object, int x, int y){
        screen.AddObject(object,  x, y);
        update();
    }

    public void remove(RenderableObject object){
        screen.RemoveObject(object);
        update();
    }

    public static void main(String[] args) throws InterruptedException {

        CLIEngine engine = new CLIEngine();
        engine.startEngine();
        Card r1 = new Card(15, 11, '3', '♡');
        Card r2 = new Card(15, 11, '5', '♢');
        Card r3 = new Card(15, 11, '8', '♣');
        Card r4 = new Card(15, 11, '4', '♠');

        engine.add(new Border(0, 0, 80, 40), 0, 0);
        engine.add(r1, 10, 10);
        engine.add(r2, 11, 12);
        engine.add(r3, 12, 14);
        engine.add(r4, 13, 16);
        engine.update();

        sleep(1000);
        engine.remove(r4);
        sleep(1000);
        engine.remove(r3);
        sleep(1000);
        engine.remove(r2);
        sleep(1000);
        engine.remove(r1);
        sleep(1000);
        engine.endEngine();

    }
}
