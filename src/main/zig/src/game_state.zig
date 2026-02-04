// ============================================================================
// game_state.zig - Game State Enumeration
// ============================================================================
// Defines the possible states the game can be in for state machine management.
// ============================================================================

pub const GameState = enum {
    main_menu, // Initial menu screen
    settings, // Settings screen
    in_game, // Active gameplay
    paused, // Game paused
    game_over, // Player died
};
