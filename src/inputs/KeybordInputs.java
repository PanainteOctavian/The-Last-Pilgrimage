package inputs;

import gamestates.Gamestate;
import main.GamePanel;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


public class KeybordInputs implements KeyListener {
    private GamePanel gamePanel;

    public KeybordInputs(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (Gamestate.state){
            case MENU:
                gamePanel.getGame().getMenu().keyPressed(e);
                break;
            case PLAYING:
                gamePanel.getGame().getPlaying().keyPressed(e);
                break;
            case MAIN_MENU:
                gamePanel.getGame().getMain_menu().keyPressed(e);
                break;
            case OPTIONS:
                gamePanel.getGame().getOptions().keyPressed(e);
                break;
            default:
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (Gamestate.state){
            case MENU:
                gamePanel.getGame().getMenu().keyReleased(e);
                break;
            case PLAYING:
                gamePanel.getGame().getPlaying().keyReleased(e);
                break;
            case MAIN_MENU:
                gamePanel.getGame().getMain_menu().keyReleased(e);
                break;
            case OPTIONS:
                gamePanel.getGame().getOptions().keyReleased(e);
                break;
            default:
                break;
        }
    }
}
