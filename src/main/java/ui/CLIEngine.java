package ui;

import static java.lang.Thread.sleep;

public class CLIEngine {
    private final CLIScreen screen;
    private static boolean RUNNING = false;

    private int screenWidth = 80;
    private int screenHeight = 40;

    public CLIEngine(){
        // TODO eventuellement determiner largeur et hauteur

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
    public void add(RenderableObject object){
        screen.AddObject(object);
        update();
    }

    public void remove(RenderableObject object){
        screen.RemoveObject(object);
        update();
    }

    public static void main(String[] args) throws InterruptedException {

        CLIEngine engine = new CLIEngine();
        engine.startEngine();
        engine.add(new Border(0, 0, 80, 40));
        engine.add(new SelectionPannel(10, 10, 60, 5, new String[]{"HIT", "STAND", "SPLIT"}));
        sleep(5000);
        engine.endEngine();
    }
}
