package gamestates;

import entities.*;
import levels.LevelManager;
import levels.LevelTransition;
import main.Game;
import utilz.Camera;
import utilz.LoadSave;
import BazaDeDate.DataBase;
import BazaDeDate.DataBase.PlayerData;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import entities.ChestManager;

public class Playing extends State implements Statemethods {

    private Player player;
    private LevelManager levelManager;
    private BufferedImage background;
    private Camera camera;
    private boolean canJump = true;
    private int currentLevel = 1;
    private LevelComplete levelComplete;
    private LevelTransition levelTransition;
    public boolean levelCompleted = false;
    private boolean gameLoaded = false;
    private EnemyManager enemyManager;
    private NPCManager npcManager;
    private SpikeManager spikeManager;
    private ChestManager chestManager;


    private void initClasses() {
        levelManager = new LevelManager(game);
        player = new Player(200, 220, 25, 80);
        player.loadLvlData(levelManager.getCurrentLevel().getLvlData());
        int levelWidth = Game.TILES_IN_WIDTH * 16;
        int levelHeight = Game.TILES_IN_HEIGHT * 16;
        camera = new Camera(0, 0, levelWidth - 1024, levelHeight - 512, 1024, 512);
        levelTransition = new LevelTransition(levelManager);
        levelComplete = new LevelComplete(game);
        enemyManager = new EnemyManager(this);
        npcManager = new NPCManager(this);
        spikeManager = new SpikeManager(game);
        chestManager = new ChestManager(game);
        chestManager.setPlaying(this);
        spikeManager.loadSpikesForLevel(currentLevel);
        chestManager.loadChestsForLevel(currentLevel);
        npcManager.loadNPCsForLevel(currentLevel);
    }

    public Playing(Game game) {
        super(game);
        initClasses();
        background = LoadSave.GetBackground(levelManager.getCurrentLevelIndex());
    }

    public void saveGame() {
        DataBase db = game.getDataBase();
        int health = player.getHealth();
        float x = player.getWorldX();
        float y = player.getWorldY();
        int score = player.getScore();
        int stamina = player.getCurrentStamina();
        db.saveGame(health, x, y, currentLevel, score, stamina);
        enemyManager.saveAllEnemyStates();
        System.out.println("Joc salvat cu succes! Nivel: " + currentLevel + ", X: " + x + ", Y: " + y +
                ", Score: " + score + ", Health: " + health + ", Stamina: " + stamina);
    }

    public void loadGame() {
        DataBase db = game.getDataBase();
        PlayerData data = db.loadGame();

        if (data != null) {
            System.out.println("Încărcare joc: Nivel: " + data.level + ", X: " + data.x + ", Y: " + data.y +
                    ", Score: " + data.score + ", Health: " + data.health + ", Stamina: " + data.stamina);
            gameLoaded = true;
            player.resetDirBooleans();
            currentLevel = data.level;
            levelManager.setLevel(currentLevel - 1);
            player.loadLvlData(levelManager.getCurrentLevel().getLvlData());
            player.setPosition(data.x, data.y);
            player.setHealth(data.health);
            player.setScore(data.score);
            player.setStamina(data.stamina);

            background = LoadSave.GetBackground(levelManager.getCurrentLevelIndex());
            camera.centerOnEntity((int)player.getWorldX(), player.getWorldY(), 80, 120);
            enemyManager.loadEnemiesForLevel(currentLevel);
            npcManager.loadNPCsForLevel(currentLevel);
            spikeManager.loadSpikesForLevel(currentLevel);
            chestManager.loadChestsForLevel(currentLevel);

            Gamestate.state = Gamestate.PLAYING;
        } else {
            System.out.println("Nu s-a găsit nicio salvare. Se pornește un joc nou.");
            startNewGame();
        }
    }

