package entities;

import gamestates.Playing;
import utilz.Camera;
import utilz.LoadSave;
import BazaDeDate.DataBase;
import BazaDeDate.DataBase.EnemyData;

import static utilz.Constants.EnemyConstants.GetSpriteAmount;
import static utilz.Constants.EnemyConstants.*;
import static utilz.Constants.PlayerConstants.DEATH;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EnemyManager {
    private Playing playing;
    private BufferedImage[][] knightArr;
    private BufferedImage[][] archerArr;
    private ArrayList<Enemy> enemies = new ArrayList<>();
    private EnemyFactory enemyFactory;

    private static class EnemySpawn {
        int enemyType;
        float x;
        float y;

        public EnemySpawn(int enemyType, float x, float y) {
            this.enemyType = enemyType;
            this.x = x;
            this.y = y;
        }
    }

    private ArrayList<EnemySpawn> level1Spawns = new ArrayList<>();
    private ArrayList<EnemySpawn> level2Spawns = new ArrayList<>();
    private ArrayList<EnemySpawn> level3Spawns = new ArrayList<>();

    public EnemyManager(Playing playing) {
        this.playing = playing;
        this.enemyFactory = new EnemyFactory(playing.getPlayer());
        loadEnemySprites();
        initializeEnemySpawns();
        loadEnemiesForCurrentLevel();
        registerEnemiesAsObservers();
    }

    private void registerEnemiesAsObservers() {
        Player player = playing.getPlayer();
        for (Enemy enemy : enemies) {
            player.addHealthObserver(enemy);
        }
        if (player.getHealth() <= 30) {
            System.out.println("Player health is low on load - setting enemies to charging mode");
            for (Enemy enemy : enemies) {
                enemy.onPlayerHealthLow();
            }
        }
    }

    private void initializeEnemySpawns() {

        // Level 1 spawns
        level1Spawns.add(new EnemySpawn(KNIGHT, 2000, 240)); // inceput
        level1Spawns.add(new EnemySpawn(KNIGHT, 2500, 200)); // sus
        level1Spawns.add(new EnemySpawn(KNIGHT, 2500, 310)); // mij
        level1Spawns.add(new EnemySpawn(KNIGHT, 2700, 300)); // mij
        level1Spawns.add(new EnemySpawn(KNIGHT, 2480, 350)); // jos
        level1Spawns.add(new EnemySpawn(KNIGHT, 3200, 250)); // final
        //level1Spawns.add(new EnemySpawn(ARCHER, 2000, 250)); // pe platforme

        // Level 2 spawns (add your level 2 enemy positions here)
        level2Spawns.add(new EnemySpawn(KNIGHT, 2376, 234)); // inceput
        level2Spawns.add(new EnemySpawn(KNIGHT, 2572, 74)); // mij
        level2Spawns.add(new EnemySpawn(KNIGHT, 2960, 106)); // sus
        level2Spawns.add(new EnemySpawn(KNIGHT, 2892, 266)); // jos
        level2Spawns.add(new EnemySpawn(KNIGHT, 3672, 250)); // mini boss
        //level2Spawns.add(new EnemySpawn(ARCHER, 2000, 250)); // pe platforme

        // Level 3 spawns (add your level 3 enemy positions here)
        // etaj 1
        level3Spawns.add(new EnemySpawn(KNIGHT, 1238, 100)); // inceput
        level3Spawns.add(new EnemySpawn(KNIGHT, 2690,138));  // mij
        level3Spawns.add(new EnemySpawn(KNIGHT, 3100,106));  // final
        level3Spawns.add(new EnemySpawn(KNIGHT, 3300,106));  // final
        level3Spawns.add(new EnemySpawn(KNIGHT, 3500,106));  // final
        //level3Spawns.add(new EnemySpawn(ARCHER, 2000, 250)); // pe platforme

        // etaj 2
        level3Spawns.add(new EnemySpawn(KNIGHT, 2544, 506)); // inceput
        level3Spawns.add(new EnemySpawn(KNIGHT, 2900, 506)); // inceput
        level3Spawns.add(new EnemySpawn(KNIGHT, 1800, 540)); // jos
        level3Spawns.add(new EnemySpawn(KNIGHT, 2000, 540)); // jos
        level3Spawns.add(new EnemySpawn(KNIGHT, 800, 410)); // sus
        level3Spawns.add(new EnemySpawn(KNIGHT, 2000, 410)); // sus
        //level3Spawns.add(new EnemySpawn(ARCHER, 2000, 250)); // pe platforme
    }

    private ArrayList<EnemySpawn> getSpawnsForLevel(int level) {
        switch (level) {
            case 1: return level1Spawns;
            case 2: return level2Spawns;
            case 3: return level3Spawns;
            default: return new ArrayList<>();
        }
    }

    public void loadEnemiesForCurrentLevel() {
        int currentLevel = playing.getCurrentLevel();
        loadEnemiesForLevel(currentLevel);
    }

    public void loadEnemiesForLevel(int level) {
        enemies.clear();

        ArrayList<EnemySpawn> spawns = getSpawnsForLevel(level);
        if (spawns.isEmpty()) {
            System.out.println("No enemy spawns defined for level " + level);
            return;
        }

        DataBase db = playing.getGame().getDataBase();
        List<EnemyData> savedStates = db.loadEnemyStates(level);

        System.out.println("Loading enemies for level " + level + ". Spawns: " + spawns.size() + ", Saved states: " + savedStates.size());

        for (EnemySpawn spawn : spawns) {
            // Check if this enemy was saved as dead
            boolean isDead = false;
            for (EnemyData savedState : savedStates) {
                if (savedState.enemyType == spawn.enemyType &&
                        Math.abs(savedState.spawnX - spawn.x) < 1.0f &&
                        Math.abs(savedState.spawnY - spawn.y) < 1.0f) {
                    isDead = savedState.isDead;
                    break;
                }
            }

            // Only spawn the enemy if it's not dead
            if (!isDead) {
                Enemy enemy = enemyFactory.createEnemy(spawn.enemyType, spawn.x, spawn.y);
                enemies.add(enemy);
                System.out.println("Spawned enemy type " + spawn.enemyType + " at (" + spawn.x + ", " + spawn.y + ")");
            } else {
                System.out.println("Enemy type " + spawn.enemyType + " at (" + spawn.x + ", " + spawn.y + ") remains dead");
            }
        }
    }

    public void update(int[][] lvlData, Player player) {
        Iterator<Enemy> it = enemies.iterator();
        while (it.hasNext()) {
            Enemy e = it.next();

            // Update the enemy
            e.update(lvlData, player);

            if (e instanceof Knight) {
                Knight knight = (Knight) e;
                if (knight.isDead() && knight.getEnemyState() == DEATH &&
                        knight.getAniIndex() == GetSpriteAmount(KNIGHT, DEATH) - 1) {

                    saveEnemyDeathState(knight);
                    player.setScore(player.getScore() + 100);
                    it.remove();
                    continue;
                }
            }
            // Handle death and removal for Archers
            else if (e instanceof Archer) {
                Archer archer = (Archer) e;
                if (archer.isDead() && archer.getEnemyState() == DEATH &&
                        archer.getAniIndex() == GetSpriteAmount(ARCHER, DEATH) - 1) {

                    saveEnemyDeathState(archer);
                    player.setScore(player.getScore() + 75); // Different score for archers
                    it.remove();
                    continue;
                }
            }
        }
    }

    private void saveEnemyDeathState(Enemy enemy) {
        DataBase db = playing.getGame().getDataBase();
        int currentLevel = playing.getCurrentLevel();

        // Find the original spawn position for this enemy
        ArrayList<EnemySpawn> spawns = getSpawnsForLevel(currentLevel);
        EnemySpawn closestSpawn = getEnemySpawn(enemy, spawns);

        if (closestSpawn != null) {
            db.saveEnemyState(currentLevel, closestSpawn.enemyType, closestSpawn.x, closestSpawn.y, true);
            String enemyTypeName = (enemy instanceof Knight) ? "Knight" : "Archer";
            System.out.println("Saved death state for " + enemyTypeName + " at (" + closestSpawn.x + ", " + closestSpawn.y + ")");
        }
    }

    private static EnemySpawn getEnemySpawn(Enemy enemy, ArrayList<EnemySpawn> spawns) {
        float enemyX = enemy.getHitbox().x;
        float enemyY = enemy.getHitbox().y;

        // Find the closest spawn point (in case the enemy moved)
        EnemySpawn closestSpawn = null;
        float minDistance = Float.MAX_VALUE;

        for (EnemySpawn spawn : spawns) {
            // Only consider spawns of the same enemy type
            if (spawn.enemyType == enemy.enemyType) {
                float distance = (float)Math.sqrt(Math.pow(spawn.x - enemyX, 2) + Math.pow(spawn.y - enemyY, 2));
                if (distance < minDistance) {
                    minDistance = distance;
                    closestSpawn = spawn;
                }
            }
        }
        return closestSpawn;
    }

    public void saveAllEnemyStates() {
        DataBase db = playing.getGame().getDataBase();
        int currentLevel = playing.getCurrentLevel();
        ArrayList<EnemySpawn> spawns = getSpawnsForLevel(currentLevel);

        for (EnemySpawn spawn : spawns) {
            boolean isAlive = false;

            for (Enemy enemy : enemies) {
                // Check if enemy type matches and is roughly at spawn position
                if (enemy.enemyType == spawn.enemyType) {
                    float enemyX = enemy.getHitbox().x;
                    float enemyY = enemy.getHitbox().y;

                    if (Math.abs(spawn.x - enemyX) < 100 && Math.abs(spawn.y - enemyY) < 100) {
                        if (enemy instanceof Knight) {
                            isAlive = !((Knight)enemy).isDead();
                        } else if (enemy instanceof Archer) {
                            isAlive = !((Archer)enemy).isDead();
                        }
                        break;
                    }
                }
            }

            // Save the state (false = dead, true would be alive but we save the inverse)
            db.saveEnemyState(currentLevel, spawn.enemyType, spawn.x, spawn.y, !isAlive);
        }

        System.out.println("Saved states for " + spawns.size() + " enemies in level " + currentLevel);
    }

    private void loadEnemySprites() {
        BufferedImage knightSprite = LoadSave.GetSpriteAtlas(LoadSave.KNIGHTSPRITE);
        BufferedImage archerSprite = LoadSave.GetSpriteAtlas(LoadSave.Enemy_Archer);

        // Load Knight sprites
        knightArr = new BufferedImage[KNIGHT_ANIMATION_STATES][];
        for (int i = 0; i < knightArr.length; i++) {
            int frameCount = GetSpriteAmount(KNIGHT, i);
            knightArr[i] = new BufferedImage[frameCount];
            for (int j = 0; j < frameCount; j++) {
                knightArr[i][j] = knightSprite.getSubimage(
                        j * KNIGHT_WIDTH_DEFAULT,
                        i * KNIGHT_HEIGHT_DEFAULT,
                        KNIGHT_WIDTH_DEFAULT,
                        KNIGHT_HEIGHT_DEFAULT
                );
            }
        }

        // Load Archer sprites
        archerArr = new BufferedImage[ARCHER_ANIMATION_STATES][];
        for (int i = 0; i < archerArr.length; i++) {
            int frameCount = GetSpriteAmount(ARCHER, i);
            archerArr[i] = new BufferedImage[frameCount];
            for (int j = 0; j < frameCount; j++) {
                archerArr[i][j] = archerSprite.getSubimage(
                        j * ARCHER_WIDTH_DEFAULT,
                        i * ARCHER_HEIGHT_DEFAULT,
                        ARCHER_WIDTH_DEFAULT,
                        ARCHER_HEIGHT_DEFAULT
                );
            }
        }
    }

    public void draw(Graphics g, Camera camera) {
        for (Enemy e : enemies) {
            if (isEnemyVisible(e, camera)) {
                if (e instanceof Knight) {
                    drawKnight(g, (Knight)e, camera);
                } else if (e instanceof Archer) {
                    drawArcher(g, (Archer)e, camera);
                }
            }
        }
    }

    private boolean isEnemyVisible(Enemy e, Camera camera) {
        int enemyWidth = (e instanceof Knight) ? KNIGHT_WIDTH : ARCHER_WIDTH;
        return e.getHitbox().x + enemyWidth > camera.getxOffset() &&
                e.getHitbox().x < camera.getxOffset() + 1024;
    }

    private void drawKnight(Graphics g, Knight knight, Camera camera) {
        BufferedImage image = knightArr[knight.getEnemyState()][knight.getAniIndex()];
        if (knight.isHit()) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            g2d.drawImage(image,
                    (int)(knight.getHitbox().x - ENEMY_HITBOX_X_OFFSET - camera.getxOffset()),
                    (int)(knight.getHitbox().y - ENEMY_HITBOX_Y_OFFSET - camera.getyOffset()),
                    KNIGHT_DRAW_WIDTH, KNIGHT_DRAW_HEIGHT, null);
            g2d.dispose();
        } else {
            g.drawImage(image,
                    (int)(knight.getHitbox().x - ENEMY_HITBOX_X_OFFSET - camera.getxOffset()),
                    (int)(knight.getHitbox().y - ENEMY_HITBOX_Y_OFFSET - camera.getyOffset()),
                    KNIGHT_DRAW_WIDTH, KNIGHT_DRAW_HEIGHT, null);
        }

        if (!knight.isDead()) {
            drawHealthBar(g, knight, camera);
        }

    }

    private void drawArcher(Graphics g, Archer archer, Camera camera) {
        BufferedImage image = archerArr[archer.getEnemyState()][archer.getAniIndex()];

            g.drawImage(image,
                    (int)(archer.getHitbox().x - ARCHER_HITBOX_X_OFFSET - camera.getxOffset()),
                    (int)(archer.getHitbox().y - ARCHER_HITBOX_Y_OFFSET - camera.getyOffset()),
                    ARCHER_WIDTH, ARCHER_HEIGHT, null);

        // Draw aiming indicator if archer is aiming
        if (archer.isAiming()) {
            drawAimingIndicator(g, archer, camera);
        }

        if (!archer.isDead()) {
            drawHealthBar(g, archer, camera);
        }

        // Debug: draw hitbox
        archer.drawHitbox(g, camera);
    }

    private void drawAimingIndicator(Graphics g, Archer archer, Camera camera) {

        g.setColor(Color.RED);
        int indicatorX = (int)(archer.getHitbox().x + archer.getHitbox().width/2 - camera.getxOffset());
        int indicatorY = (int)(archer.getHitbox().y - 20 - camera.getyOffset());
        g.fillOval(indicatorX - 3, indicatorY - 3, 6, 6);

        g.drawLine(indicatorX - 8, indicatorY, indicatorX + 8, indicatorY);
        g.drawLine(indicatorX, indicatorY - 8, indicatorX, indicatorY + 8);
    }

    private void drawHealthBar(Graphics g, Enemy e, Camera camera) {
        int barWidth = 40;
        int barHeight = 5;
        int barX = (int)(e.getHitbox().x - camera.getxOffset() + (e.getHitbox().width - barWidth) / 2);
        int barY = (int)(e.getHitbox().y - camera.getyOffset() - 10);

        // Background (red)
        g.setColor(Color.RED);
        g.fillRect(barX, barY, barWidth, barHeight);

        // Health (green)
        int healthWidth = (int)((e.getHealth() / (float)e.getMaxHealth()) * barWidth);
        g.setColor(Color.GREEN);
        g.fillRect(barX, barY, healthWidth, barHeight);

        // Border (black)
        g.setColor(Color.BLACK);
        g.drawRect(barX, barY, barWidth, barHeight);
    }

    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }


}