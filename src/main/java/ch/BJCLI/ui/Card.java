package ch.BJCLI.ui;

public class Card extends RenderableObject{

    private Card(int width, int height, String value, String color, char[] sides) {
        super(0, 0, width, height);
        if(width<5 || height<5) throw new RuntimeException("The card should be at least 5x5");
        this.setWidth(width);
        this.setHeight(height);
        char[][] image = new char[height][width];
        setBorder(image, sides);
        setNumbers(image, value, color);
        setStatus(image);
    }
    public Card(int width, int height, String value, String color) {
        this(width, height, value, color, new char[]{'│','─'});
    }

    private void setNumbers(char[][] image, String value, String color){

        String realColor = "?";
        switch(color){
            case "H":
                realColor = "♡";
                break;
            case "D":
                realColor = "♢";
                break;
            case "C":
                realColor = "♧";
                break;
            case "S":
                realColor = "♤";
                break;
        }
        char[] values = (value + realColor).toCharArray();
        int x = 1;
        int y = 1;

        for(int i = 0; i < values.length; i++){
            image[y][x] = values[i];
            image[getHeight()-2][getWidth()-1-x] = values[i];
            x++;
        }

        if(getWidth() % 2 == 1 && getHeight() % 2 == 1){
            image[getHeight()/2][getWidth()/2] = color.charAt(0);
        }
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
                if(corner) image[y][x] = '┼';
                else if(vertical) image[y][x] = sides[0];
                else if(horizontal) image[y][x] = sides[1];
                else image[y][x] = ' ';
            }
        }
    }
}