    public void startNewGame() {
        DataBase db = game.getDataBase();
        PlayerData data = db.startNewGame();

        if (data != null) {
            player.resetDirBooleans();
            currentLevel = data.level;
            levelManager.setLevel(currentLevel - 1);
            player.loadLvlData(levelManager.getCurrentLevel().getLvlData());
            player.setPosition(data.x, data.y);
            player.setHealth(data.health);
            player.setScore(data.score);
            background = LoadSave.GetBackground(levelManager.getCurrentLevelIndex());
            camera.centerOnEntity((int)player.getWorldX(), player.getWorldY(), 80, 120);
            enemyManager.loadEnemiesForLevel(currentLevel);
            npcManager.loadNPCsForLevel(currentLevel);
            spikeManager.loadSpikesForLevel(currentLevel);
            chestManager.loadChestsForLevel(currentLevel);
            Gamestate.state = Gamestate.PLAYING;
        }
    }

    @Override
    public void update() {
        if (Gamestate.state == Gamestate.PLAYING) {
            if (gameLoaded) {
                gameLoaded = false;
                camera.centerOnEntity((int)player.getWorldX(), player.getWorldY(), 80, 120);
            }
            if (levelTransition.isTransitioning()) {
                levelTransition.update();
                if (!levelTransition.isTransitioning() && levelTransition.hasLevelChanged()) {
                    resetPlayerPositionForLevel(levelTransition.getTargetLevel());
                    player.loadLvlData(levelManager.getCurrentLevel().getLvlData());
                    enemyManager.loadEnemiesForLevel(currentLevel);
                    npcManager.loadNPCsForLevel(currentLevel);
                    chestManager.loadChestsForLevel(currentLevel);
                }
            }
            else if (levelCompleted) {
                Gamestate.state = Gamestate.LEVELCOMPLETE;
                saveGame();
            }
            else if (player.isDeathAnimationComplete()) {
                Gamestate.state = Gamestate.DEATH;
            }
            else {
                player.update();
                spikeManager.update(player);
                enemyManager.update(levelManager.getCurrentLevel().getLvlData(), player);
                npcManager.update();
                chestManager.update(player);
                checkPlayerEnemyInteractions();
                camera.centerOnEntity((int)player.getWorldX(), player.getWorldY(), 80, 120);
                checkLevelCompletion();
            }
        }
    }


    private void resetPlayerPositionForLevel(int level) {
        switch (level) {
            case 1:
                player.setPosition(200, 220);
                break;
            case 2:
                player.setPosition(200, 200);
                break;
            case 3:
                player.setPosition(30, 40);
                break;
            default:
                player.setPosition(200, 220);
                break;
        }
        player.resetDirBooleans();
    }

    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        int cameraOffsetX = camera.getxOffset();
        int cameraOffsetY = camera.getyOffset();
        g2d.translate(-cameraOffsetX, -cameraOffsetY);
        levelManager.draw(g2d);
        g2d.translate(cameraOffsetX, cameraOffsetY);

        g.drawImage(
                levelManager.getCurrentBackground(),
                -cameraOffsetX,
                -cameraOffsetY,
                levelManager.getCurrentBackground().getWidth(),
                levelManager.getCurrentBackground().getHeight(),
                null
        );

