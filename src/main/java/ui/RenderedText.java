package ui;

public class RenderedText extends RenderableObject{
    protected RenderedText(int x, int y, int width, int height, String text) {
        super(x, y, width, height);
        update(text);
    }
    public void update(String newText) {
        char status[][] = new char[this.getHeight()][this.getWidth()];
        int i = 0;
        int j = 0;
        for (char c : newText.toCharArray()) {
            status[j][i] = c;
            i++;
            if (i >= this.getWidth()) {
                i = 0;
                j += 1;
            }
            if (j >= this.getHeight()) {
                j = 0;
            }
        }
        setStatus(status);
    }
}
