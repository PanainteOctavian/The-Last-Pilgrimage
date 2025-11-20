package Tiles;

import java.awt.image.BufferedImage;
import utilz.LoadSave;

public class TileManagers {

    public static final int LAND_ID = 0;
    public static final int SPIKE_ID = 1;
    public static final int POTION_ID = 2;

    public static Tile LAND;
    public static Tile SPIKE;
    public static Tile POTION;

    private BufferedImage atlas;
    private final int tileSize = Tile.TILE_SIZE;
    private int tilesPerRow;

    private void loadAtlas() {
        atlas = LoadSave.GetSpriteAtlas(LoadSave.TILE_ATLAS);
        if (atlas == null) {
            throw new RuntimeException("Failed to load tile atlas!");
        }
    }

    private void calculateTilesPerRow() {
        tilesPerRow = atlas.getWidth() / tileSize;
    }

    private Tile createTile(int id) {
        if (id < 0 || id >= tilesPerRow * (atlas.getHeight() / tileSize)) {
            throw new IllegalArgumentException("Invalid tile ID: " + id);
        }

        int x = (id % tilesPerRow) * tileSize;
        int y = (id / tilesPerRow) * tileSize;
        BufferedImage subImage = atlas.getSubimage(x, y, tileSize, tileSize);
        return new Tile(subImage, id);
    }

    private void createTiles() {
        LAND = createTile(LAND_ID);
        SPIKE = createTile(SPIKE_ID);
        POTION = createTile(POTION_ID);
    }

    public TileManagers() {
        loadAtlas();
        calculateTilesPerRow();
        createTiles();
    }
}