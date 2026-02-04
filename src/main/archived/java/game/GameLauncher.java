package game;

// ============================================================================
// GameLauncher.java - Game Initialization and Main Loop Controller
// ============================================================================
// Central coordinator that initializes all game systems and runs the main
// game loop. This class is called from MainMenu when the player clicks "Play".
//
// Responsibilities:
//   1. Create and wire together all game subsystems (dependency injection)
//   2. Start the game loop in a separate thread
//   3. Coordinate frame timing for consistent FPS
//   4. Call engine.update() and window.ref() each frame
//
// Game Loop Architecture:
//   - Runs in dedicated thread to not block UI
//   - Uses millisecond timing for FPS throttling
//   - Default: 60 FPS (configurable in Config)
//
// Subsystem Wiring (init_game):
//   Input -> Window, Engine, Config
//   Render -> Engine
//   Engine -> Player, Config
//   UI -> Config
//   Window -> Config, Render, Engine, UI
//
// Frame Timing:
//   - Tracks elapsed time since last frame
//   - Only refreshes window when enough time has passed (1000ms / FPS)
//   - Includes 1ms sleep to prevent CPU spinning
// ============================================================================

import game.core.*;
import game.graphics.*;
import game.dat.*;
import game.opt.*;
import game.input.*;
import game.ui.*;
///
import states.*;
//
import java.awt.Image;

public class GameLauncher {
    /// FIELDS ///
    ///
    // Singleton instances of all game subsystems
    static final Window    WIN     = new Window();   // Game window/display
    static final UI        UI      = new UI();       // HUD and UI elements
    static final Input     INP     = new Input();    // Keyboard input handler
    static final Render    REND    = new Render();   // Graphics renderer
    static final Config    CONFG   = new Config();   // Game configuration
    static final Engine    ENGINE  = new Engine();   // Core game logic
    static final Player    PLAYER  = new Player();   // Player entity
    ///
    // Frame timing variables for FPS control
    private static long fps_start = 0;   // Timestamp of current frame
    private static long fps_prev;         // Timestamp of previous frame
    private static long fps_steps = 0;   // Accumulated time since last render
    private static GameStates game_state;

    private Thread game_thrd;  // Dedicated game loop thread

    // ===========================
    // ACCESSORS FOR MAIN MENU
    // ===========================
    // Get game icon for window
    public
    Image icon() {
        return CONFG.get_icon().getImage();
    }

    // Get game title for window
    public
    String title()
    {
        return CONFG.GAME_TITLE;
    }

    // ===========================
    // GAME START
    // ===========================
    // Called by MainMenu when "Play Game" is clicked
    // Launches game loop in a dedicated thread
    public
    void start(GameStates game_state) {

        game_thrd = new Thread(() ->
        {
            this.game_state = game_state;
            init();   // Initialize all subsystems
            loop();   // Enter main game loop (infinite)
        });
        game_thrd.start();
    }

    /// PROGRAM DRIVER ///
    ///
    // Initialize all game subsystems
    private static
    void init()
    {
        init_game();
    }
    ///
    // Main game loop - runs forever until window closes
    private static
    void loop()
    {
        // Core game loop - runs continuously
        while (true)
        {
            loop_game();  // Update game state and render
            brake();      // Sleep to prevent CPU spinning
        }
    }


    /// GAME DRIVER ///
    ///
    // Wire up all game subsystems via dependency injection
    // Order matters - some systems depend on others being set first
    static
    void init_game()
    {
        // ===========================
        // OPTIONAL SETTINGS (uncomment to customize)
        // ===========================
        // CONFG.switch_controls = true;  // Swap WASD and arrow keys
        // CONFG.fps = 120;               // Higher frame rate

        // ===========================
        // DEPENDENCY INJECTION
        // ===========================
        // Wire Input to Window and Engine for keyboard handling
        INP.win(WIN);
        INP.engine(ENGINE);

        // Wire Render to Engine for access to game entities
        REND.engine(ENGINE);

        // Wire Engine to Player and Config
        ENGINE.player(PLAYER);
        ENGINE.config(CONFG);

        // Wire UI to Config for colors
        UI.config(CONFG);

        // Wire Window to Config and Render
        WIN.config(CONFG);
        WIN.rend(REND);

        // ===========================
        // WINDOW SETUP
        // ===========================
        WIN.size(CONFG.screen_width, CONFG.screen_height);
        WIN.build();          // Create and show window
        WIN.engine(ENGINE);   // Link engine after build
        INP.config(CONFG);    // Input needs config for control scheme

        // ===========================
        // ENGINE STARTUP
        // ===========================
        ENGINE.screen_width = WIN.width();
        ENGINE.screen_height = WIN.height();
        ENGINE.start();  // Initialize player, zombies, and start timers

        // Final window setup
        WIN.title(CONFG.GAME_TITLE);
        WIN.ui(UI);  // Link UI system
    }
    ///
    // Called every iteration of the game loop
    // Updates game state and renders frame (if enough time has passed)
    static
    void loop_game()
    {
        // ===========================
        // UPDATE GAME STATE
        // ===========================
        ENGINE.update();  // Process collisions, spawning, health, etc.

        // Keep engine in sync with window size (for bounds checking)
        ENGINE.screen_width = WIN.width();
        ENGINE.screen_height = WIN.height();

        // ===========================
        // FPS-CONTROLLED RENDERING
        // ===========================
        // Calculate time elapsed since last frame
        fps_prev = fps_start;
        fps_start = System.currentTimeMillis();
        fps_steps += (fps_start - fps_prev);

        // Only render when enough time has passed for target FPS
        // Formula: 1000ms / FPS = milliseconds per frame
        // Example: 1000 / 60 = 16.67ms per frame
        if (CONFG.fps > 0)
        {
            if (fps_steps >= (int)(1000.0/CONFG.fps))
            {
                WIN.ref();     // Refresh window (updates UI + repaints)
                fps_steps = 0; // Reset accumulator
            }
        } else {
            WIN.ref();  // Unlimited FPS mode - render every loop
        }
    }



    /// SYSTEM SLEEP ///
    ///
    // Brief sleep to prevent CPU spinning
    // Without this, the loop would consume 100% CPU
    private static
    void brake()
    {
        try
        {
            Thread.sleep(1);  // 1ms pause - enough to yield CPU
        } catch (InterruptedException ex)
        {
            ex.printStackTrace();
        }
    }

}