        g2d.translate(-cameraOffsetX, -cameraOffsetY);
        player.render(g2d);
        g2d.translate(cameraOffsetX, cameraOffsetY);
        enemyManager.draw(g2d, camera);
        npcManager.draw(g2d, cameraOffsetX, cameraOffsetY);
        drawLevelTransition(g);
        drawPlayerHealth(g);
        drawPlayerStamina(g);
        drawPlayerScore(g);
    }


    private void drawPlayerScore(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setTransform(new java.awt.geom.AffineTransform());
        int scoreX = 824;
        int scoreY = 40;
        Font scoreFont = new Font("Arial", Font.BOLD, 18);
        g2d.setFont(scoreFont);
        String scoreText = "Score: " + player.getScore();
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(scoreText);
        int textHeight = fm.getHeight();
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRoundRect(scoreX - 15, scoreY - textHeight + 5, textWidth + 30, textHeight + 10, 8, 8);
        g2d.setColor(Color.WHITE);
        g2d.drawRoundRect(scoreX - 15, scoreY - textHeight + 5, textWidth + 30, textHeight + 10, 8, 8);
        g2d.setColor(Color.YELLOW);
        g2d.drawString(scoreText, scoreX, scoreY);
        g2d.dispose();
    }

    private void drawPlayerHealth(Graphics g) {
        int healthBarWidth = 200;
        int healthBarHeight = 20;
        int healthBarX = 20;
        int healthBarY = 20;
        // Background of health bar
        g.setColor(Color.GRAY);
        g.fillRect(healthBarX, healthBarY, healthBarWidth, healthBarHeight);
        // Health amount
        int currentHealthWidth = (int)((player.getHealth() / 100.0) * healthBarWidth);
        g.setColor(Color.RED);
        g.fillRect(healthBarX, healthBarY, currentHealthWidth, healthBarHeight);
        // Border
        g.setColor(Color.BLACK);
        g.drawRect(healthBarX, healthBarY, healthBarWidth, healthBarHeight);
        // Text
        g.setColor(Color.WHITE);
        String healthText = "HP: " + player.getHealth() + "/100";
        g.drawString(healthText, healthBarX + 5, healthBarY + 15);
    }

    private void drawPlayerStamina(Graphics g) {
        int staminaBarWidth = 200;
        int staminaBarHeight = 15;
        int staminaBarX = 20;
        int staminaBarY = 50;

        g.setColor(Color.GRAY);
        g.fillRect(staminaBarX, staminaBarY, staminaBarWidth, staminaBarHeight);
        // Stamina amount
        int currentStaminaWidth = (int)((player.getCurrentStamina() / (float)player.getMaxStamina()) * staminaBarWidth);
        g.setColor(new Color(0, 150, 0));
        g.fillRect(staminaBarX, staminaBarY, currentStaminaWidth, staminaBarHeight);
        // Border
        g.setColor(Color.BLACK);
        g.drawRect(staminaBarX, staminaBarY, staminaBarWidth, staminaBarHeight);
        // Text
        g.setColor(Color.WHITE);
        String staminaText = "SP: " + player.getCurrentStamina() + "/100";
        g.drawString(staminaText, staminaBarX + 5, staminaBarY + 12);
    }

    private void drawLevelTransition(Graphics g) {
        if (levelTransition.isTransitioning()) {
            levelTransition.draw(g, Game.GAME_WIDTH, Game.GAME_HEIGHT);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (player.isDead() || player.isInDeathAnimation())
            return;
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                player.setJumping(true);
                break;
            case KeyEvent.VK_A:
                player.setLeft(true);
                break;
            case KeyEvent.VK_D:
                player.setRight(true);
                break;
            case KeyEvent.VK_S:
                player.setCrouching(true);
                break;
            case KeyEvent.VK_J:
                player.setAttacking1(true);
                break;
            case KeyEvent.VK_K:
                player.setAttacking2(true);
                break;
            case KeyEvent.VK_ESCAPE:
                Gamestate.state = Gamestate.MENU;
                break;
            case KeyEvent.VK_T:
                if (!player.isDead()) {
                    playerDied();
                }
                break;
            case KeyEvent.VK_F5:
                saveGame();
                break;
            case KeyEvent.VK_H:
                testDamagePlayer(10);
                break;
            case KeyEvent.VK_G:
                testHealPlayer(10);
                break;
            case KeyEvent.VK_E:
                npcManager.handleInteraction();
                chestManager.handleInteraction();
                break;
        }
    }

    private void checkPlayerEnemyInteractions() {
        if (player.isDead() || player.isInDeathAnimation()) {
            return;
        }

        ArrayList<Enemy> enemies = enemyManager.getEnemies();

        if (player.isAttacking1() || player.isAttacking2()) {
            Rectangle attackBox = player.getAttackBox();

            for (Enemy enemy : enemies) {
                if (enemy instanceof Knight) {
                    Knight knight = (Knight) enemy;
                    if (!knight.isDead() && attackBox.intersects(knight.getHitbox().getBounds())) {
                        int damage = player.isAttacking2() ? 20 : 10;
                        knight.takeDamage(damage);
                        break;
                    }
                }

                else if (enemy instanceof Archer) {
                    Archer archer = (Archer) enemy;
                    if (!archer.isDead() && attackBox.intersects(archer.getHitbox().getBounds())) {
                        int damage = player.isAttacking2() ? 20 : 10;
                        archer.takeDamage(damage);
                        break;
                    }
                }
            }
        }
    }

    // Test method to damage player
    private void testDamagePlayer(int amount) {
        if (!player.isDead() && !player.isInDeathAnimation()) {
            int currentHealth = player.getHealth();
            int newHealth = Math.max(0, currentHealth - amount);
            System.out.println("Test damage: " + currentHealth + " -> " + newHealth);
            player.setHealth(newHealth);
        }
    }

    private void testHealPlayer(int amount) {
        if (!player.isDead() && !player.isInDeathAnimation()) {
            int currentHealth = player.getHealth();
            int newHealth = Math.min(100, currentHealth + amount);
            System.out.println("Test heal: " + currentHealth + " -> " + newHealth);
            player.setHealth(newHealth);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (player.isDead() || player.isInDeathAnimation())
            return;

        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                player.setJumping(false);
                canJump = true;
                break;
            case KeyEvent.VK_A:
                player.setLeft(false);
                break;
            case KeyEvent.VK_D:
                player.setRight(false);
                break;
            case KeyEvent.VK_S:
                player.setCrouching(false);
                break;
            case KeyEvent.VK_J:
                player.setAttacking1(false);
                break;
            case KeyEvent.VK_K:
                player.setAttacking2(false);
                break;
        }
    }

    private void checkLevelCompletion() {
        if (currentLevel == 3) {
            float playerX = player.getWorldX();
            float playerY = player.getWorldY();
            if (Math.abs(playerX - 44) <= 20 && Math.abs(playerY - 527) <= 20 && !levelCompleted) {
                completedLevel();
            }
        } else {
            if (player.getWorldX() > 3799 && !levelCompleted) {
                completedLevel();
            }
        }
    }

    public void initiateTransition(int targetLevel) {
        if (!levelTransition.isTransitioning() && targetLevel != currentLevel) {
            saveGame();
            levelCompleted = false;
            currentLevel = targetLevel;
            levelTransition.startTransition(targetLevel);
        }
    }


    public void playerDied() {
        player.setHealth(0);
    }

    public void resetAfterDeath() {
        player.reset();
        resetPlayerPositionForLevel(currentLevel);
        player.loadLvlData(levelManager.getCurrentLevel().getLvlData());

        enemyManager.loadEnemiesForLevel(currentLevel);
        spikeManager.loadSpikesForLevel(currentLevel);
        npcManager.loadNPCsForLevel(currentLevel);
        chestManager.loadChestsForLevel(currentLevel);
    }

    public Player getPlayer() {
        return player;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (levelCompleted) {
            levelComplete.mousePressed(e);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (levelCompleted) {
            levelComplete.mouseReleased(e);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (levelCompleted) {
            levelComplete.mouseMoved(e);
        }
    }

    public void completedLevel() {
        System.out.println("Nivel completat! Schimbare la starea LevelComplete.");
        levelCompleted = true;
        Gamestate.state = Gamestate.LEVELCOMPLETE;
    }

    public LevelManager getLevelManager() {
        return levelManager;
    }

    public NPCManager getNPCManager() {
        return npcManager;
    }

    public ChestManager getChestManager() {return chestManager;}
}