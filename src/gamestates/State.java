package gamestates;

import UI.MainMenuButton;
import UI.MenuButton;
import UI.OptionsButtons;
import main.Game;

import java.awt.event.MouseEvent;

public class State {

    protected Game game;
    public State(Game game){
        this.game = game;

    }

    public boolean isIn(MouseEvent e, MainMenuButton mb){
        return mb.getBounds().contains(e.getX(), e.getY());
    }

    public boolean isIn(MouseEvent e, MenuButton mb){
        return mb.getBounds().contains(e.getX(), e.getY());
    }

    public boolean isIn(MouseEvent e, OptionsButtons mb){
        return mb.getBounds().contains(e.getX(), e.getY());
    }

    public Game getGame()
    {
        return game;
    }

}
