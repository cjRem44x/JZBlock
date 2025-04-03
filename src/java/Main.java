// AUTHOR: cjRem44x //
//
import core.*; // Import core package
import graphics.*; // Import graphics package
import dat.*; // Import dat package
import opt.*; // Import opt package
import input.*; // Import input package
import ui.*; // Import ui package

public class Main 
{
    // FIELDS //
    //
    static final Window    WIN     = new Window(); // Create a new window
    static final UI        UI      = new UI(); // Create a new UI
    static final Input     INP     = new Input(); // Create a new input
    static final Render    REND    = new Render(); // Create a new render
    static final Settings  SET     = new Settings(); // Create a new settings
    static final Engine    ENGINE  = new Engine(); // Create a new engine
    static final Player    PLAYER  = new Player(); // Create a new player


    // DRIVER //
    //
    // Main method
    public static 
    void main(final String[] args) 
    {
        // init game,
        // then loop game.
        init(); // Initialize the game
        loop(); // Start the game loop
    }


    // GAME //
    //
    // game init
    private static 
    void init() 
    {
        // optional (start) settings:
        //
        // SET.switch_controls = true;
        // SET.fps = 120;

        INP.win(WIN); // Set the window for input
        INP.engine(ENGINE); // Set the engine for input
        REND.engine(ENGINE); // Set the engine for render
        ENGINE.player(PLAYER); // Set the player for engine
        ENGINE.settings(SET); // Set the settings for engine
        UI.settings(SET); // Set the settings for UI
        WIN.settings(SET); // Set the settings for window
        WIN.rend(REND); // Set the render for window
        WIN.size(SET.screen_width, SET.screen_height); // Set the size of the window
        WIN.build(); // Build the window
        WIN.engine(ENGINE); // Set the engine for window
        INP.settings(SET); // Set the settings for input

        ENGINE.screen_width = WIN.width(); // Set the screen width for engine
        ENGINE.screen_height = WIN.height(); // Set the screen height for engine
        ENGINE.start(); // Start the engine

        WIN.title(SET.GAME_TITLE); // Set the title of the window
        WIN.ui(UI); // Set the UI for window
    }
    //
    // game loop
    private static 
    void loop() 
    {
        // vars to keep track of looping time
        long fps_start = 0, fps_prev, fps_steps = 0;
        
        // core game loop
        while (true) 
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
            
            if (SET.fps > 0) 
            {
                if (fps_steps >= (int)(1000.0/SET.fps)) 
                {
                    WIN.ref(); // Refresh the window
                    // System.out.println("winref @"+fps_steps);
                    fps_steps = 0;
                }
            } else {WIN.ref();}


            // let system brake to
            // prevent resource eating.  
            brake(); // Pause the thread
        }
    }


    // SLEEP //
    //
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


} // END CLASS //