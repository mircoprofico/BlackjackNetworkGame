package ch.BJCLI.ui;

import java.util.ArrayList;

public class CLIScreen {
    private final int width;
    private final int height;
    private final char[][] characters;
    private final ArrayList<RenderableObject> objects = new ArrayList<>();

    public CLIScreen(int width, int height) {
        this.width = width;
        this.height = height;
        characters = new char[height][width];
        fillAll(' ');
    }

    public CLIScreen fillAll(char value) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                characters[y][x] = value;
            }
        }
        return this;
    }

    public CLIScreen AddObject(RenderableObject object, int x, int y) {
        object.setPosition(x, y);
        objects.add(object);
        return this;
    }

    public CLIScreen AddObject(RenderableObject object) {
        objects.add(object);
        return this;
    }

    public CLIScreen RemoveObject(RenderableObject object) {
        objects.remove(object);
        return this;
    }

    public void RenderObjects() {
        fillAll(' ');
        for (RenderableObject object : objects) {
            renderSingleObject(object);
        }
    }

    private void renderSingleObject(RenderableObject object) {
        int startX = object.xPos();
        int maxX = Math.min(startX + object.getWidth(), width);
        int startY = object.yPos();
        int maxY = Math.min(startY + object.getHeight(), height);

        for (int y = startY; y < maxY; y++) {
            for (int x = startX; x < maxX; x++) {
                char val = object.at(x - startX, y - startY);
                if (val != 0) characters[y][x] = val;
            }
        }
    }

    public void setAt(int x, int y, char value) {
        characters[y][x] = value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\033[7m");
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                sb.append(characters[y][x]);
            }
            sb.append("\r\n"); // \r needed since we are in raw mode
        }
        sb.append("\033[m");

        return sb.toString();
    }
}
