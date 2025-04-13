// AUTHOR: cjRem44x //
//
package game.opt;

import java.awt.*;

public class Config 
{
    // FIELDS //
    //
    public final String GAME_TITLE = "JZBlock ϫⲍⲃⲗⲟⲕ"; // The title of the game window
    //
    public int     screen_width = 1350,  // Width of the game screen
                   screen_height = 820, // Height of the game screen
                   fps = 60;              // Frames per second
    public boolean switch_controls = false; // Flag to switch control scheme
    public Color   p_color     = new Color(137,0,255), // Player color
                   z_color     = new Color(0, 255, 1),   // Zombie color
                   z_dead_color = new Color(0, 55, 1),  // Zombie dead color
                   lazar_color = new Color(255, 0, 159), // Lazar color
                   lazar_shadow = new Color(252, 146, 212), // Lazar shadow color
                   lazar_blow_color = new Color(255, 255, 255); // Lazar explosion color
    public Color   window_bg = new Color(16, 16, 36); // Window background color
    public Color  go_bg          = new Color(38, 10, 0),   // Game over background color
                  p_health_color = new Color(255, 100 ,100), // Player health color
                  go_color       = new Color(255, 0, 0),     // Game over text color
                  zkill_color    = new Color(255, 135, 0),   // Zombie kill color
                  zwave_color    = new Color(255, 0, 0),     // Zombie wave color
                  reload_color   = new Color(255, 0, 0),    // Reload text color
                  ZZZ_color      = new Color(255, 0, 240),   // ZZZ color
                  stats_color    = new Color(255, 0, 0),    // Stats color
                  pause_color    = new Color(255, 0, 0),    // Pause color
                  lzammo_color   = new Color(200, 255, 200),   // Lazar ammo color
                  keybinds_color = new Color(255, 233, 0), // Keybinds color
                  reload_prompt_color = new Color(200, 255, 200), // Reload prompt color
                  paused_bg_color = new Color(50, 50, 50); // Game over background color
}
