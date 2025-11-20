package levels;

import main.Game;

public class Level {

    private int[][] lvlData;
    // Fiecare valoare din matrice reprezintă un index al unui sprite (imagine) dintr-un tileset (set de imagini pentru nivel)

    public Level(int[][] lvlData) {
        if (lvlData == null) {
            System.err.println("Error: Level matrix is null!");
            this.lvlData = new int[Game.TILES_IN_HEIGHT][Game.TILES_IN_WIDTH]; // Fallback
        } else {
            this.lvlData = lvlData;
        }
    }

    public int getSpriteIndex(int x, int y) { // Returnează indexul sprite-ului de la coordonatele (x, y) din nivel
        if (x >= 0 && x < lvlData[0].length && y >= 0 && y < lvlData.length) {
            return lvlData[y][x]; // matricea este indexată pe rânduri și coloane
        }
        return -1;
    }


    public int getRows()       { return lvlData.length; }
    public int getCols()       { return lvlData[0].length; }

    public int[][] getLvlData(){
        return lvlData;
    }
}
