package Tiles;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class Tile {
    public static Map<Integer, Tile> tiles = new HashMap<>();
    public static final int TILE_SIZE = 16;

    protected BufferedImage img;
    protected final int id;

    public Tile(BufferedImage image, int id) {
        this.img = image;
        this.id = id;
        if (!tiles.containsKey(id)) {
            tiles.put(id, this);
        }
    }

    public void Draw(Graphics g, int x, int y) {
        g.drawImage(img, x, y, TILE_SIZE, TILE_SIZE, null);
    }
}