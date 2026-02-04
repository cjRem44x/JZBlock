// AUTHOR: cjRem44x //
//
// ============================================================================
// Main.java - Application Entry Point for JZBlock
// ============================================================================
// This is the main entry point for the JZBlock zombie survival game.
// The game launches by displaying the main menu first, which then allows
// the player to start the actual game.
//
// Game Flow:
//   1. Main.main() is called
//   2. GameStates is set to MAIN_MENU
//   3. MainMenu is built and displayed
//   4. User clicks "Play Game" -> GameLauncher.start() is called
//   5. Game loop begins in GameLauncher
// ============================================================================
//
import game.*;
import menu.*;
//
import states.GameStates;

public class Main
{
    /// FIELDS ///
    ///
    // Current state of the game (MAIN_MENU, IN_GAME, or SETTINGS)
    static GameStates game_state;
    ///
    // Singleton instances for main menu and game launcher
    static final MainMenu     MAIN_MENU = new MainMenu();  // Handles the main menu UI
    static final GameLauncher GL        = new GameLauncher();  // Controls game initialization and loop


    /// ENTRY ///
    ///
    // Application entry point - initializes and displays the main menu
    public static
    void main(final String[] args)
    {
        // Launch game starting at the MAIN_MENU state
        game_state = GameStates.MAIN_MENU;
        MAIN_MENU.launcher(GL);  // Pass GameLauncher reference so menu can start the game
        MAIN_MENU.build(game_state);  // Build and display the main menu window
    }

} // END CLASS //
