package entities;

import main.Game;
import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Spike {
    private Rectangle2D.Float hitbox;
    private int x, y, width, height;
    private boolean active = true;

    public Spike(float x, float y, int width, int height) {
        this.x = (int)x;
        this.y = (int)y;
        this.width = width;
        this.height = height;
        this.hitbox = new Rectangle2D.Float(x, y, width, height);
    }

    public boolean checkCollision(Rectangle2D.Float playerHitbox) {
        return active && hitbox.intersects(playerHitbox);
    }


    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Rectangle2D.Float getHitbox() {
        return hitbox;
    }

    public boolean isActive() {
        return active;
    }
}