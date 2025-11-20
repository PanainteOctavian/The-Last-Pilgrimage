package utilz;

import main.Game;

public class Constants
{
    public static class EnemyConstants{
        public static final int KNIGHT = 0;
        public static final int ARCHER = 1;
        public static final int MINIBOSS = 2;
        public static final int BOSS = 3;

        public static final int IDLE = 0;
        public static final int ATTACK = 1;
        public static final int DEAD = 2;
        public static final int RUNNING = 3;

        public static final int KNIGHT_WIDTH_DEFAULT = 64;
        public static final int KNIGHT_HEIGHT_DEFAULT = 44;
        public static final int ARCHER_WIDTH_DEFAULT = 43;
        public static final int ARCHER_HEIGHT_DEFAULT = 25;
        public static final int ARCHER_HITBOX_X_OFFSET = 11;
        public static final int ARCHER_HITBOX_Y_OFFSET = 15;
        public static final int KNIGHT_WIDTH = (int)(KNIGHT_WIDTH_DEFAULT * Game.SCALE);
        public static final int KNIGHT_HEIGHT = (int)(KNIGHT_HEIGHT_DEFAULT * Game.SCALE);
        public static final int ARCHER_WIDTH = (int)(ARCHER_HEIGHT_DEFAULT * Game.SCALE);
        public static final int ARCHER_HEIGHT = (int)(ARCHER_WIDTH_DEFAULT * Game.SCALE);

        public static final int BOSS_WIDTH_DEFAULT = 64;
        public static final int BOSS_HEIGHT_DEFAULT = 44;
        public static final int BOSS_WIDTH = (int)(BOSS_WIDTH_DEFAULT * Game.SCALE);
        public static final int BOSS_HEIGHT = (int)(BOSS_HEIGHT_DEFAULT * Game.SCALE);

        public static final int MINIBOSS_WIDTH_DEFAULT = 64;
        public static final int MINIBOSS_HEIGHT_DEFAULT = 44;
        public static final int MINIBOSS_WIDTH = (int)(MINIBOSS_WIDTH_DEFAULT * Game.SCALE);
        public static final int MINIBOSS_HEIGHT = (int)(MINIBOSS_HEIGHT_DEFAULT * Game.SCALE);


        public static final int KNIGHT_ANIMATION_STATES = 4;
        public static final int ARCHER_ANIMATION_STATES = 4;
        public static final int BOSS_ANIMATION_STATES = 4;
        public static final int MINIBOSS_ANIMATION_STATES = 4;

        public static final int KNIGHT_DRAW_WIDTH = 80;
        public static final int KNIGHT_DRAW_HEIGHT = 100;
        public static final float ENEMY_HITBOX_X_OFFSET = 20 * Game.SCALE;
        public static final float ENEMY_HITBOX_Y_OFFSET = 10 * Game.SCALE;

        public static int GetSpriteAmount(int enemy_type, int enemy_state){
            switch (enemy_type){
                case KNIGHT:
                    switch (enemy_state){
                        case IDLE, DEAD:return 15;
                        case ATTACK:return 17;
                        case RUNNING:return 8;
                    }
                case ARCHER:
                    switch (enemy_state){
                        case IDLE, RUNNING:
                            return 8;
                        case ATTACK:return 19;
                        case DEAD:return 24;
                    }
            }
            return 0;
        }
    }

    public static class UI{
        public static class Buttons{
            public static final int B_WIDTH_DEFAULT = 180;
            public static final int B_HEIGHT_DEFAULT = 60;
            public static final int B_WIDTH = (int) (B_WIDTH_DEFAULT * Game.SCALE);
            public static final int B_HEIGHT = (int) (B_HEIGHT_DEFAULT * Game.SCALE);
        }
    }
    public static class Directions {
        public static final int LEFT = 0;
        public static final int UP = 1;
        public static final int RIGHT = 2;
        public static final int DOWN = 3;
    }

    public static class PlayerConstants{
        public static final int IDLE = 0;
        public static final int DEATH = 1;
        public static final int DEATH_NO_MOVEMENT = 2;
        public static final int JUMP_FALLING_BETWEEN = 3;
        public static final int JUMP = 4;
        public static final int RUN = 5;
        public static final int TURN_AROUND = 6;
        public static final int HIT = 7;
        public static final int FALL = 8;
        public static final int ATTACK_1 = 9;
        public static final int ATTACK_2 = 10;
        public static final int ATTACK1_NO_MOVEMENT = 11;
        public static final int ATTACK2_NO_MOVEMENT = 12;
        public static final int ATTACKCOMBO = 13;
        public static final int ATTACKCOMBO_NO_MOVEMENT = 14;
        public static final int CROUCH = 15;
        public static final int CROUCH_TRANSITION = 16;
        public static final int CROUCH_FULL = 17;
        public static final int CROUCH_ATTACK = 18;
        public static final int CROUCH_WALK = 19;


        public static int GetSpriteAmmount(int player_action) {
            switch (player_action)
            {
                case DEATH:
                case DEATH_NO_MOVEMENT:
                    return 10;
                case RUN:
                    return 7;
                case JUMP_FALLING_BETWEEN:
                case TURN_AROUND:
                    return 2;
                case JUMP:
                case FALL:
                case CROUCH_FULL:
                    return 3;
                case HIT:
                case CROUCH:
                case CROUCH_TRANSITION:
                    return 1;
                case CROUCH_ATTACK :
                case ATTACK_1:
                case ATTACK1_NO_MOVEMENT:
                case ATTACKCOMBO:
                case ATTACKCOMBO_NO_MOVEMENT:
                    return 4;
                case ATTACK_2:
                case IDLE:
                    return 6;
                case ATTACK2_NO_MOVEMENT:
                    return 5;
                case CROUCH_WALK:
                    return 8;
                default:
                    return 1;
            }
        }
    }
}