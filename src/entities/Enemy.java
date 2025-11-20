package entities;

import static utilz.Constants.EnemyConstants.*;
import static utilz.Constants.PlayerConstants.DEATH;
import static utilz.HelpMethods.*;
import static utilz.Constants.Directions.*;

import main.Game;
import utilz.Camera;

import java.awt.*;

public abstract class Enemy extends Entity implements HealthObserver {
    protected int aniIndex, enemyState, enemyType;
    protected int aniTick, aniSpeed = 20;
    protected boolean firstUpdate = true;
    protected boolean inAir;
    protected float fallSpeed;
    protected float gravity = 0.04f * Game.SCALE;
    protected float walkSpeed = 1f * Game.SCALE;
    protected int walkDir = LEFT;
    protected int tileY;
    protected float attackDistance = Game.TILES_SIZE * 2;

    protected boolean isCharging = false;
    protected float chargeSpeed = 2.5f * Game.SCALE;

    // Add these new fields for IDLE behavior
    protected int idleTick = 0;

    public Enemy(float x, float y, int width, int height, int enemyType) {
        super(x, y, width, height);
        this.enemyType = enemyType;
        initHitbox(x, y, width, height);
        this.enemyState = IDLE;
    }

    public void drawHitbox(Graphics g, Camera camera) {
        g.setColor(Color.RED);
        g.drawRect(
                (int)(hitbox.x - camera.getxOffset()),
                (int)(hitbox.y - camera.getyOffset()),
                (int)hitbox.width,
                (int)hitbox.height
        );
    }

    protected void updateAnimationTick() {
        aniTick++;
        if (aniTick >= aniSpeed) {
            aniTick = 0;
            aniIndex++;
            if (aniIndex >= GetSpriteAmount(enemyType, enemyState)) {
                aniIndex = 0;
                if(enemyState == ATTACK)
                    enemyState = RUNNING;
                else if(enemyState == DEATH)
                    aniIndex = GetSpriteAmount(enemyType, enemyState) - 1; // Stay on last frame
            }
        }
    }

    protected void firstUpdateCheck(int[][] lvlData){
        if (!IsEntityOnFloor(hitbox, lvlData))
            inAir = true;
        firstUpdate = false;
    }

    protected void updateInAir(int[][] lvlData) {
        if (CanMoveHere(hitbox.x, hitbox.y + fallSpeed, hitbox.width, hitbox.height, lvlData)) {
            hitbox.y += fallSpeed;
            fallSpeed += gravity;
        } else {
            inAir = false;
            // This is crucial - make sure enemy is positioned exactly at floor level
            //hitbox.y = GetEntityYPosUnderRoofOrAboveFloor(hitbox, fallSpeed);
            // Reset fall speed when landing
            fallSpeed = 0;
            tileY = (int)(hitbox.y / Game.TILES_SIZE);
        }
    }

    protected void Move(int[][] lvlData) {
        float currentSpeed = isCharging ? chargeSpeed : walkSpeed;
        float xSpeed = (walkDir == LEFT) ? -currentSpeed : currentSpeed;
        boolean canMove = CanMoveHere(hitbox.x + xSpeed + 1, hitbox.y, hitbox.width, hitbox.height, lvlData);
        boolean isFloor = IsFloor(hitbox, xSpeed, lvlData);

        if (canMove && isFloor) {
            hitbox.x += xSpeed;
        } else {
            changeWalkDir();
        }
    }

    protected void newState(int enemyState){
        this.enemyState = enemyState;
        aniTick = 0;
        aniIndex = 0;
    }

    protected void update(int[][] lvlData, Player player) {
        if (health <= 0) {
            if (enemyState != DEATH) {
                newState(DEATH);
            }
            updateAnimationTick();
            return;
        }

        updateMove(lvlData, player);
        updateAnimationTick();
    }

    // Remove the automatic IDLE -> RUNNING transition from base class
    protected void updateMove(int[][] lvlData, Player player) {
        if (firstUpdate)
            firstUpdateCheck(lvlData);

        if (inAir) {
            updateInAir(lvlData);
        } else {
            switch (enemyState) {
                case IDLE:
                    newState(RUNNING);
                    break;
                case RUNNING:
                    if (canSeePlayer(lvlData, player)) {
                        turnTowardsPlayer(player);
                    }
                    if (isPlayerCloseForAttack(player) && hitbox.y == player.getWorldY()) {
                        newState(ATTACK);
                    } else {
                        Move(lvlData);
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

    protected void changeWalkDir() {
        if (walkDir == LEFT)
            walkDir = RIGHT;
        else
            walkDir = LEFT;
    }

    protected boolean isPlayerInRange(Player player){
        int absValue = (int)Math.abs(player.hitbox.x - hitbox.x);
        float range = isCharging ? attackDistance * 7 : attackDistance * 5;
        return absValue <= range;
    }

    protected boolean isPlayerCloseForAttack(Player player){
        int absValue = (int)Math.abs(player.hitbox.x - hitbox.x);
        return absValue <= attackDistance;
    }

    protected boolean canSeePlayer(int[][] lvlData, Player player) {
        int playerTileY = (int)(player.getHitbox().y / Game.TILES_SIZE);
        boolean sameRow = Math.abs(playerTileY - tileY) <= 1;
        boolean inRange = isPlayerInRange(player);
        boolean sightClear = IsSightClear(lvlData, hitbox, player.hitbox, tileY);
        return sameRow && inRange && sightClear;
    }

    protected void turnTowardsPlayer(Player player){
        if(player.hitbox.x > hitbox.x)
            walkDir = RIGHT;
        else
            walkDir = LEFT;
    }

    public int getEnemyState() {
        return enemyState;
    }

    public int getAniIndex() {
        return aniIndex;
    }

    public abstract void takeDamage(int damage);

    protected void onDeath() {
        newState(DEATH);
        System.out.println("Enemy died");
    }

    @Override
    public void onPlayerHealthLow() {
        if (!isDead()) {
            isCharging = true;
            System.out.println("Enemy is now charging - player health is low!");
        }
    }

    @Override
    public void onPlayerHealthRestored() {
        isCharging = false;
        System.out.println("Enemy stopped charging - player health restored");
    }

    public boolean isCharging() {
        return isCharging;
    }

    public abstract boolean isDead();
}