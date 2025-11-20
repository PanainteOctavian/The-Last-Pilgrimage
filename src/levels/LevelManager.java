package levels;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import main.Game;
import utilz.LoadSave;
import Tiles.Tile;
import Tiles.TileManagers;

public class LevelManager {
    private Game game;
    private Level[] levels;
    private int currentLevelIndex;
    private TileManagers tileManagers;
    private BufferedImage[] backgrounds;

    public LevelManager(Game game) {
        this.game = game;
        tileManagers = new TileManagers();
        initLevels();
        initBackgrounds();
    }

    private void initLevels() {
        // Initialize the levels array with 3 levels
        levels = new Level[3];
        levels[0] = new Level(LoadSave.GetLevelDataFromXml(1)); // Cave level
        levels[1] = new Level(LoadSave.GetLevelDataFromXml(2)); // Forest level
        levels[2] = new Level(LoadSave.GetLevelDataFromXml(3)); // Castle level
        currentLevelIndex = 0;
    }

    private void initBackgrounds() {
        backgrounds = new BufferedImage[3];
        backgrounds[0] = LoadSave.GetSpriteAtlas(LoadSave.BACKGROUND1); // Cave background
        backgrounds[1] = LoadSave.GetSpriteAtlas(LoadSave.BACKGROUND2); // Forest background
        backgrounds[2] = LoadSave.GetSpriteAtlas(LoadSave.BACKGROUND3); // Castle background
    }

    public void draw(Graphics g) {
        Level currentLevel = getCurrentLevel();
        for (int j = 0; j < Game.TILES_IN_HEIGHT; j++) {
            for (int i = 0; i < Game.TILES_IN_WIDTH; i++) {
                int id = currentLevel.getSpriteIndex(i, j);
                if(id == -1) continue;
                if (Tile.tiles.containsKey(id)) {
                    Tile.tiles.get(id).Draw(g, Game.TILES_SIZE * i, Game.TILES_SIZE * j);
                } else {
                    System.err.println("Unknown tile ID: " + id);
                }
            }
        }
    }

    public void update() {

    }

    public Level getCurrentLevel() {
        return levels[currentLevelIndex];
    }

    public BufferedImage getCurrentBackground() {
        return backgrounds[currentLevelIndex];
    }

    public int getCurrentLevelIndex() {
        return currentLevelIndex;
    }

    public void setLevel(int levelIndex) {
        if (levelIndex >= 0 && levelIndex < levels.length) {
            this.currentLevelIndex = levelIndex;
            System.out.println("Switched to level: " + (levelIndex + 1));
        } else {
            System.err.println("Invalid level index: " + levelIndex);
        }
    }

    public boolean changeLevel(int levelIndex) {
        if (levelIndex >= 0 && levelIndex < levels.length) {
            this.currentLevelIndex = levelIndex;
            return true;
        }
        return false;
    }

    public int getLevelCount() {
        return levels.length;
    }
}