package ui;

public class CLIEngine {
    // TODO determiner largeur et hauteur
    static public boolean RUNNING = false;
    CLIScreen screen;

    public CLIEngine(){
        RUNNING = true;
        screen = new CLIScreen(80, 50);
        screen.fillAll(' ');
        System.out.println(screen);
    }

    public static void main(String[] args) {
        CLIEngine engine = new CLIEngine();
    }
}
