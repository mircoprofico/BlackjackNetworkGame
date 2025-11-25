package ui;

public class Border extends RenderableObject{

    protected Border(int x, int y, int width, int height) {
        super(x, y, width, height);
        char[][] borderTab = new char[height][width];
        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                if(i==0 || j == 0 || i == height-1 || j == width-1){
                    borderTab[i][j] = '#';
                } else {
                    //borderTab[i][j] = ' ';
                }
            }
        }

        setStatus(borderTab);
    }
}
