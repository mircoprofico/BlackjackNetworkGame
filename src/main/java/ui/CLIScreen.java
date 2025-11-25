package ui;
import java.util.ArrayList;
import java.util.Arrays;

public class CLIScreen {
    private final int width;
    private final int height;
    private final char[][] characters;
    private ArrayList<RenderableObject> objects = new ArrayList<RenderableObject>();

    public CLIScreen(int width, int height) {
        this.width = width;
        this.height = height;
        characters = new char[height][width];
        //fillAll(' ');
    }

    public CLIScreen fillAll(char value){
        for(int i = 0; i < height; i++) {
            Arrays.fill(characters[i], value);
        }
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
}
