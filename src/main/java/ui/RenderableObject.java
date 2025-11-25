package ui;

public abstract class RenderableObject {
    private char[][] status;
    protected boolean shown = true;
    private int x;
    private int y;
    private int width;
    private int height;
    protected boolean setStatus(char[][] status){
        this.status = status;
        return true;
    }

    public boolean toggleVisibility(){
        shown = !shown;
        return shown;
    }
    public boolean doRender(){return shown;}

    public void setPosition(int x, int y){
        this.x = x;
        this.y = y;
    }
    public int xPos(){return x;}
    public int yPos(){return y;}

    public int getWidth(){return width;}
    public int getHeight(){
        return height;
    }
    public void setWidth(int width) {this.width = Math.max(width, 0);}
    public void setHeight(int height) {this.height = Math.max(height, 0);}

    public char at(int x, int y){
        return status[y][x];
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        for (char[] chars : status) {
            for (int j = 0; j < chars.length; j++) {
                sb.append(chars[j]);
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}
