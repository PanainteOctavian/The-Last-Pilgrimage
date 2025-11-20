package entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

public abstract class Entity {
    protected float x, y;
    protected int width, height;
    protected Rectangle2D.Float hitbox;
    protected boolean isDead = false;
    protected int health = 100;
    protected int maxHealth = 100;

    public Entity(float x, float y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    protected void drawHitbox(Graphics g) {

        g.setColor(Color.PINK);
        g.drawRect((int) hitbox.x, (int) hitbox.y, (int) hitbox.width, (int) hitbox.height);
    }

    protected void initHitbox(float x, float y, float width, float height) {
        hitbox = new Rectangle2D.Float(x, y, width, height);
    }

    public Rectangle2D.Float getHitbox() {
        return hitbox;
    }

    public float getWorldX() {
        return (int) hitbox.x;
    }

    public int getWorldY() {
        return (int) hitbox.y;
    }

    public boolean isDead() {
        return isDead || health <= 0;
    }

    public void setDead(boolean dead) {
        this.isDead = dead;
    }

    public void takeDamage(int damage) {
        this.health -= damage;
        if (this.health <= 0) {
            this.health = 0;
            this.isDead = true;
            onDeath();
        }
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
        if (this.health <= 0) {
            this.health = 0;
            this.isDead = true;
            onDeath();
        }
    }

    public int getMaxHealth() {
        return maxHealth;
    }


    protected void onDeath() {
        System.out.println("Entity died at position: " + hitbox.x + ", " + hitbox.y);
    }

    public void reset() {
        this.isDead = false;
        this.health = maxHealth;
    }
}