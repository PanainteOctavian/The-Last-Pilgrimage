package entities;

import main.Game;

import static utilz.Constants.Directions.LEFT;
import static utilz.Constants.Directions.RIGHT;
import static utilz.Constants.EnemyConstants.*;
import static utilz.Constants.PlayerConstants.DEATH;
import static utilz.HelpMethods.*;

public class Archer extends Enemy {
    private int attackDamage = 12;
    private boolean canAttack = true;
    private int attackCooldown = 90;
    private int attackCooldownTick = 0;
    private boolean isHit = false;
    private int hitEffectTick = 0;
    private int hitEffectDuration = 15;
    private boolean hasBeenDamaged = false;

    private float detectionRange = Game.TILES_SIZE * 4;
    private float attackRange = Game.TILES_SIZE * 3;
    private boolean isAiming = false;
    private int aimingTime = 30;
    private int aimingTick = 0;

    public Archer(float x, float y) {
        super(x, y, ARCHER_WIDTH, ARCHER_HEIGHT, ARCHER);

        float hitboxWidth = 22;
        float hitboxHeight = 80;

        float hitboxX = x + (ARCHER_WIDTH - hitboxWidth)/2;
        float hitboxY = y + ARCHER_HEIGHT - hitboxHeight;

        initHitbox(hitboxX, hitboxY, hitboxWidth, hitboxHeight);

        maxHealth = 35;
        health = maxHealth;
        walkSpeed = 0.8f * Game.SCALE;
    }

    @Override
    public void update(int[][] lvlData, Player player) {
        if (health <= 0) {
            if (enemyState != DEATH) {
                enemyState = DEATH;
                aniTick = 0;
                aniIndex = 0;
            }
            updateAnimationTick();
            return;
        }

        updateHitEffect();
        updateAttackCooldown();
        updateAiming();

        if (!player.isAttacking1() && !player.isAttacking2()) {
            hasBeenDamaged = false;
        }

        checkPlayerAttacks(player);
        checkArcherAttacks(player);

        updateMove(lvlData, player);
        updateAnimationTick();
    }

    private void updateHitEffect() {
        if (isHit) {
            hitEffectTick++;
            if (hitEffectTick >= hitEffectDuration) {
                isHit = false;
                hitEffectTick = 0;
            }
        }
    }

    private void updateAttackCooldown() {
        if (!canAttack) {
            attackCooldownTick++;
            if (attackCooldownTick >= attackCooldown) {
                canAttack = true;
                attackCooldownTick = 0;
            }
        }
    }

    private void updateAiming() {
        if (isAiming) {
            aimingTick++;
            if (aimingTick >= aimingTime) {
                isAiming = false;
                aimingTick = 0;
                if (enemyState == RUNNING && canAttack) {
                    newState(ATTACK);
                }
            }
        }
    }

    // Archer Implementation - Alert scanning behavior
    @Override
    protected void updateMove(int[][] lvlData, Player player) {
        if (firstUpdate)
            firstUpdateCheck(lvlData);

        if (inAir) {
            updateInAir(lvlData);
        } else {
            switch (enemyState) {
                case IDLE:
                    idleTick++;

                    if (canSeePlayer(lvlData, player)) {
                        newState(RUNNING);
                        turnTowardsPlayer(player);
                    } else {
                        if (idleTick % 240 == 0) { // Every 4 seconds
                            changeWalkDir();
                        }
                    }
                    break;

                case RUNNING:
                    if (canSeePlayer(lvlData, player)) {
                        turnTowardsPlayer(player);

                        if (isPlayerInArcherAttackRange(player)) {

                            if (!isAiming && canAttack) {
                                isAiming = true;
                                aimingTick = 0;
                            }
                        } else if (isPlayerTooClose(player)) {
                            if (walkDir == LEFT && player.getHitbox().x < hitbox.x) {
                                changeWalkDir();
                            } else if (walkDir == RIGHT && player.getHitbox().x > hitbox.x) {
                                changeWalkDir();
                            }
                            Move(lvlData);
                        } else if (isPlayerInArcherRange(player)) {
                            Move(lvlData);
                        } else {
                            Move(lvlData);
                        }
                    } else {
                        newState(IDLE);
                        idleTick = 0;
                        isAiming = false; // Stop aiming if we lose sight
                        aimingTick = 0;
                    }
                    break;

                case ATTACK:
                    if (aniIndex == 0 && aniTick == 0 && !isAiming) {
                        newState(RUNNING);
                    }
                    break;
            }
        }
    }

    private boolean isPlayerInArcherAttackRange(Player player) {
        int absValue = (int)Math.abs(player.getHitbox().x - hitbox.x);
        return absValue <= attackRange && absValue >= Game.TILES_SIZE;
    }

    private boolean isPlayerTooClose(Player player) {
        int absValue = (int)Math.abs(player.getHitbox().x - hitbox.x);
        return absValue < Game.TILES_SIZE * 1.5f; // Too close, need to back away
    }

    @Override
    protected boolean canSeePlayer(int[][] lvlData, Player player) {
        int playerTileY = (int)(player.getHitbox().y / Game.TILES_SIZE);
        boolean sameRow = Math.abs(playerTileY - tileY) <= 1;
        boolean inRange = isPlayerInArcherRange(player);
        boolean sightClear = IsSightClear(lvlData, hitbox, player.hitbox, tileY);
        return sameRow && inRange && sightClear;
    }

    private boolean isPlayerInArcherRange(Player player) {
        int absValue = (int)Math.abs(player.getHitbox().x - hitbox.x);
        return absValue <= detectionRange;
    }

    private void checkPlayerAttacks(Player player) {
        if ((player.isAttacking1() || player.isAttacking2()) && !isDead() && !hasBeenDamaged) {
            // Check if player is close enough to hit the archer
            int absValue = (int)Math.abs(player.getHitbox().x - hitbox.x);
            if (absValue <= Game.TILES_SIZE * 1.2f) { // Player must be close to hit archer
                int damage = player.isAttacking2() ? 20 : 10; // Attack2 does more damage
                takeDamage(damage);
                hasBeenDamaged = true;
            }
        }
    }

    private void checkArcherAttacks(Player player) {
        if (isDead()) return;


        if (enemyState == ATTACK && aniIndex == 4 && canAttack && !isAiming) {
            if (isPlayerInArcherAttackRange(player) && player.canBeDamaged()) {
                player.takeDamage(attackDamage);
                canAttack = false;
                attackCooldownTick = 0;
                System.out.println("Archer shot player for " + attackDamage + " damage!");
            }
        }
    }

    public void takeDamage(int damage) {
        if (isDead()) return;

        health -= damage;
        isHit = true;
        hitEffectTick = 0;

        System.out.println("Archer took " + damage + " damage. Health: " + health);

        if (health <= 0) {
            health = 0;
            onDeath();
            System.out.println("Archer health reached 0 - dying");
        }
    }

    @Override
    protected void onDeath() {
        enemyState = DEATH;
        aniTick = 0;
        aniIndex = 0;
        isAiming = false;
        aimingTick = 0;
        System.out.println("Archer onDeath() called");
    }

    public boolean isHit() {
        return isHit;
    }

    public boolean isDead() {
        return health <= 0;
    }

    public boolean isAiming() {
        return isAiming;
    }
}