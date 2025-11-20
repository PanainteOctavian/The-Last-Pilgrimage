package entities;

import main.Game;
import utilz.LoadSave;
import static utilz.HelpMethods.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import static utilz.Constants.PlayerConstants.*;
import java.util.ArrayList;
import java.util.List;

public class Player extends Entity implements HealthSubject {

    private BufferedImage[][] animations;
    private int aniTick, aniIndex, aniSpeed = 20;
    private int playerAction = IDLE;
    private boolean moving = false, attacking1 = false, crouch = false, attacking2 = false, crouchattack = false;
    private boolean jumping = false, falling = false, right = false, left = false;
    private float playerSpeed = 0.01f;

    private int[][] lvlData;
    private float xDrawOffset = 10 * Game.SCALE;
    private float yDrawOffset = 4 * Game.SCALE;

    // jumping / gravity
    private float airSpeed = 1f;
    private float gravity = 0.02f * Game.SCALE;
    private float jumpSpeed = -1.5f * Game.SCALE;
    private float fallSpeedAfterCollision = 0.5f * Game.SCALE;
    private boolean inAir = false;

    // hitbox crouch
    private float normalHitboxHeight;
    private float crouchHitboxHeight;

    private int Score = 0;
    private boolean isDying = false;
    private int deathAnimationTick = 0;
    private int maxDeathAnimationTicks = 120;
    private boolean isHit = false;
    private int hitEffectTick = 0;
    private int hitEffectDuration = 15;

    // Stamina system
    private int maxStamina = 100;
    private int currentStamina = 100;
    private float staminaRegenRate = 1.25f; // 1.25 points per second
    private float staminaAccumulator = 0;
    private int staminaAttack2Cost = 20;
    private boolean staminaDepleted = false;

    // Health Observer Pattern
    private List<HealthObserver> healthObservers = new ArrayList<>();
    private boolean wasLowHealth = false;
    private static final int LOW_HEALTH_THRESHOLD = 30;

    public Player(float x, float y, int width, int height) {
        super(x, y, width, height);
        LoadAnimation();
        this.normalHitboxHeight = height;
        this.crouchHitboxHeight = height * 0.6f;
        initHitbox(x, y, width, normalHitboxHeight);
        this.y = y;
        this.maxHealth = 100;
        this.health = maxHealth;
    }

    public void update(){
        System.out.println("x= " + hitbox.x + " y= " + hitbox.y);
        if (isDying) {
            updateDeathAnimation();
            return;
        }

        if (isDead()) {
            startDeathAnimation();
            return;
        }
        updateHitEffect();
        updatePos();
        updateAnimationTick();
        setAnimation();
        updateStamina();
    }

    private void updateStamina() {
        if (currentStamina < maxStamina) {
            staminaAccumulator += staminaRegenRate / 60; // 60 FPS
            if (staminaAccumulator >= 1) {
                int pointsToAdd = (int)staminaAccumulator;
                currentStamina = Math.min(maxStamina, currentStamina + pointsToAdd);
                staminaAccumulator -= pointsToAdd;
                if (currentStamina >= staminaAttack2Cost) {
                    staminaDepleted = false;
                }
            }
        }
    }

    private void startDeathAnimation() {
        isDying = true;
        deathAnimationTick = 0;
        playerAction = DEATH;
        resetAniTick();
    }

    private void updateDeathAnimation() {
        deathAnimationTick++;
        if (deathAnimationTick >= maxDeathAnimationTicks) {
            isDying = false;
        }
        updateAnimationTick();
    }

    @Override
    protected void onDeath() {
        startDeathAnimation();
    }

    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        float drawYOffset = crouch ? (normalHitboxHeight - crouchHitboxHeight) : 0;

