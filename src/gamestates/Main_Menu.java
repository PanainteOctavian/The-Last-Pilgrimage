package gamestates;


import UI.MainMenuButton;
import main.Game;
import utilz.LoadSave;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;


public class Main_Menu extends State implements Statemethods {

    private MainMenuButton[] Buttons = new MainMenuButton[4];
    private BufferedImage background;

    public Main_Menu(Game game) {
        super(game);
        background = LoadSave.GetMenuBackground();
        loadButtons();
        Gamestate.state = Gamestate.MAIN_MENU;
    }

    private void loadButtons() {
        Buttons[0] = new MainMenuButton(1024 / 2, (int) (150 * Game.SCALE), 0, Gamestate.PLAYING);
        Buttons[1] = new MainMenuButton(1024 / 2, (int) (220 * Game.SCALE), 1, Gamestate.LOAD);
        Buttons[2] = new MainMenuButton(1024 / 2, (int) (290 * Game.SCALE), 2, Gamestate.OPTIONS);
        Buttons[3] = new MainMenuButton(1024 / 2, (int) (360 * Game.SCALE), 3, Gamestate.EXIT);
    }

    @Override
    public void update() {
        for(MainMenuButton mb:Buttons)
            mb.update();
    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(background, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);
        for(MainMenuButton mb:Buttons)
            mb.draw(g);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        for(MainMenuButton mb:Buttons){
            if(isIn(e, mb)){
                mb.setMousePressed(true);
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        for(MainMenuButton mb:Buttons){
            if(isIn(e, mb)){
                if(mb.isMousePressed()) {
                    if(mb.getState() == Gamestate.PLAYING) {
                        game.getPlaying().startNewGame();
                        Gamestate.state = Gamestate.PLAYING;
                    }
                    else if(mb.getState() == Gamestate.LOAD) {
                        game.getPlaying().loadGame();
                    }
                    else if(mb.getState() == Gamestate.OPTIONS) {
                        // Setăm starea anterioară înainte de a trece la Options
                        game.getOptions().setPreviousState(Gamestate.MAIN_MENU);
                        Gamestate.state = Gamestate.OPTIONS;
                        System.out.println("Main_Menu: Navigating to Options, previousState set to MAIN_MENU"); // Debug
                    }
                    else if(mb.getState() == Gamestate.EXIT) {
                        mb.applyGameState();
                    }
                }
                break;
            }
        }
        resetButtons();
    }

    private void resetButtons() {
        for(MainMenuButton mb:Buttons){
            mb.resetBools();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        for(MainMenuButton mb:Buttons){
            mb.setMouseOver(false);
        }
        for(MainMenuButton mb:Buttons){
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