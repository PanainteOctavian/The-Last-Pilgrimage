package entities;

import static utilz.Constants.Directions.LEFT;
import static utilz.Constants.Directions.RIGHT;
import static utilz.Constants.EnemyConstants.*;
import static utilz.Constants.PlayerConstants.DEATH;
import static utilz.HelpMethods.*;

public class MiniBoss extends Enemy {
    private int attackDamage = 15;
    private boolean canAttack = true;
    private int attackCooldown = 60; // frames
    private int attackCooldownTick = 0;
    private boolean isHit = false;
    private int hitEffectTick = 0;
    private int hitEffectDuration = 15; // frames for hit effect to show
    private boolean hasBeenDamaged = false; // Prevent multiple damage per attack

    // Charging behavior properties
    private int normalAttackDamage = 15;
    private int chargeAttackDamage = 25; // More damage when charging

    private int knightIdleTime = 180; // 3 seconds at 60 FPS
    private boolean isPatrolling = false;

    public MiniBoss(float x, float y) {
        super(x, y, MINIBOSS_WIDTH, MINIBOSS_HEIGHT, MINIBOSS);

        float hitboxWidth = 25;
        float hitboxHeight = 70;

        float hitboxX = x + (MINIBOSS - hitboxWidth)/2;
        float hitboxY = y + MINIBOSS - hitboxHeight;

        initHitbox(hitboxX, hitboxY, hitboxWidth, hitboxHeight);

        maxHealth = 50;
        health = maxHealth;
    }

    @Override
    public void update(int[][] lvlData, Player player) {
        // Update attack damage based on charging state
        attackDamage = isCharging ? chargeAttackDamage : normalAttackDamage;

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

        if (!player.isAttacking1() && !player.isAttacking2()) {
            hasBeenDamaged = false;
        }
        checkPlayerAttacks(player);
        checkKnightAttacks(player);
        super.update(lvlData, player);
    }
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
                        isPatrolling = true;
                    }
                    else if (idleTick >= knightIdleTime) {
                        newState(RUNNING);
                        isPatrolling = true;
                        idleTick = 0;
                    }
                    break;

                case RUNNING:
                    if (canSeePlayer(lvlData, player)) {
                        turnTowardsPlayer(player);
                        isPatrolling = false;

                        if (isCharging) {
                            if (!isPlayerCloseForAttack(player)) {
                                Move(lvlData);
                            } else {
                                newState(ATTACK);
                            }
                        } else {
                            if (isPlayerCloseForAttack(player)) {
                                newState(ATTACK);
                            } else {
                                Move(lvlData);
                            }
                        }
                    } else {
                        // No player in sight
                        if (isPatrolling) {
                            Move(lvlData);
                            if (Math.random() < 0.005) {
                                newState(IDLE);
                                isPatrolling = false;
                                idleTick = 0;
                            }
                        } else {
                            newState(IDLE);
                            idleTick = 0;
                        }
                    }
                    break;

                case ATTACK:
                    if (aniIndex == 0 && aniTick == 0) {
                        newState(RUNNING);
                    }
                    break;
            }
        }
    }

    @Override
    protected boolean isPlayerInRange(Player player) {
        int absValue = (int)Math.abs(player.hitbox.x - hitbox.x);
        float range = isCharging ? attackDistance * 8 : attackDistance * 5;
        return absValue <= range;
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

            int currentCooldown = isCharging ? attackCooldown / 2 : attackCooldown;
            if (attackCooldownTick >= currentCooldown) {
                canAttack = true;
                attackCooldownTick = 0;
            }
        }
    }

    private void checkPlayerAttacks(Player player) {
        if ((player.isAttacking1() || player.isAttacking2()) && !isDead() && !hasBeenDamaged) {
            if (isPlayerInAttackRange(player)) {
                int damage = player.isAttacking2() ? 20 : 10;
                takeDamage(damage);
                hasBeenDamaged = true;
            }
        }
    }

    private void checkKnightAttacks(Player player) {
        if (isDead()) return;
        if (enemyState == ATTACK && aniIndex == 3 && canAttack) {
            if (isPlayerInAttackRange(player) && player.canBeDamaged()) {
                player.takeDamage(attackDamage);
                canAttack = false;

                if (isCharging) {
                    System.out.println("Knight charged attack for " + attackDamage + " damage!");
                } else {
                    System.out.println("Knight attacked for " + attackDamage + " damage!");
                }
            }
        }
    }

    private boolean isPlayerInAttackRange(Player player) {
        int playerHitboxCenterX = (int)(player.getHitbox().x + player.getHitbox().width / 2);
        int knightHitboxCenterX = (int)(hitbox.x + hitbox.width / 2);
        int xDistance = Math.abs(playerHitboxCenterX - knightHitboxCenterX);
        boolean facingRight = walkDir == RIGHT && playerHitboxCenterX > knightHitboxCenterX;
        boolean facingLeft = walkDir == LEFT && playerHitboxCenterX < knightHitboxCenterX;
        boolean closeEnough = xDistance <= attackDistance + 20;
        boolean atSameHeight = Math.abs(player.getHitbox().y - hitbox.y) < 30;
        return (facingRight || facingLeft) && closeEnough && atSameHeight;
    }

    @Override
    public void takeDamage(int damage) {
        if (isDead()) return;

        health -= damage;
        isHit = true;
        hitEffectTick = 0;
        if (health <= 0) {
            health = 0;
        }
    }

    @Override
    protected void onDeath() {
        enemyState = DEATH;
        aniTick = 0;
        aniIndex = 0;
    }

    @Override
    public void onPlayerHealthLow() {
        super.onPlayerHealthLow();
        System.out.println("Knight received low health notification - entering aggressive mode!");
    }

    @Override
    public void onPlayerHealthRestored() {
        super.onPlayerHealthRestored();
        System.out.println("Knight received health restored notification - returning to normal behavior");
    }

    public boolean isHit() {
        return isHit;
    }

    public boolean isDead() {
        return health <= 0;
    }
}