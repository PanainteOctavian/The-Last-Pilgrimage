package gamestates;

import UI.MenuButton;
import UI.OptionsButtons;
import main.Game;
import utilz.LoadSave;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.geom.Rectangle2D;

public class Options extends State implements Statemethods {

    private OptionsButtons[] buttons = new OptionsButtons[2];
    private BufferedImage background;
    private Gamestate previousState = Gamestate.MAIN_MENU; // Setez default la MAIN_MENU

    // Volume slider components
    private Rectangle volumeSlider;
    private Rectangle volumeButton;
    private boolean isDraggingVolume = false;
    private float volume = 0.5f; // Volume between 0.0 and 1.0
    private int sliderWidth = 200;
    private int sliderHeight = 20;
    private int buttonSize = 20;

    public Options(Game game) {
        super(game);
        background = LoadSave.GetMenuBackground();
        loadButtons();
        initVolumeSlider();
    }

    // Method to set the previous state when entering options
    public void setPreviousState(Gamestate previousState) {
        this.previousState = previousState;
        System.out.println("Options: Previous state set to " + previousState); // Debug
    }

    // Method to get the appropriate back state
    private Gamestate getBackState() {
        System.out.println("Options: Getting back state, previousState = " + previousState); // Debug
        if (previousState == Gamestate.MAIN_MENU) {
            return Gamestate.MAIN_MENU;
        } else if (previousState == Gamestate.MENU) {
            return Gamestate.MENU;
        } else {
            // Dacă nu știm de unde am venit, returnăm la meniul principal
            return Gamestate.MAIN_MENU;
        }
    }

    private void loadButtons() {
        buttons[0] = new OptionsButtons(1024 / 2, (int) (110 * Game.SCALE), 0, Gamestate.PLAYING);
        buttons[1] = new OptionsButtons(1024 / 2, (int) (180 * Game.SCALE), 1, Gamestate.BACK);
    }

    private void initVolumeSlider() {
        int sliderX = (1024 - sliderWidth) / 2;  // Folosesc valorile directe
        int sliderY = 300;  // Simplificat fără scalare

        volumeSlider = new Rectangle(sliderX, sliderY, sliderWidth, sliderHeight);

        // Position volume button based on current volume
        int buttonX = sliderX + (int) (volume * (sliderWidth - buttonSize));
        volumeButton = new Rectangle(buttonX, sliderY, buttonSize, buttonSize);
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = Math.max(0.0f, Math.min(1.0f, volume));
        updateVolumeButtonPosition();
    }

    private void updateVolumeButtonPosition() {
        int buttonX = volumeSlider.x + (int) (volume * (sliderWidth - buttonSize));
        volumeButton.x = buttonX;
    }

    @Override
    public void update() {
        for (OptionsButtons mb : buttons)
            mb.update();
    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(background, 0, 0, 1024, 512, null);

        // Draw buttons
        for (OptionsButtons mb : buttons)
            mb.draw(g);

        // Draw volume slider
        drawVolumeSlider(g);

        // Draw buttons
        for (OptionsButtons mb : buttons)
            mb.draw(g);

        // Draw volume slider
        drawVolumeSlider(g);
    }

    private void drawVolumeSlider(Graphics g) {
        // Draw volume label
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        String volumeText = "Volume: " + (int) (volume * 100) + "%";
        FontMetrics fm = g.getFontMetrics();
        int textX = (1024 - fm.stringWidth(volumeText)) / 2;
        int textY = volumeSlider.y - 20;
        g.drawString(volumeText, textX, textY);

        g.setColor(Color.DARK_GRAY);
        g.fillRect(volumeSlider.x, volumeSlider.y, volumeSlider.width, volumeSlider.height);

        g.setColor(Color.GREEN);
        int fillWidth = (int) (volume * sliderWidth);
        g.fillRect(volumeSlider.x, volumeSlider.y, fillWidth, volumeSlider.height);

        g.setColor(Color.WHITE);
        g.drawRect(volumeSlider.x, volumeSlider.y, volumeSlider.width, volumeSlider.height);


        g.setColor(Color.WHITE);
        g.fillOval(volumeButton.x, volumeButton.y, volumeButton.width, volumeButton.height);
        g.setColor(Color.BLACK);
        g.drawOval(volumeButton.x, volumeButton.y, volumeButton.width, volumeButton.height);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (volumeSlider.contains(e.getPoint())) {
            isDraggingVolume = true;
            updateVolumeFromMouse(e.getX());
        }

        for (OptionsButtons mb : buttons) {
            if (isIn(e, mb)) {
                mb.setMousePressed(true);
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        isDraggingVolume = false;

        for (OptionsButtons mb : buttons) {
            if (isIn(e, mb)) {
                if (mb.isMousePressed()) {
                    if (mb.getState() == Gamestate.BACK) {
                        Gamestate backState = getBackState();
                        System.out.println("Options: Going back to " + backState); // Debug
                        Gamestate.state = backState;
                    } else {
                        mb.applyGameState();
                    }
                }
                break;
            }
        }
        resetButtons();
    }

    private void updateVolumeFromMouse(int mouseX) {
        int relativeX = mouseX - volumeSlider.x;
        float newVolume = (float) relativeX / sliderWidth;
        setVolume(newVolume);
    }

    private void resetButtons() {
        for (OptionsButtons mb : buttons) {
            mb.resetBools();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // Handle volume slider dragging in mouseMoved when mouse is pressed
        if (isDraggingVolume) {
            updateVolumeFromMouse(e.getX());
        }

        for (OptionsButtons mb : buttons) {
            mb.setMouseOver(false);
        }
        for (OptionsButtons mb : buttons) {
            if (isIn(e, mb)) {
                mb.setMouseOver(true);
                break;
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            Gamestate backState = getBackState();
            System.out.println("Options: ESC pressed, going back to " + backState); // Debug
            Gamestate.state = backState;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}