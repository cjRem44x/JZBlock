package states;

// ============================================================================
// GameStates.java - Game State Enumeration
// ============================================================================
// Defines the possible states the game can be in. Used for state management
// and transitions between different screens/modes.
//
// States:
//   - MAIN_MENU: The initial menu screen where player can start game or exit
//   - IN_GAME:   Active gameplay state where player fights zombies
//   - SETTINGS:  Configuration screen (not yet implemented in archived code)
// ============================================================================

public enum GameStates
{
    MAIN_MENU,  // Main menu screen
    IN_GAME,    // Active gameplay
    SETTINGS,   // Settings/options screen (placeholder)
}
