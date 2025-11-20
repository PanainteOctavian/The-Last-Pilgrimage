package levels;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.AlphaComposite;
import java.awt.Font;

public class LevelTransition {
    // Transition states
    public static final int NOT_TRANSITIONING = 0;
    public static final int FADE_OUT = 1;
    public static final int FADE_IN = 2;

    private int transitionState = NOT_TRANSITIONING;
    private float alpha = 0.0f; // Transparency level for fade effect
    private int targetLevel;
    private LevelManager levelManager;
    private boolean levelChanged = false; // Flag to track if level has been changed during this transition

    // Transition speed (lower = slower)
    private float fadeSpeed = 0.02f; // Reduced for smoother transition

    // Delay timer after fade completes
    private int delayCounter = 0;
    private final int TRANSITION_DELAY = 60; // Increased frames to wait after fade out completes

    public LevelTransition(LevelManager levelManager) {
        this.levelManager = levelManager;
    }

    public void startTransition(int targetLevel) {
        if (transitionState == NOT_TRANSITIONING) {
            this.targetLevel = targetLevel;
            transitionState = FADE_OUT;
            alpha = 0.0f;
            delayCounter = 0;
            levelChanged = false;
        }
    }

    public void update() {
        if (transitionState == FADE_OUT) {
            alpha += fadeSpeed;

            if (alpha >= 1.0f) {
                alpha = 1.0f;
                delayCounter++;

                if (delayCounter >= TRANSITION_DELAY) {
                    if (!levelChanged) {
                        levelManager.setLevel(targetLevel - 1);
                        levelChanged = true;
                    }
                    transitionState = FADE_IN;
                    delayCounter = 0;
                }
            }
        } else if (transitionState == FADE_IN) {
            alpha -= fadeSpeed;
            if (alpha <= 0.0f) {
                alpha = 0.0f;
                transitionState = NOT_TRANSITIONING;
            }
        }
    }

    public void draw(Graphics g, int width, int height) {
        if (transitionState != NOT_TRANSITIONING) {
            Graphics2D g2d = (Graphics2D) g;
            AlphaComposite originalComposite = (AlphaComposite) g2d.getComposite();
            
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

            // Draw a black rectangle covering the screen with current alpha
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, width, height);
            
            if (alpha > 0.3f) {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.min(1.0f, alpha + 0.3f)));
                g2d.setColor(Color.WHITE);
                g2d.setFont(g2d.getFont().deriveFont(24.0f));
                String message = "Se încarcă nivelul " + targetLevel + "...";
                int textWidth = g2d.getFontMetrics().stringWidth(message);
                g2d.drawString(message, width/2 - textWidth/2, height/2);
            }
            g2d.setComposite(originalComposite);
        }
    }

    private String getStateName() {
        switch(transitionState) {
            case FADE_OUT: return "FADE_OUT";
            case FADE_IN: return "FADE_IN";
            default: return "NOT_TRANSITIONING";
        }
    }

    public boolean isTransitioning() {
        return transitionState != NOT_TRANSITIONING;
    }

    public int getTargetLevel() {
        return targetLevel;
    }

    public boolean hasLevelChanged() {
        return levelChanged;
    }
}