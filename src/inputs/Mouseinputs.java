package inputs;

import gamestates.Gamestate;
import main.GamePanel;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class Mouseinputs implements MouseListener, MouseMotionListener
{
    private GamePanel gamePanel;

    public Mouseinputs(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        switch (Gamestate.state) {
            case PLAYING:
                gamePanel.getGame().getPlaying().mouseClicked(e);
                break;
            case MENU:
                gamePanel.getGame().getMenu().mouseClicked(e);
                break;
            case MAIN_MENU:
                gamePanel.getGame().getMain_menu().mouseClicked(e);
                break;
            case OPTIONS:
                gamePanel.getGame().getOptions().mouseClicked(e);
                break;
            case LEVELCOMPLETE:
                gamePanel.getGame().getLevelComplete().mouseClicked(e);
                break;
            case DEATH:
                gamePanel.getGame().getDeath().mouseClicked(e);
                break;
            default:
                break;
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        switch (Gamestate.state) {
            case MENU:
                gamePanel.getGame().getMenu().mousePressed(e);
                break;
            case PLAYING:
                gamePanel.getGame().getPlaying().mousePressed(e);
                break;
            case MAIN_MENU:
                gamePanel.getGame().getMain_menu().mousePressed(e);
                break;
            case OPTIONS:
                gamePanel.getGame().getOptions().mousePressed(e);
                break;
            case LEVELCOMPLETE:
                gamePanel.getGame().getLevelComplete().mousePressed(e);
                break;
            case DEATH:
                gamePanel.getGame().getDeath().mousePressed(e);
                break;
            default:
                break;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        switch (Gamestate.state) {
            case MENU:
                gamePanel.getGame().getMenu().mouseReleased(e);
                break;
            case PLAYING:
                gamePanel.getGame().getPlaying().mouseReleased(e);
                break;
            case MAIN_MENU:
                gamePanel.getGame().getMain_menu().mouseReleased(e);
                break;
            case OPTIONS:
                gamePanel.getGame().getOptions().mouseReleased(e);
                break;
            case LEVELCOMPLETE:
                gamePanel.getGame().getLevelComplete().mouseReleased(e);
                break;
            case DEATH:
                gamePanel.getGame().getDeath().mouseReleased(e);
                break;
            default:
                break;
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        switch (Gamestate.state) {
            case MENU:
                gamePanel.getGame().getMenu().mouseMoved(e);
                break;
            case PLAYING:
                gamePanel.getGame().getPlaying().mouseMoved(e);
                break;
            case MAIN_MENU:
                gamePanel.getGame().getMain_menu().mouseMoved(e);
            case OPTIONS:
                gamePanel.getGame().getOptions().mouseMoved(e);
                break;
            case LEVELCOMPLETE:
                gamePanel.getGame().getLevelComplete().mouseMoved(e);
                break;
            case DEATH:
                gamePanel.getGame().getDeath().mouseMoved(e);
                break;
            default:
                break;
        }
    }
}
