package ui;

public class CLIEngine {
    // TODO determiner largeur et hauteur
    private CLIScreen screen;

    public CLIEngine(){
        screen = new CLIScreen(80, 50);
        screen.fillAll(' ');
        System.out.println(screen);
    }
    public void update(){
        screen.RenderObjects();
        System.out.println(screen);
    }

    public void add(RenderableObject object, int x, int y){
        screen.AddObject(object,  x, y);
    }


    public static void main(String[] args) {
        CLIEngine engine = new CLIEngine();
        Card r = new Card(7, 5, '3', 'A');
        engine.add(r, 10, 10);
        engine.update();
    }
}
