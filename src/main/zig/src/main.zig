// ============================================================================
// main.zig - JZBlock Game Entry Point
// ============================================================================
// Main game loop with state management for menu, gameplay, pause, and game over.
// ============================================================================

const std = @import("std");
const rl = @import("raylib");
const config = @import("config.zig");
const GameState = @import("game_state.zig").GameState;
const Engine = @import("engine.zig").Engine;
const Menu = @import("menu.zig").Menu;
const Settings = @import("menu.zig").Settings;
const UI = @import("ui.zig").UI;
const drawBackgroundEffects = @import("ui.zig").drawBackgroundEffects;

pub fn main() !void {
    // Initialize Raylib window
    rl.initWindow(config.SCREEN_WIDTH, config.SCREEN_HEIGHT, config.GAME_TITLE);
    defer rl.closeWindow();
    rl.setTargetFPS(config.FPS);

    // Set window icon
    if (rl.loadImage("../../../res/img/icon.png")) |icon| {
        var icon_rgba = icon;
        rl.imageFormat(&icon_rgba, .uncompressed_r8g8b8a8);
        rl.setWindowIcon(icon_rgba);
        rl.unloadImage(icon_rgba);
    } else |_| {}

    // Disable ESC closing the window (we use ESC for pause)
    rl.setExitKey(.null);

    // Initialize allocator
    var gpa = std.heap.GeneralPurposeAllocator(.{}){};
    defer _ = gpa.deinit();
    const allocator = gpa.allocator();

    // Initialize game state
    var game_state: GameState = .main_menu;

    // Initialize menu
    var menu = Menu.init();
    defer menu.deinit();

    // Initialize settings screen
    var settings = Settings.init();

    // Initialize UI
    var ui = UI.init();

    // Engine will be initialized when game starts
    var engine: ?Engine = null;
    defer {
        if (engine) |*e| {
            e.deinit();
        }
    }

    // Main game loop
    while (!rl.windowShouldClose()) {
        const delta = rl.getFrameTime();

        // Update UI animation timer
        ui.update(delta);

        // Update based on current state
        switch (game_state) {
            .main_menu => {
                if (!menu.update(&game_state)) {
                    break; // Quit button clicked
                }

                // Check if transitioning to game
                if (game_state == .in_game) {
                    // Initialize engine when starting game
                    engine = try Engine.init(allocator, &game_state);
                }
            },

            .settings => {
                settings.update(&game_state);
            },

            .in_game => {
                if (engine) |*e| {
                    try e.update(delta);
                }
            },

            .paused => {
                // Handle pause menu buttons (Resume, Main Menu)
                ui.updatePaused(&game_state);
            },

            .game_over => {
                // Handle restart
                if (rl.isKeyPressed(.r)) {
                    if (engine) |*e| {
                        try e.restart();
                    }
                }
            },
        }

        // Drawing
        rl.beginDrawing();
        defer rl.endDrawing();

        switch (game_state) {
            .main_menu => {
                menu.draw();
            },

            .settings => {
                settings.draw();
            },

            .in_game => {
                rl.clearBackground(config.BG_COLOR);
                drawBackgroundEffects(ui.anim_time);

                if (engine) |*e| {
                    e.draw();
                    ui.drawHUD(e);
                }
            },

            .paused => {
                rl.clearBackground(config.BG_COLOR);
                drawBackgroundEffects(ui.anim_time);

                if (engine) |*e| {
                    e.draw();
                }

                ui.drawPaused();
            },

            .game_over => {
                if (engine) |*e| {
                    ui.drawGameOver(e);
                }
            },
        }
    }
}
