package gamestates;

import UI.DeathButtons;
import main.Game;
import utilz.LoadSave;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class Death extends State implements Statemethods {
    private Playing playing;
    private BufferedImage backgroundImg;
    private BufferedImage deathImg;
    private DeathButtons retryButton;
    private DeathButtons mainMenuButton;
    private boolean initialized = false;

    public Death(Game game) {
        super(game);
        this.playing = game.getPlaying();
        loadImages();
        loadButtons();
    }

    private void loadImages() {
        backgroundImg = LoadSave.GetSpriteAtlas(LoadSave.MENU_BACKGROUND);
        deathImg = LoadSave.GetSpriteAtlas(LoadSave.LEVEL_DEATH);
        initialized = true;
    }

    private void loadButtons() {
        int buttonX = 1024 / 2;
        int deathTextY = 512 / 3 - deathImg.getHeight() / 2;
        int retryButtonY = deathTextY + deathImg.getHeight() + 50;
        int mainMenuButtonY = retryButtonY + 80;
        retryButton = new DeathButtons(buttonX, retryButtonY, 2, Gamestate.PLAYING);
        mainMenuButton = new DeathButtons(buttonX, mainMenuButtonY, 0, Gamestate.MAIN_MENU);
    }

    @Override
    public void draw(Graphics g) {
        try {
            if (!initialized) {
                loadImages();
                loadButtons();
            }
            g.drawImage(backgroundImg, 0, 0, 1024, 512, null);
            int deathImgX = 1024 / 2 - deathImg.getWidth() / 2;
            int deathImgY = 512 / 3 - deathImg.getHeight() / 2;
            g.drawImage(deathImg, deathImgX, deathImgY, null);
            retryButton.draw(g);
            mainMenuButton.draw(g);

            // Draw score
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            String scoreText = "Score: " + playing.getPlayer().getScore();
            int textWidth = g.getFontMetrics().stringWidth(scoreText);
            g.drawString(scoreText, 1024/2 - textWidth/2, deathImgY + deathImg.getHeight() + 20);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update() {
        retryButton.update();
        mainMenuButton.update();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        retryButton.setMouseOver(false);
        mainMenuButton.setMouseOver(false);

        if (isIn(e, retryButton)) {
            retryButton.setMouseOver(true);
        } else if (isIn(e, mainMenuButton)) {
            mainMenuButton.setMouseOver(true);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (isIn(e, retryButton))
            retryButton.setMousePressed(true);
        else if (isIn(e, mainMenuButton))
            mainMenuButton.setMousePressed(true);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (isIn(e, retryButton) && retryButton.isMousePressed()) {
            playing.resetAfterDeath();
            Gamestate.state = Gamestate.PLAYING;
        } else if (isIn(e, mainMenuButton) && mainMenuButton.isMousePressed()) {
            playing.resetAfterDeath();
            Gamestate.state = Gamestate.MAIN_MENU;
        }

        retryButton.resetBools();
        mainMenuButton.resetBools();
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    private boolean isIn(MouseEvent e, DeathButtons b) {
        return b.getBounds().contains(e.getX(), e.getY());
    }
}