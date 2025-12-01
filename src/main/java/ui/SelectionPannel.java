package ui;

public class SelectionPannel extends RenderableObject{

    SelectionOption[] options;
    // todo change current option with key press
    int currentOption = 0;
    public SelectionPannel(int x, int y, int width, int height, String[] opt) {
        super(x, y, width, height);
        options = new SelectionOption[opt.length];
        char[][] computedStatus = new char[height][width];
        for(int i = 0; i < opt.length; i++){
            int optionWidth = width / opt.length;
            int xPos = optionWidth * i;

// If last option -> use remaining space
            if (i == opt.length - 1) {
                optionWidth = width - xPos;
            }

            options[i] = new SelectionOption(xPos, 0, optionWidth, height, opt[i]);
            if (i == 0) {
                options[i].changeBorderChar('▓');
            }
            options[i].placeOption(computedStatus);
        }
        setStatus(computedStatus);
    }

    class SelectionOption{
        char[][] status;
        int x;
        int y;
        char border = '░';
        protected SelectionOption(int x, int y, int width, int height, String name) {
            status = new char[height][width];
            this.x = x;
            this.y = y;
            recomputeBorder();
            // Word placement
            int wordYPlacement = height/2;
            if(name.length() > width - 2){
                String toWrite = name.substring(0, width - 2);
                for(int i = 0; i < toWrite.length(); i++){
                    status[wordYPlacement][i+1] = toWrite.charAt(i);
                }
            } else {
                int wordXStart = ((width-2)-name.length())/2 + 1;
                for(int i = 0; i < name.length(); i++){
                    status[wordYPlacement][wordXStart + i] = name.charAt(i);
                }
            }
        }
        protected void changeBorderChar(char newChar){
            border = newChar;
            recomputeBorder();
        }

        protected void recomputeBorder(){
            for(int i = 0; i < status.length; i++){
                for(int j = 0; j < status[0].length; j++){
                    if(i == 0 || j == 0 || i == status.length-1 || j == status[0].length-1){
                        status[i][j]=border;
                    }
                }
            }
        }


        protected void placeOption(char[][] dest){
            for(int i = 0; i < status.length; i++){
                for(int j = 0; j < status[0].length; j++){
                    if(dest.length > y+i && dest[0].length > x+j) {
                        dest[y + i][x + j] = status[i][j];
                    }
                }
            }
        }
    }
}
