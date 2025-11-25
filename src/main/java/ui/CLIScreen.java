package ui;
import java.util.ArrayList;
import java.util.Arrays;

import static java.lang.Thread.sleep;

public class CLIScreen {
    private final int width;
    private final int height;
    private final char[][] characters;
    private ArrayList<RenderableObject> objects = new ArrayList<RenderableObject>();

    public CLIScreen(int width, int height) {
        this.width = width;
        this.height = height;
        characters = new char[height][width];
        fillAll(' ');
    }

    public CLIScreen fillAll(char value){
        for(int i = 0; i < height; i++) {
            Arrays.fill(characters[i], value);
        }
        return this;
    }
    public CLIScreen fillSide(boolean horizontal, boolean first, char border){
        int max = (horizontal) ? width : height;
        int secondVal = (first) ? 0 : ((horizontal) ? height : width)-1;
        for(int i = 0; i < max; i++){
            if(horizontal)characters[secondVal][i] =  border;
            else characters[i][secondVal] = border;
        }
        return this;
    }
    public CLIScreen fillBorder(char value){
        fillSide(true, true,value);
        fillSide(false, true,value);
        fillSide(true, false,value);
        fillSide(false, false,value);
        return this;
    }

    public CLIScreen AddObject(RenderableObject object, int x, int y){
        object.setPosition(x, y);
        objects.add(object);
        return this;
    }
    public CLIScreen RemoveObject(RenderableObject object){
        if(objects.contains(object)){
            objects.remove(object);
        }
        return this;
    }

    public CLIScreen RenderObjects(){
        this.fillAll(' ');
        for(RenderableObject object : objects) RenderSingularObject(object);
        return this;
    }

    private void RenderSingularObject(RenderableObject object){
        int startX = object.xPos();
        int maxX = Math.min(startX + object.getWidth(), width);
        int startY = object.yPos();
        int maxY = Math.min(startY + object.getHeight(), height);

        for(int x = startX; x < maxX; x++){
            for(int y = startY; y < maxY; y++){
                setAt(x, y, object.at(x-startX,y-startY));
            }
        }
    }
    public void setAt(int x, int y, char value){characters[y][x] = value;}

    @Override
    public String toString() {
        this.RenderObjects();
        StringBuilder sb = new StringBuilder();
        sb.append("\033[2J\033[H"); // 2 escape sequence, clean the screen and go back to top left
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                sb.append(characters[i][j]);
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public static void main(String[] args) throws InterruptedException {
        CLIScreen screen = new CLIScreen(80, 10);


        Card d = new Card(9, 7, '2', 'A', new char[]{'|','━'});
        Card d2 = new Card(9, 7, '3', 'B', new char[]{'|','━'});
        Card d3 = new Card(9, 7, '4', 'C', new char[]{'|','━'});

        screen.AddObject(d, 1, 1).AddObject(d2, 2, 2).AddObject(d3, 3, 3);
        System.out.print(screen);
    }
}
