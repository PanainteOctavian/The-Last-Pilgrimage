package utilz;

import main.Game;

import java.awt.geom.Rectangle2D;

public class HelpMethods {

    public static boolean CanMoveHere(float x, float y, float width, float height, int[][] lvlData) {
        if (!IsSolid(x, y, lvlData) &&
                !IsSolid(x+width, y+height, lvlData) &&
                !IsSolid(x+width, y, lvlData) &&
                !IsSolid(x, y+height, lvlData)) {
            float step = Game.TILES_SIZE/2f;
            for (float xCheck = x; xCheck <= x+width; xCheck += step) {
                if (IsSolid(xCheck, y, lvlData) || IsSolid(xCheck, y+height, lvlData))
                    return false;
            }
            for (float yCheck = y; yCheck <= y+height; yCheck += step) {
                if (IsSolid(x, yCheck, lvlData) || IsSolid(x+width, yCheck, lvlData))
                    return false;
            }
            return true;
        }
        return false;
    }

    public static boolean IsSolid(float x, float y, int[][] lvlData){
        if (x < 0 || x >= Game.GAME_WIDTH)
            return true;
        if (y < 0 || y >= Game.GAME_HEIGHT)
            return true;

        float xIndex = x / Game.TILES_SIZE;
        float yIndex = y / Game.TILES_SIZE;

        return IsTileSolid((int)xIndex, (int)yIndex, lvlData);
    }

    public static boolean IsTileSolid(int xTile, int yTile, int[][] lvlData){
        int value = lvlData[(int) yTile][(int) xTile];

        if (value == -1) return false;
        else return true;
    }

    public static boolean IsEntityOnFloor(Rectangle2D.Float hitbox, int[][] lvlData) {
        boolean leftCornerHasFloor = IsSolid(hitbox.x, hitbox.y + hitbox.height + 1, lvlData);
        boolean rightCornerHasFloor = IsSolid(hitbox.x + hitbox.width - 1, hitbox.y + hitbox.height + 1, lvlData);
        return leftCornerHasFloor || rightCornerHasFloor;
    }

    public static boolean IsFloor(Rectangle2D.Float hitbox, float xSpeed, int[][] lvlData) {
        for (int i = 0; i <= hitbox.width; i += Game.TILES_SIZE/2) {
            if (IsSolid(hitbox.x + i + xSpeed, hitbox.y + hitbox.height + 1, lvlData)) {
                return true;
            }
        }
        return false;
    }

    public static boolean IsAllTileWalkable(int xStart, int xEnd, int y, int[][] lvlData){
        for(int i=0; i<xEnd - xStart; ++i){
            if(IsTileSolid(xStart + i, y , lvlData)){
                return false;
            }
        }
        return true;
    }

    public static boolean IsSightClear(int[][] lvlData, Rectangle2D.Float hitbox1, Rectangle2D.Float hitbox2, int yTile){
        int firstXTile = (int)(hitbox1.x / Game.TILES_SIZE);
        int secondXTile = (int)(hitbox2.x / Game.TILES_SIZE);

        if(firstXTile > secondXTile)
            return IsAllTileWalkable(secondXTile, firstXTile, yTile, lvlData);
        else
            return IsAllTileWalkable(firstXTile, secondXTile, yTile, lvlData);

    }

}