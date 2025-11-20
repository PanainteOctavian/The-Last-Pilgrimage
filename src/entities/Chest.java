package entities;

import java.awt.*;
import java.awt.image.BufferedImage;
import utilz.LoadSave;

public class Chest {
    private float x, y;
    private int width, height;
    private Rectangle hitbox;
    private boolean isOpened = false;
    private int interactionRadius = 50; // Radius for player interaction
    private int healAmount = 30; // Amount of health to restore

    // Visual properties
    private BufferedImage closedChestImage;
    private BufferedImage openedChestImage;
    private boolean hasLoadedImages = false;

    // Animation for opened state
    private boolean showOpenEffect = false;
    private int openEffectTick = 0;
    private int openEffectDuration = 60; // 1 second at 60 FPS

    public Chest(float x, float y) {
        this.x = x;
        this.y = y;
        this.width = 32; // Standard chest size
        this.height = 32;
        this.hitbox = new Rectangle((int)x, (int)y, width, height);
        loadImages();
    }

    private void loadImages() {
        try {
            // You'll need to add chest sprites to your resources
            // For now, we'll create simple colored rectangles as placeholders
            closedChestImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = closedChestImage.createGraphics();
            g2d.setColor(new Color(139, 69, 19)); // Brown color for chest
            g2d.fillRect(0, 0, width, height);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(0, 0, width-1, height-1);
            // Draw a simple lock symbol
            g2d.setColor(Color.YELLOW);
            g2d.fillOval(width/2-3, height/2-3, 6, 6);
            g2d.dispose();

            openedChestImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            g2d = openedChestImage.createGraphics();
            g2d.setColor(new Color(139, 69, 19)); // Brown color for chest
            g2d.fillRect(0, 0, width, height);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(0, 0, width-1, height-1);
            // Draw opened chest indication
            g2d.setColor(Color.GREEN);
            g2d.fillOval(width/2-4, height/2-4, 8, 8);
            g2d.dispose();

            hasLoadedImages = true;
        } catch (Exception e) {
            System.err.println("Failed to load chest images: " + e.getMessage());
        }
    }

    public void update() {
        if (showOpenEffect) {
            openEffectTick++;
            if (openEffectTick >= openEffectDuration) {
                showOpenEffect = false;
                openEffectTick = 0;
            }
        }
    }

    public void draw(Graphics2D g, int cameraOffsetX, int cameraOffsetY) {
        if (!hasLoadedImages) return;

        int drawX = (int)x - cameraOffsetX;
        int drawY = (int)y - cameraOffsetY;

        // Draw the chest
        BufferedImage currentImage = isOpened ? openedChestImage : closedChestImage;
        g.drawImage(currentImage, drawX, drawY, width, height, null);

        // Draw open effect if active
        if (showOpenEffect) {
            g.setColor(new Color(255, 215, 0, 150)); // Golden glow
            int glowSize = (int)(20 * (1.0f - (float)openEffectTick / openEffectDuration));
            g.fillOval(drawX - glowSize/2 + width/2, drawY - glowSize/2 + height/2, glowSize, glowSize);
        }

        // Debug: Draw interaction radius (remove in final version)
        if (!isOpened) {
            g.setColor(new Color(0, 255, 0, 50));
            g.drawOval(drawX - interactionRadius + width/2, drawY - interactionRadius + height/2,
                    interactionRadius * 2, interactionRadius * 2);
        }
    }

    public boolean canInteract(Player player) {
        if (isOpened) return false;
        if (player.getHealth() >= player.getMaxHealth()) return false; // Player at full health

        float playerCenterX = player.getWorldX() + player.getHitbox().width / 2;
        float playerCenterY = player.getWorldY() + player.getHitbox().height / 2;
        float chestCenterX = x + width / 2;
        float chestCenterY = y + height / 2;

        float distance = (float)Math.sqrt(Math.pow(playerCenterX - chestCenterX, 2) +
                Math.pow(playerCenterY - chestCenterY, 2));

        return distance <= interactionRadius;
    }

    public boolean interact(Player player) {
        if (!canInteract(player)) return false;

        // Heal the player
        int currentHealth = player.getHealth();
        int newHealth = Math.min(player.getMaxHealth(), currentHealth + healAmount);
        player.setHealth(newHealth);

        // Mark chest as opened
        isOpened = true;
        showOpenEffect = true;
        openEffectTick = 0;

        System.out.println("Chest opened! Player healed from " + currentHealth + " to " + newHealth + " HP");
        return true;
    }

    // Getters
    public float getX() { return x; }
    public float getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public Rectangle getHitbox() { return hitbox; }
    public boolean isOpened() { return isOpened; }
    public int getInteractionRadius() { return interactionRadius; }

    // Setters for customization
    public void setHealAmount(int healAmount) {
        this.healAmount = Math.max(1, healAmount);
    }

    public void setInteractionRadius(int radius) {
        this.interactionRadius = Math.max(10, radius);
    }

    // For save/load functionality
    public void setOpened(boolean opened) {
        this.isOpened = opened;
    }
}