package main;

import gamestates.*;
import BazaDeDate.DataBase;
import gamestates.Menu;
import gamestates.LevelComplete;

import java.awt.*;

public class Game implements Runnable
{
    private GameWindow gameWindow;
    private GamePanel gamePanel;
    private Thread gameThread;
    private static Game instance;

    private final int FPS_SET = 120;
    private final int UPS_SET = 200;

    private Playing playing;
    private Menu menu;
    private Main_Menu main_menu;
    private Options options;
    private DataBase dataBase;
    private LevelComplete levelComplete;
    private Death death;

    public final static int TILES_DEFAULT_SIZE = 16;
    public final static float SCALE = 1.0f;
    public final static int TILES_IN_WIDTH = 240; // aici nu ar fi o pb daca am nr de tileuri diferite intre nivele
    public final static int TILES_IN_HEIGHT = 39;
    public final static int TILES_SIZE = (int) (TILES_DEFAULT_SIZE * SCALE);
    public final static int GAME_WIDTH = TILES_SIZE * TILES_IN_WIDTH;
    public final static int GAME_HEIGHT = TILES_SIZE * TILES_IN_HEIGHT;



    private void initClasses() {
        dataBase = new DataBase();
        playing = new Playing(this);
        menu = new Menu(this);
        main_menu = new Main_Menu(this);
        options = new Options(this);
        levelComplete = new LevelComplete(this);
        death = new Death(this);
    }

    private void startGameLoop()
    {
        gameThread = new Thread(this);
        gameThread.start();
    }

    private Game() {
        initClasses();
        gamePanel= new GamePanel(this);
        gameWindow = new GameWindow(gamePanel);
        gamePanel.requestFocus();
        startGameLoop();
    }

    public static Game getInstance() {
        if (instance == null) {
            synchronized (Game.class) {
                if (instance == null) {
                    instance = new Game();
                }
            }
        }
        return instance;
    }

    public void update() {
        switch(Gamestate.state) {
            case MENU:
                menu.update();
                break;
            case PLAYING:
                playing.update();
                break;
            case MAIN_MENU:
                main_menu.update();
                break;
            case SAVE:
                playing.saveGame();
                Gamestate.state = Gamestate.PLAYING;
                break;
            case LOAD:
                playing.loadGame();
                Gamestate.state = Gamestate.PLAYING;
                break;
            case EXIT:
                System.exit(0);
                break;
            case OPTIONS:
                options.update();
                break;
            case QUIT:
                Gamestate.state = Gamestate.MAIN_MENU;
                break;
            case BACK:
                Gamestate.state = Gamestate.MENU;
                break;
            case LEVELCOMPLETE:
                if (levelComplete != null) {
                    levelComplete.update();
                }
                break;
            case DEATH:
                if (death != null) {
                    death.update();
                }
                break;
            default:
                break;
        }
    }
    public void render(Graphics g) {
        try {
            switch(Gamestate.state) {
                case MENU:
                    menu.draw(g);
                    break;
                case PLAYING:
                    playing.draw(g);
                    break;
                case MAIN_MENU:
                    main_menu.draw(g);
                    break;
                case OPTIONS:
                    options.draw(g);
                    break;
                case LEVELCOMPLETE:
                    levelComplete.draw(g);
                    break;
                case DEATH:
                    death.draw(g);
                    break;
                default:
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run()
    {
        double timePerFrame = 1000000000.0 / FPS_SET; // cat dureaza un frame
        double timePerUpdate = 1000000000.0 / UPS_SET; // timpul dintre 2 frameuri

        long previousTime = System.nanoTime();

        int frames = 0;
        int updates = 0;
        long lastCheck = System.currentTimeMillis();

        double deltaU = 0;
        double deltaF = 0;

        while(true) {
            long currentTime = System.nanoTime();

            deltaU +=(currentTime - previousTime)/timePerUpdate; // !!!
            deltaF +=(currentTime - previousTime)/timePerFrame;
            previousTime = currentTime;

            if(deltaU >= 1){
                update();
                updates++;
                deltaU--;
            }

            if(deltaF >= 1){
                gamePanel.repaint();
                frames++;
                deltaF--;
            }

            if(System.currentTimeMillis() - lastCheck >= 1000)
            {
                lastCheck = System.currentTimeMillis();
                System.out.println("FPS: " + frames);
                frames = 0;
            }
        }
    }

    public void windowFocusLost() {
        if(Gamestate.state == Gamestate.PLAYING)
            playing.getPlayer().resetDirBooleans();
    }
    public Menu getMenu(){
        return menu;
    }

    public Playing getPlaying(){
        return playing;
    }

    public Main_Menu getMain_menu(){return main_menu;}

    public DataBase getDataBase() {return dataBase;}

    public Options getOptions() {return options;}

    public LevelComplete getLevelComplete() {return levelComplete;}

    public Death getDeath() {return death;}
}