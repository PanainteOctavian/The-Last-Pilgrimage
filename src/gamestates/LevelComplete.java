package gamestates;

import UI.LevelTransitionButton;
import main.Game;
import utilz.LoadSave;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class LevelComplete extends State implements Statemethods {
    private Playing playing;
    private BufferedImage backgroundImg;
    private BufferedImage levelCompleteImg;
    private LevelTransitionButton nextLevelButton;
    private boolean initialized = false;

    public LevelComplete(Game game) {
        super(game);
        this.playing = game.getPlaying();
        loadImages();
        LoadButtons();
    }

    private void loadImages() {
        backgroundImg = LoadSave.GetSpriteAtlas(LoadSave.MENU_BACKGROUND);
        levelCompleteImg = LoadSave.GetSpriteAtlas(LoadSave.LEVEL_COMPLETE_IMG);
        initialized = true;
    }

    private void LoadButtons() {
        int buttonX = 1024 / 2;
        int levelCompleteY = 512 / 3 - levelCompleteImg.getHeight() / 2;
        int buttonY = levelCompleteY + levelCompleteImg.getHeight() + 120;
        nextLevelButton = new LevelTransitionButton(buttonX, buttonY, 0, Gamestate.PLAYING);
    }

    @Override
    public void draw(Graphics g) {
        try {
            if (!initialized) {
                loadImages();
                LoadButtons();
            }

            g.drawImage(backgroundImg, 0, 0, 1024, 512, null);
            int levelCompleteX = 1024 / 2 - levelCompleteImg.getWidth() / 2;
            int levelCompleteY = 512 / 3 - levelCompleteImg.getHeight() / 2;
            g.drawImage(levelCompleteImg, levelCompleteX, levelCompleteY, null);
            if (nextLevelButton != null) {
                nextLevelButton.draw(g);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update() {
        if (nextLevelButton != null) {
            nextLevelButton.update();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (nextLevelButton != null) {
            nextLevelButton.setMouseOver(false);
            if (isIn(e, nextLevelButton)) {
                nextLevelButton.setMouseOver(true);
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (nextLevelButton != null && isIn(e, nextLevelButton))
            nextLevelButton.setMousePressed(true);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (nextLevelButton != null && isIn(e, nextLevelButton) && nextLevelButton.isMousePressed()) {
            int nextLevel = playing.getCurrentLevel() + 1;
            if (nextLevel <= playing.getLevelManager().getLevelCount()) {
                playing.levelCompleted = false;
                playing.initiateTransition(nextLevel);
                Gamestate.state = Gamestate.PLAYING;
            } else {
                Gamestate.state = Gamestate.MAIN_MENU;
            }
        }

        if (nextLevelButton != null) {
            nextLevelButton.resetBools();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            Gamestate.state = Gamestate.PLAYING;
        } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            int nextLevel = playing.getCurrentLevel() + 1;
            if (nextLevel <= playing.getLevelManager().getLevelCount()) {
                playing.levelCompleted = false;
                playing.initiateTransition(nextLevel);
                Gamestate.state = Gamestate.PLAYING;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    private boolean isIn(MouseEvent e, LevelTransitionButton b) {
        return b.getBounds().contains(e.getX(), e.getY());
    }
}