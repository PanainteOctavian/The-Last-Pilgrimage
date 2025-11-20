package main;

import inputs.KeybordInputs;
import inputs.Mouseinputs;
import javax.swing.JPanel;
import java.awt.*;

import static main.Game.GAME_HEIGHT;
import static main.Game.GAME_WIDTH;

public class GamePanel extends JPanel {
    private Mouseinputs mouseinputs;
    private Game game;

    private void setPanelSize() {
        Dimension size = new Dimension(1024, 512); // NU GAME_WIDTH
        setPreferredSize(size);
    }

    public GamePanel(Game game) {
        mouseinputs = new Mouseinputs(this);
        this.game = game;
        setPanelSize();
        addKeyListener(new KeybordInputs(this));
        addMouseListener(mouseinputs);
        addMouseMotionListener(mouseinputs);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        game.render( g);
    }

    public Game getGame(){
        return game;
    }
}

