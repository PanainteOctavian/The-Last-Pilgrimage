package gamestates;

public enum Gamestate {
    PLAYING, MENU, OPTIONS, QUIT, LOAD, MAIN_MENU, SAVE, EXIT, BACK, LEVELCOMPLETE, DEATH;

    public static Gamestate state = MAIN_MENU;
}
