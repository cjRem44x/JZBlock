// AUTHOR: cjRem44x //
//
// ============================================================================
// Config.java - Game Configuration and Color Theme Settings
// ============================================================================
// Central configuration class that holds all game settings and color values.
// This acts as a single source of truth for visual styling and game parameters.
//
// Categories:
//   1. WINDOW SETTINGS - Screen dimensions and frame rate
//   2. CONTROL SETTINGS - Input configuration options
//   3. ENTITY COLORS - Player, zombie, and projectile colors
//   4. UI COLORS - HUD elements, game over screen, pause menu colors
//
// Color Scheme:
//   - Player: Purple (#8900FF)
//   - Zombies: Bright green (#00FF01), Dark green when dead (#003701)
//   - Lazars: Pink (#FF009F) with light pink shadow (#FC92D4)
//   - Background: Dark blue-gray (#101024)
//   - UI Text: Various reds, oranges, and accent colors
//
// Modifying this file allows easy theming without touching game logic.
// ============================================================================
//
package game.opt;

import java.awt.*;
import javax.swing.*;

public class Config
{
    // ===========================
    // WINDOW SETTINGS
    // ===========================
    public final String GAME_TITLE = "JZBlock ϫⲍⲃⲗⲟⲕ";  // Window title (with stylized characters)
    public int     screen_width = 1400;   // Default window width in pixels
    public int     screen_height = 900;   // Default window height in pixels
    public int     fps = 60;              // Target frames per second for rendering

    // ===========================
    // CONTROL SETTINGS
    // ===========================
    public boolean switch_controls = false;  // When true: WASD shoots, Arrows move (inverted)

    // ===========================
    // ENTITY COLORS
    // ===========================
    // Player appearance
    public Color   p_color     = new Color(137,0,255);       // Player body: Purple

    // Zombie appearance
    public Color   z_color     = new Color(0, 255, 1);       // Zombie alive: Bright green
    public Color   z_dead_color = new Color(0, 55, 1);       // Zombie dead: Dark green

    // Lazar (projectile) appearance
    public Color   lazar_color = new Color(255, 0, 159);     // Lazar body: Hot pink
    public Color   lazar_shadow = new Color(252, 146, 212);  // Lazar shadow: Light pink
    public Color   lazar_blow_color = new Color(255, 255, 255);  // Lazar explosion: White

    // ===========================
    // BACKGROUND COLORS
    // ===========================
    public Color   window_bg = new Color(16, 16, 36);        // Game background: Dark blue-gray
    public Color   go_bg     = new Color(38, 10, 0);         // Game over background: Dark red-brown
    public Color   paused_bg_color = new Color(50, 50, 50);  // Paused background: Gray

    // ===========================
    // UI TEXT COLORS
    // ===========================
    public Color   p_health_color = new Color(255, 100 ,100);     // Health display: Light red
    public Color   go_color       = new Color(255, 0, 0);         // "Game Over" text: Red
    public Color   zkill_color    = new Color(255, 135, 0);       // Kill counter: Orange
    public Color   zwave_color    = new Color(255, 0, 0);         // Wave indicator: Red (randomized)
    public Color   reload_color   = new Color(255, 0, 0);         // "Press R to restart": Red
    public Color   ZZZ_color      = new Color(255, 0, 240);       // Currency display: Magenta
    public Color   stats_color    = new Color(255, 0, 0);         // Power/upgrade stats: Red
    public Color   pause_color    = new Color(255, 0, 0);         // Pause message: Red
    public Color   lzammo_color   = new Color(200, 255, 200);     // Ammo counter: Light green
    public Color   keybinds_color = new Color(255, 233, 0);       // Keybinds help: Yellow
    public Color   reload_prompt_color = new Color(200, 255, 200); // Reload prompt: Light green

    // ===========================
    // RESOURCE LOADING
    // ===========================
    // Returns the game window icon from the resources directory
    public
    ImageIcon get_icon() {
        final var icon_path = System.getProperty("user.dir")+"../../../res/img/icon.png";
        return new ImageIcon(icon_path);
    }
}
