package ui;

public class Card extends RenderableObject{

    public Card(int width, int height, char value, char color, char[] sides) {
        if(width<5 || height<5) throw new RuntimeException("The card should be at least 5x5");
        this.setWidth(width);
        this.setHeight(height);
        char[][] image = new char[height][width];
        setBorder(image, sides);
        setNumbers(image, value, color);
        setStatus(image);
    }
    public Card(int width, int height, char value, char color) {
        this(width, height, value, color, new char[]{'│','─'});
    }


    private void setNumbers(char[][] image, char value, char color){
        image[1][1] = value;
        image[1][2] = color;
        if(getWidth() % 2 == 1 && getHeight() % 2 == 1){
            image[getHeight()/2][getWidth()/2] = color;
        }
        image[getHeight()-2][getWidth()-2] = value;
        image[getHeight()-2][getWidth()-3] = color;



    }

    private void setBorder(char[][] image, char[] sides){
        // Those two booleans are used to detect if we are drawing edges
        boolean horizontal = false;
        boolean vertical = false;
        boolean corner = false;
        int height = this.getHeight();
        int width = this.getWidth();

        for(int y=0;y<height;y++){
            horizontal = y % (height - 1) == 0;

            for(int x=0;x<width;x++){
                vertical = x % (width - 1) == 0;

                corner = horizontal && vertical;
                if(corner) image[y][x] = 'X';
                else if(vertical) image[y][x] = sides[0];
                else if(horizontal) image[y][x] = sides[1];
                else image[y][x] = ' ';
            }
        }
    }
}
