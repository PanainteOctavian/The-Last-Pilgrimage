package entities;

import static utilz.Constants.EnemyConstants.*;

public class EnemyFactory {
    private Player player;

    public EnemyFactory(Player player) {
        this.player = player;
    }

    public Enemy createEnemy(int enemyType, float x, float y) {
        Enemy enemy = null;
        switch(enemyType) {
            case KNIGHT:
                enemy = new Knight(x, y);
                break;
            case ARCHER:
                enemy = new Archer(x, y);
                break;
            default:
                System.out.println("Unknown enemy type: " + enemyType);
                break;
        }
        return enemy;
    }
}