        if (left && !right) {
            // Flip sprite by drawing with negative width when moving left
            // Don't add the full width, just keep the same base position
            g2d.drawImage(animations[playerAction][aniIndex],
                    (int)(hitbox.x - xDrawOffset), // Keep same x position as normal
                    (int)(hitbox.y - yDrawOffset - drawYOffset),
                    -144, 120, null); // Negative width flips horizontally
        } else {
            // Normal drawing (facing right) - default and when moving right
            g2d.drawImage(animations[playerAction][aniIndex],
                    (int)(hitbox.x - xDrawOffset),
                    (int)(hitbox.y - yDrawOffset - drawYOffset),
                    144, 120, null);
        }
    }

    private void updateAnimationTick() {
        aniTick++;
        if (aniTick >= aniSpeed) {
            aniTick = 0;
            aniIndex++;
            if (aniIndex >= GetSpriteAmmount(playerAction)){
                aniIndex = 0;
                attacking1 = false;
                attacking2 = false;
            }
        }
    }

    private void resetAniTick() {
        aniTick = 0;
        aniIndex = 0;
    }

    private void setAnimation() {
        int startAni = playerAction;

        // Priority: Attacks > Jumping/Falling > Crouching > Moving > Idle
        if (attacking1 && !crouch && !inAir) {
            playerAction = ATTACK_1;
        } else if (attacking2 && !crouch && !inAir) {
            playerAction = ATTACK_2;
        } else if (inAir) {
            if (airSpeed < 0) { // Moving upward (jumping)
                playerAction = JUMP;
            } else { // Moving downward (falling)
                playerAction = FALL;
            }
        } else if (crouch) {
            if (attacking1) {
                playerAction = CROUCH_ATTACK;
            } else if (moving) {
                playerAction = CROUCH_WALK;
            } else {
                playerAction = CROUCH;
            }
        } else if (moving) {
            playerAction = RUN;
        } else {
            playerAction = IDLE;
        }

        if (startAni != playerAction) {
            resetAniTick();
        }
    }

    public void loadLvlData(int[][] lvlData){
        this.lvlData = lvlData;
        if (!IsEntityOnFloor(hitbox, lvlData))
            inAir = true;
    }

    private void updatePos() {
        if (isDead() || isDying) return;
        if (attacking1 || attacking2) return;
        playerSpeed = crouch ? 1.0f : 2.0f; // Crouch moves slower
        moving = false;

        if (jumping && !inAir) {
            // Stand up if crouching when jumping
            if (crouch) {
                setCrouching(false); // This will try to stand up
                // If still crouched (couldn't stand up), don't jump
                if (crouch) {
                    jumping = false;
                    return;
                }
            }
            jump();
        }

        // Horizontal movement
        float xSpeed = 0;
        if (left) xSpeed -= playerSpeed;
        if (right) xSpeed += playerSpeed;

        if (!inAir && !IsEntityOnFloor(hitbox, lvlData)) {
            inAir = true;
        }
        if (inAir) {
            if (CanMoveHere(hitbox.x, hitbox.y + airSpeed, hitbox.width, hitbox.height, lvlData)) {
                hitbox.y += airSpeed;
                airSpeed += gravity;
                updateXPos(xSpeed);
            } else {
                if (airSpeed > 0) {
                    resetInAir();
                } else {
                    airSpeed = fallSpeedAfterCollision;
                }
                updateXPos(xSpeed);
            }
        } else {
            updateXPos(xSpeed);
        }
        moving = (left || right) && !inAir;
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

    private void jump() {
        if (inAir)
            return;
        inAir = true;
        airSpeed = jumpSpeed;
    }

    private void resetInAir() {
        inAir = false;
        airSpeed = 0;
    }

    private void updateXPos(float xSpeed) {
        if (CanMoveHere(hitbox.x + xSpeed, hitbox.y, hitbox.width, hitbox.height, lvlData)){
            hitbox.x += xSpeed;
        }
    }

    private void LoadAnimation() {
        int frameWidth = 120;
        int frameHeight = 60;
        int verticalFootSpacing = 63;
        int totalAnimations = 20;
        int maxFramesPerAnimation = 10;
        BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_ATLAS);
        animations = new BufferedImage[totalAnimations][maxFramesPerAnimation];
        for (int j = 0; j < totalAnimations; j++) {
            for (int i = 0; i < maxFramesPerAnimation; i++) {
                int x = i * frameWidth;
                int y = j * verticalFootSpacing;
                if (x + frameWidth <= img.getWidth() && y + frameHeight <= img.getHeight()) {
                    animations[j][i] = img.getSubimage(x, y, frameWidth, frameHeight);
                }
            }
        }
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        if (hitbox != null) {
            hitbox.x = x;
            hitbox.y = y;
        }

        resetInAir();
        resetDirBooleans();
    }

    // Add getters for world coordinates (needed for camera)

    public void setLeft(boolean left) {
        this.left = left;
    }


    public void setJumping(boolean jumping) {
        this.jumping = jumping;
    }


    public void setRight(boolean right) {
        this.right = right;
    }


    public boolean isAttacking1() {
        return attacking1;
    }

    public void resetDirBooleans() {
        left = false;
        right = false;
        jumping = false;
        falling = false;
        attacking1 = false;
        attacking2 = false;
        crouchattack = false;
    }

    public boolean canBeDamaged() {
        return !isDead() && !isInDeathAnimation();
    }

    public void setAttacking1(boolean attacking) {
        this.attacking1 = attacking;
    }

    public boolean isAttacking2() {
        return attacking2;
    }

    public void setAttacking2(boolean attacking) {
        // Check if we have enough stamina to perform attack2
        if (attacking && currentStamina >= staminaAttack2Cost && !staminaDepleted) {
            this.attacking2 = true;
            currentStamina -= staminaAttack2Cost;

            // Check if we've run out of stamina
            if (currentStamina < staminaAttack2Cost) {
                staminaDepleted = true;
            }
        } else if (!attacking) {
            this.attacking2 = false;
        }
    }

    public void setCrouching(boolean crouching) {
        if (inAir) {
            return;
        }

        if (crouching == this.crouch) {
            return;
        }

        this.crouch = crouching;

        if (crouching) {
            float newHeight = crouchHitboxHeight;
            float heightDifference = normalHitboxHeight - newHeight;
            float newY = hitbox.y + heightDifference;

            if (CanMoveHere(hitbox.x, newY, hitbox.width, newHeight, lvlData)) {
                hitbox.height = newHeight;
                hitbox.y = newY;
            } else {
                this.crouch = false;
            }
        } else {
            float newHeight = normalHitboxHeight;
            float heightDifference = newHeight - hitbox.height;
            float newY = hitbox.y - heightDifference;

            if (CanMoveHere(hitbox.x, newY, hitbox.width, newHeight, lvlData)) {
                hitbox.height = newHeight;
                hitbox.y = newY;
            } else {
                this.crouch = true;
            }
        }
    }

    private void checkHealthThreshold() {
        boolean isCurrentlyLowHealth = health <= LOW_HEALTH_THRESHOLD;

        if (isCurrentlyLowHealth != wasLowHealth) {
            notifyHealthObservers(isCurrentlyLowHealth);
            wasLowHealth = isCurrentlyLowHealth;

            // Debug output
            if (isCurrentlyLowHealth) {
                System.out.println("Player health is low (" + health + "/" + maxHealth + ") - notifying enemies to charge!");
            } else {
                System.out.println("Player health restored (" + health + "/" + maxHealth + ") - enemies stop charging.");
            }
        }
    }

    @Override
    public void setHealth(int health) {
        super.setHealth(health);
        checkHealthThreshold();
    }

    @Override
    public void takeDamage(int damage) {
        if (!isDead() && !isDying) {
            int newHealth = health - damage;
            setHealth(Math.max(0, newHealth)); // This will trigger checkHealthThreshold
            if (health <= 0) {
                onDeath();
            }
        }
    }

    @Override
    public void addHealthObserver(HealthObserver observer) {
        healthObservers.add(observer);
        System.out.println("Health observer added. Total observers: " + healthObservers.size());
    }


    @Override
    public void notifyHealthObservers(boolean isLowHealth) {
        System.out.println("Notifying " + healthObservers.size() + " observers. Low health: " + isLowHealth);
        for (HealthObserver observer : healthObservers) {
            if (isLowHealth) {
                observer.onPlayerHealthLow();
            } else {
                observer.onPlayerHealthRestored();
            }
        }
    }

    public int getScore() {
        return Score;
    }

    public void setScore(int Score) {
        this.Score = Score;
    }

    public boolean isDeathAnimationComplete() {
        return isDead() && !isDying;
    }

    public boolean isInDeathAnimation() {
        return isDying;
    }

    // New stamina-related getters
    public int getCurrentStamina() {
        return currentStamina;
    }

    public int getMaxStamina() {
        return maxStamina;
    }

    public void setStamina(int stamina) {
        this.currentStamina = Math.max(0, Math.min(maxStamina, stamina));
        this.staminaDepleted = currentStamina < staminaAttack2Cost;
    }

    @Override
    public void reset() {
        super.reset();
        resetDirBooleans();
        isDying = false;
        deathAnimationTick = 0;
        currentStamina = maxStamina;
        staminaDepleted = false;
        wasLowHealth = false;
        checkHealthThreshold(); // Check health threshold on reset
    }
    public Rectangle getAttackBox() {
        int attackWidth = 40;
        int attackHeight = 40;
        int attackX;

        if (left && !right) { // Player is facing left
            attackX = (int) (hitbox.x - attackWidth);
        } else { // Player is facing right (default)
            attackX = (int) (hitbox.x + hitbox.width);
        }

        int attackY = (int) (hitbox.y + hitbox.height / 4);
        return new Rectangle(attackX, attackY, attackWidth, attackHeight);
    }

    }