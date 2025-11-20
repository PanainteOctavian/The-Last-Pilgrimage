package entities;

import main.Game;
import entities.Player;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SpikeManager {
    private List<Spike> spikes;
    private Game game;

    public SpikeManager(Game game) {
        this.game = game;
        this.spikes = new ArrayList<>();
    }
    public void loadSpikesForLevel(int level) {
        spikes.clear();
        switch (level) {
            case 1:
                addSpike(560, 600, 64, 32);
                addSpike(864, 600, 64, 32);
                addSpike(1541, 600, 64, 32);
                break;

            case 2:
                addSpike(2644, 490, 30, 32);
                addSpike(3004, 490, 100, 32);
                addSpike(3214, 490, 40, 32);
                addSpike(3354, 490, 60, 32);
                break;

            case 3:
                addSpike(160, 250, 144, 32);
                addSpike(384, 266, 170, 32);
                addSpike(608, 266, 600, 32);
                addSpike(1904, 170, 60, 32);
                addSpike(2209, 234, 380, 32);
                addSpike(2832, 271, 120, 32);
                addSpike(3200, 570, 400, 32);
                break;

            default:
                break;
        }

        System.out.println("Loaded " + spikes.size() + " spikes for level " + level);

        for (int i = 0; i < spikes.size(); i++) {
            Spike spike = spikes.get(i);
            System.out.println("Spike " + i + ": x=" + spike.getX() + ", y=" + spike.getY() +
                    ", width=" + spike.getWidth() + ", height=" + spike.getHeight());
        }
    }

    private void addSpike(int x, int y, int width, int height) {
        spikes.add(new Spike(x, y, width, height));
    }

    public void update(Player player) {
        if (player.isDead() || player.isInDeathAnimation()) {
            return;
        }

        for (Spike spike : spikes) {
            if (spike.checkCollision(player.getHitbox())) {
                System.out.println("Player hit spikes! Instant death.");
                player.setHealth(0);
                break;
            }
        }
    }



}