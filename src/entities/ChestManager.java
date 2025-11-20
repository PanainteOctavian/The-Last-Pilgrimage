package entities;

import main.Game;
import gamestates.Playing;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ChestManager {
    private Game game;
    private Playing playing; // Store direct reference to Playing
    private List<Chest> chests;
    private Chest nearbyChest = null; // Chest that player can currently interact with

    public ChestManager(Game game) {
        this.game = game;
        this.chests = new ArrayList<>();
        // Don't try to get Playing reference here - it might not be initialized yet
    }

    // Set the Playing reference after it's fully initialized
    public void setPlaying(Playing playing) {
        this.playing = playing;
    }

    public void loadChestsForLevel(int levelNumber) {
        chests.clear();
        nearbyChest = null;

        // Define chest positions for each level
        switch (levelNumber) {
            case 1:
                addChest(3722, 351);
                break;
            case 2:
                // Add chests for level 2
                addChest(350, 320);
                addChest(900, 200);
                addChest(1400, 290);
                break;
            case 3:
                // Add chests for level 3
                addChest(500, 310);
                addChest(1000, 240);
                addChest(1500, 270);
                break;
            default:
                // Default chest placement
                addChest(400, 300);
                break;
        }

        // Load chest states from database
        loadChestStates();

        System.out.println("Loaded " + chests.size() + " chests for level " + levelNumber);
    }

    public void addChest(float x, float y) {
        Chest chest = new Chest(x, y);
        chests.add(chest);
    }

    public void addChest(float x, float y, int healAmount) {
        Chest chest = new Chest(x, y);
        chest.setHealAmount(healAmount);
        chests.add(chest);
    }

    public void update(Player player) {
        nearbyChest = null;

        for (Chest chest : chests) {
            chest.update();

            // Check if player can interact with this chest
            if (chest.canInteract(player)) {
                nearbyChest = chest;
                break; // Only one chest can be nearby at a time
            }
        }
    }

    public void draw(Graphics2D g, int cameraOffsetX, int cameraOffsetY) {
        for (Chest chest : chests) {
            chest.draw(g, cameraOffsetX, cameraOffsetY);
        }

        // Draw interaction prompt if player is near a chest
        if (nearbyChest != null) {
            drawInteractionPrompt(g, cameraOffsetX, cameraOffsetY);
        }
    }

    private void drawInteractionPrompt(Graphics2D g, int cameraOffsetX, int cameraOffsetY) {
        if (nearbyChest == null) return;

        // Calculate position above the chest
        int promptX = (int)(nearbyChest.getX() - cameraOffsetX + nearbyChest.getWidth()/2);
        int promptY = (int)(nearbyChest.getY() - cameraOffsetY - 10);

        // Draw background
        String text = "Press E to open";
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();

        g.setColor(new Color(0, 0, 0, 180));
        g.fillRoundRect(promptX - textWidth/2 - 5, promptY - textHeight,
                textWidth + 10, textHeight + 5, 5, 5);

        // Draw text
        g.setColor(Color.WHITE);
        g.drawString(text, promptX - textWidth/2, promptY - 5);
    }

    public void handleInteraction() {
        if (nearbyChest != null && playing != null) {
            Player player = playing.getPlayer();
            boolean wasInteracted = nearbyChest.interact(player);

            if (wasInteracted) {
                // Save the chest state to database
                saveChestState(nearbyChest);
                // Save the game to update player's health in database
                playing.saveGame();
                System.out.println("Chest interaction saved to database!");
            }

        }
    }

    private void saveChestState(Chest chest) {
        if (playing == null) {
            System.out.println("Cannot save chest state: Playing reference is null");
            return;
        }

        int currentLevel = playing.getCurrentLevel();
        game.getDataBase().saveChestState(currentLevel, chest.getX(), chest.getY(), chest.isOpened());
    }


    // Load chest states from database
    public void loadChestStates() {
        if (playing == null) {
            System.out.println("Cannot load chest states: Playing reference is null");
            return;
        }

        int currentLevel = playing.getCurrentLevel();
        var chestStates = game.getDataBase().loadChestStates(currentLevel);

        for (var chestState : chestStates) {
            for (Chest chest : chests) {
                if (Math.abs(chest.getX() - chestState.x) < 1.0f &&
                        Math.abs(chest.getY() - chestState.y) < 1.0f) {
                    chest.setOpened(chestState.isOpened);
                    break;
                }
            }
        }

        System.out.println("Loaded chest states for level " + currentLevel);
    }

    // Getters
    public List<Chest> getChests() {
        return chests;
    }

    public Chest getNearbyChest() {
        return nearbyChest;
    }

    public boolean hasNearbyChest() {
        return nearbyChest != null;
    }
}