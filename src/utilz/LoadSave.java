package utilz;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

import Tiles.XmlToMatrix;
import main.Game;

public class LoadSave {

    public static final String PLAYER_ATLAS = "Spriteuri/Entitati/Protagonist1.png";

    public static final String TILE_ATLAS = "Tileuri/tileuri.png";
    public static final String BACKGROUND1 = "Back/Pestera.png";
    public static final String BACKGROUND2 = "Back/Padure2.png";
    public static final String BACKGROUND3 = "Back/Castel.png";

    public static final String MENU_BUTTONS = "Spriteuri/Menu/Menu_Pause.png";
    public static final String MAIN_MENU_BUTTONS = "Spriteuri/Menu/Menu_Main.png";
    public static final String MENU_BACKGROUND = "Spriteuri/Menu/Back screen.jpg";
    public static final String Options_Buttons_large = "Spriteuri/Menu/Menu_Options.png";
    public static final String Level_complete_button = "Spriteuri/Menu/Menu_Finish_Level.png";
    public static final String LEVEL_COMPLETE_IMG = "Spriteuri/Menu/Level_Complete.png";
    public static final String LEVEL_DEATH = "Spriteuri/Menu/Level_Death.png";
    public static final String MENU_DEATH = "Spriteuri/Menu/Menu_Death.png";

    public static final String KNIGHTSPRITE = "Spriteuri/Entitati/KnightSprite2.png";
    //public static final String Enemy_boss = "Spriteuri/Entitati/ .png";
    public static final String Enemy_Archer = "Spriteuri/Entitati/Archer.png";
    //public static final String Enemy_Mage = "Spriteuri/Entitati/ .png";
    public static final String NPCMALESPRITE = "Spriteuri/Entitati/NpcMale.png";


    public static BufferedImage GetSpriteAtlas(String fileName) {
        BufferedImage img = null;
        InputStream is = LoadSave.class.getResourceAsStream("/" + fileName);
        try {
            img = ImageIO.read(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return img;
    }

    public static int[][] GetLevelDataFromXml(int levelNumber) {
        String xmlFilePath = switch (levelNumber) {
            case 2 -> "res/Mape XML/Padure.xml";
            case 3 -> "res/Mape XML/Castel.xml";
            default -> "res/Mape XML/Pestera.xml";
        };

        int[][] lvlData = XmlToMatrix.GetIdMatrix(xmlFilePath);
        for (int j = 0; j < lvlData.length; j++) {
            for (int i = 0; i < lvlData[j].length; i++) {
                lvlData[j][i] = lvlData[j][i]-1;
            }
        }

        return lvlData;
    }

    public static BufferedImage GetMenuBackground() {
        return GetSpriteAtlas(MENU_BACKGROUND);
    }

    public static BufferedImage GetBackground(int levelnumber) {
        return switch (levelnumber) {
            case 2 -> GetSpriteAtlas(BACKGROUND2);
            case 3 -> GetSpriteAtlas(BACKGROUND3);
            default -> GetSpriteAtlas(BACKGROUND1);
        };
    }
}
