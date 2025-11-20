package UI;

import gamestates.Gamestate;
import utilz.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;
import static utilz.Constants.UI.Buttons.*;

public class MenuButton {
    private int xPos, yPos, rowIndex, index;
    private BufferedImage[] MB;
    private int xOffsetCenter = B_WIDTH / 2;
    private boolean mouseOver, mousePressed;
    private Rectangle bounds;

    private Gamestate state;
    public MenuButton(int xPos, int yPos, int rowIndex, Gamestate state){
        this.xPos = xPos;
        this.yPos = yPos;
        this.rowIndex = rowIndex;
        this.state = state;
        loadImgs();
        initBounds();
    }

    private void initBounds() {
        bounds = new Rectangle(xPos - xOffsetCenter, yPos, B_WIDTH, B_HEIGHT);
    }

    private void loadImgs() {
        MB = new BufferedImage[4];
        BufferedImage menbut = LoadSave.GetSpriteAtlas(LoadSave.MENU_BUTTONS);
        for(int i = 0; i < MB.length; i++)
            MB[i] = menbut.getSubimage(i * 180, rowIndex * 60, B_WIDTH_DEFAULT, (B_HEIGHT_DEFAULT));
    }

    public void draw(Graphics g){
        g.drawImage(MB[index], xPos - xOffsetCenter, yPos, B_WIDTH, B_HEIGHT, null);
    }
    public  void update(){
        index = 0;
        if(mouseOver || mousePressed)
            index = 1;
    }

    public boolean isMouseOver() {
        return mouseOver;
    }

    public void setMouseOver(boolean mouseOver) {
        this.mouseOver = mouseOver;
    }

    public boolean isMousePressed() {
        return mousePressed;
    }

    public void setMousePressed(boolean mousePressed) {
        this.mousePressed = mousePressed;
    }

    public  void applyGameState(){
        Gamestate.state = state;
    }
    public Rectangle getBounds(){
        return bounds;
    }

    public void resetBools(){
        mouseOver = false;
        mousePressed = false;
    }

    public Gamestate getState() {
        return state;
    }
}
