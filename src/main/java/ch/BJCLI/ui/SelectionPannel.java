package ch.BJCLI.ui;

public class SelectionPannel extends RenderableObject{

    private SelectionOption[] options;
    private int currentOption = 0;
    private char[] borders = new char[]{'░', '▓'};
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
                options[i].changeBorderChar(borders[1]);
            }
            options[i].placeOption(computedStatus);
        }
        setStatus(computedStatus);
    }

    public void changeOption(int displacement){
        options[currentOption].changeBorderChar(borders[0]);
        currentOption += displacement;
        currentOption %= options.length;
        while(currentOption < 0){
            currentOption += options.length;
        }
        options[currentOption].changeBorderChar(borders[1]);

        char[][] computed = new char[getHeight()][getWidth()];
        for (SelectionOption opt : options)
            opt.placeOption(computed);
        setStatus(computed);
    }

    public String getCurrentOption(){return options[currentOption].getName();}

    class SelectionOption{
        char[][] status;
        int x;
        int y;
        char border = '░';
        private String optName;
        protected SelectionOption(int x, int y, int width, int height, String name) {
            status = new char[height][width];
            this.x = x;
            this.y = y;
            optName = name;
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
        public String getName(){
            return optName;
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
