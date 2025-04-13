package game;

import game.core.*; // Import core package
import game.graphics.*; // Import graphics package
import game.dat.*; // Import dat package
import game.opt.*; // Import opt package
import game.input.*; // Import input package
import game.ui.*; // Import ui package
///
import states.*;

public class GameLauncher {
    /// FIELDS ///   
    ///
    static final Window    WIN     = new Window(); // Create a new window
    static final UI        UI      = new UI(); // Create a new UI
    static final Input     INP     = new Input(); // Create a new input
    static final Render    REND    = new Render(); // Create a new render
    static final Config    CONFG     = new Config(); // Create a new settings
    static final Engine    ENGINE  = new Engine(); // Create a new engine
    static final Player    PLAYER  = new Player(); // Create a new Player
    ///
    // vars to keep track of looping time
    private static long fps_start = 0, fps_prev, fps_steps = 0;
    private static GameStates game_state;

    private Thread game_thrd;

    public 
    void start(GameStates game_state) {
        
        game_thrd = new Thread(() ->
        {
            this.game_state = game_state;
            init();
            loop();
        });
        game_thrd.start();
    }

    /// PROGRAM DRIVER ///
    /// 
    private static 
    void init() 
    {
        init_game();
    }
    ///
    private static 
    void loop() 
    {
        
        // core game loop
        while (true) 
        {
            loop_game();
            // let system brake to
            // prevent resource eating.  
            brake(); // Pause the thread
        }
    }


    /// GAME DRIVER ///   
    ///
    static 
    void init_game() 
    {
        // optional (start) settings:
        //
        // CONFG.switch_controls = true;
        // CONFG.fps = 120;

        INP.win(WIN); // Set the window for input
        INP.engine(ENGINE); // Set the engine for input
        REND.engine(ENGINE); // Set the engine for render
        ENGINE.player(PLAYER); // Set the player for engine
        ENGINE.config(CONFG); // Set the config for engine
        UI.config(CONFG); // Set the config for UI
        WIN.config(CONFG); // Set the config for window
        WIN.rend(REND); // Set the render for window
        WIN.size(CONFG.screen_width, CONFG.screen_height); // Set the size of the window
        WIN.build(); // Build the window
        WIN.engine(ENGINE); // Set the engine for window
        INP.config(CONFG); // Set the config for input

        ENGINE.screen_width = WIN.width(); // Set the screen width for engine
        ENGINE.screen_height = WIN.height(); // Set the screen height for engine
        ENGINE.start(); // Start the engine

        WIN.title(CONFG.GAME_TITLE); // Set the title of the window
        WIN.ui(UI); // Set the UI for window
    }
    ///
    static 
    void loop_game() 
    {
        // update engine
        ENGINE.update(); // Update the engine
        ENGINE.screen_width = WIN.width(); // Update the screen width for engine
        ENGINE.screen_height = WIN.height(); // Update the screen height for engine

        // window refresh;
        // loop-time calculation
        fps_prev = fps_start;
        fps_start = System.currentTimeMillis();
        fps_steps += (fps_start-fps_prev);
        
        if (CONFG.fps > 0) 
        {
            if (fps_steps >= (int)(1000.0/CONFG.fps)) 
            {
                WIN.ref(); // Refresh the window
                // System.out.println("winref @"+fps_steps);
                fps_steps = 0;
            }
        } else {WIN.ref();}
    }



    /// SYSTEM SLEEP ///
    ///
    private static 
    void brake() 
    {
        try 
        {
            Thread.sleep(1); // Pause the thread for 1 millisecond
            // System.out.println("sleeping");
        } catch (InterruptedException ex) 
        {
            ex.printStackTrace();
        }
    }



}
