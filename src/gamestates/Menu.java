package gamestates;


import UI.MenuButton;
import main.Game;
import utilz.LoadSave;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;


public class Menu extends State implements Statemethods {

    private MenuButton[] buttons = new MenuButton[5];
    private BufferedImage background;

    public Menu(Game game) {
        super(game);
        background = LoadSave.GetMenuBackground();
        loadButtons();
    }

    private void loadButtons() {
        buttons[0] = new MenuButton(1024 / 2, (int) (110 * Game.SCALE), 0, Gamestate.PLAYING);
        buttons[1] = new MenuButton(1024 / 2, (int) (180 * Game.SCALE), 1, Gamestate.SAVE);
        buttons[2] = new MenuButton(1024 / 2, (int) (250 * Game.SCALE), 2, Gamestate.LOAD);
        buttons[3] = new MenuButton(1024 / 2, (int) (320 * Game.SCALE), 3, Gamestate.OPTIONS);
        buttons[4] = new MenuButton(1024 / 2, (int) (390 * Game.SCALE), 4, Gamestate.MAIN_MENU);
    }

    @Override
    public void update() {
        for(MenuButton mb:buttons)
            mb.update();

    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(background, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);
        for(MenuButton mb:buttons)
            mb.draw(g);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        for(MenuButton mb:buttons){
            if(isIn(e, mb)){
                mb.setMousePressed(true);
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        for(MenuButton mb:buttons){
            if(isIn(e, mb)){
                if(mb.isMousePressed()) {
                    if(mb.getState() == Gamestate.SAVE) {
                        game.getPlaying().saveGame();
                        Gamestate.state = Gamestate.PLAYING;
                    }
                    else if(mb.getState() == Gamestate.LOAD) {
                        game.getPlaying().loadGame();
                    }
                    else if(mb.getState() == Gamestate.OPTIONS) {
                        // Setăm starea anterioară înainte de a trece la Options
                        game.getOptions().setPreviousState(Gamestate.MENU);
                        Gamestate.state = Gamestate.OPTIONS;
                        System.out.println("Menu: Navigating to Options, previousState set to MENU"); // Debug
                    }
                    else if(mb.getState() == Gamestate.MAIN_MENU) {
                        Gamestate.state = Gamestate.MAIN_MENU;
                    }
                    else {
                        mb.applyGameState();
                    }
                }
                break;
            }
        }
        resetButtons();
    }

    private void resetButtons() {
        for(MenuButton mb:buttons){
            mb.resetBools();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        for(MenuButton mb:buttons){
            mb.setMouseOver(false);
        }
        for(MenuButton mb:buttons){
            if(isIn(e, mb)){
                mb.setMouseOver(true);
                break;
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}