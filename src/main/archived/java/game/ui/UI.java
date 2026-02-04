package game.ui;

// ============================================================================
// UI.java - Heads-Up Display (HUD) Manager
// ============================================================================
// Manages all on-screen UI elements including health, kills, wave counter,
// currency display, ammo counter, pause screen, and game over screen.
//
// UI Elements:
//   - Health Display (top-left): Shows current player health with heart emoji
//   - Kill Counter (top-right): Tracks total zombie kills with skull emoji
//   - Wave Display (top-center): Current wave number with random colors
//   - Currency Display (bottom-right): Player's ZZZ currency for upgrades
//   - Stats Display (bottom-left): Weapon power and upgrade cost
//   - Ammo Counter (bottom-right): Current lazar ammo with gun emoji
//   - Reload Prompt: Appears when ammo is empty
//   - Pause Screen: Shows when ESC pressed with keybinds help
//   - Game Over Screen: Shows when player dies with restart prompt
//
// Optimization:
//   - Uses first_paint flag to avoid recreating labels every frame
//   - Labels created once on first update(), then just setText() on subsequent frames
//   - Only pause/wave/game-over labels are recreated each time (by design)
//
// Called from Window.ref() every frame to update all displays.
// ============================================================================

import java.awt.*;
import java.util.Random;
import javax.swing.*;
import java.awt.event.*;
//
import game.core.Engine;
import game.graphics.Window;
import game.opt.Config;

public class UI
{
    // ===========================
    // FIELDS
    // ===========================
    private Random rand = new Random();  // For random wave label colors
    private Config set;                   // Configuration for colors
    private Window win;                   // Window to add labels to
    private Engine e;                     // Engine for game state data

    // ===========================
    // UI LABELS
    // ===========================
    // HUD elements (persistent during gameplay)
    private JLabel p_health_lbl;     // Player health display
    private JLabel zkill_lbl;        // Zombie kill counter
    private JLabel zwave_lbl;        // Current wave number
    private JLabel ZZZ_lbl;          // Currency display
    private JLabel stats_lbl;        // Weapon power and upgrade info
    private JLabel lzammo_lbl;       // Ammo counter
    private JLabel lzrel_lbl;        // Reload prompt (when out of ammo)
    private JLabel pause_lbl;        // Pause message
    private JLabel[] keybinds_lbl;   // Keybindings help (shown when paused)

    // Game over elements
    private JLabel go_lbl;           // "Game Over" text
    private JLabel reload_lbl;       // "Press R to restart" prompt

    private Timer tutorial_timer;    // Unused timer (legacy)

    // Optimization flag - prevents recreating labels every frame
    // Labels are created on first update (first_paint < 1), then just updated
    int first_paint = 0;


    // ===========================
    // DEPENDENCY INJECTION
    // ===========================
    // Set window reference (needed to add labels)
    public void window(Window win) {
        this.win = win;
    }
    //
    // Set engine reference (needed for game state data)
    public void engine(Engine e) {
        this.e = e;
    }
    //
    // Set config reference (needed for colors)
    public void config(Config set) {
        this.set = set;
    }


    // ===========================
    // MAIN UPDATE METHOD
    // ===========================
    // Called every frame from Window.ref() to refresh all UI elements
    public void update() {
        // Update all HUD elements with current game state
        p_health();  // Player health (top-left)
        z_kills();   // Kill counter (top-right)
        zwave();     // Wave number (top-center)
        ZZZ();       // Currency (bottom-right)
        stats();     // Weapon stats (bottom-left)
        pause();     // Pause screen (if paused)
        lzammo();    // Ammo counter (bottom-right)

        // Handle game over state
        if (!e.is_p_alive) {
            g_over();        // Show "Game Over" message
            reload();        // Show "Press R to restart"
            first_paint = 0; // Reset so labels get recreated on restart
        } else {
            new_game();      // Clean up game over UI if alive
        }
    }
    // ===========================
    // HEALTH DISPLAY
    // ===========================
    // Shows player health with heart emoji in top-left corner
    // Format: "💚 100"
    private void p_health() {
        String txt = "  💚  "+Integer.toString(e.p.health);

        // Create label only on first frame
        if (first_paint < 1)
        {
            p_health_lbl = new JLabel();
            p_health_lbl.setFont(new Font("Arial Unicode MS", Font.PLAIN|Font.ITALIC, 50));
            p_health_lbl.setOpaque(false);
            p_health_lbl.setForeground(set.p_health_color);
        }

        // Calculate label size based on text
        FontMetrics metrics = p_health_lbl.getFontMetrics(p_health_lbl.getFont());
        int width = metrics.stringWidth(p_health_lbl.getText()+5);
        int height = metrics.getHeight();

        // Position at top-left
        p_health_lbl.setBounds(0, 0, width, height);
        p_health_lbl.setText(txt);

        // Add to window only on first frame
        if (first_paint < 1)
            win.add(p_health_lbl);
    }
    // ===========================
    // GAME OVER DISPLAY
    // ===========================
    // Shows centered "Game Over" message when player dies
    // Also changes background to dark red
    private
    void g_over()
    {
        zwave();  // Keep wave display visible

        // Create game over label with sad emojis
        go_lbl = new JLabel("🙁 Game Over 😭");
        go_lbl.setFont(new Font("Arial Unicode MS", Font.PLAIN, 100));
        go_lbl.setOpaque(false);
        go_lbl.setForeground(set.go_color);

        // Calculate size and center on screen
        FontMetrics metrics = go_lbl.getFontMetrics(go_lbl.getFont());
        int width = metrics.stringWidth(go_lbl.getText());
        int height = metrics.getHeight();
        go_lbl.setBounds((int)((win.width()/2.00)-(width/2.00)), (int)((win.height()/2.00)-(height/2.00)), width, height);

        // Change background to game-over color (dark red)
        win.setBackground(set.go_bg);

        win.add(go_lbl);
    }
    // ===========================
    // KILL COUNTER DISPLAY
    // ===========================
    // Shows total zombie kills with skull emoji in top-right
    // Format: "☠ 42"
    private
    void z_kills()
    {
        // Create label only on first frame
        if (first_paint < 1)
        {
            zkill_lbl = new JLabel("K1llZ [  000  ]");  // Placeholder for sizing
            zkill_lbl.setFont(new Font("Arial Unicode MS", Font.PLAIN, 50));
            zkill_lbl.setOpaque(false);
            zkill_lbl.setForeground(set.zkill_color);  // Orange
        }

        // Calculate position
        FontMetrics metrics = zkill_lbl.getFontMetrics(zkill_lbl.getFont());
        int width = metrics.stringWidth(zkill_lbl.getText());
        int height = metrics.getHeight();

        // Update text with current kill count
        zkill_lbl.setText("☠ "+Integer.toString(e.z_kills)+" ");

        // Position at top-right
        zkill_lbl.setBounds((int)((win.width()-width*2.00)), (int)(height/2.00), width, height);

        if (first_paint < 1)
            win.add(zkill_lbl);
    }
    // ===========================
    // WAVE DISPLAY
    // ===========================
    // Shows current wave number at top-center
    // Uses random colors that change each frame for visual flair
    // Format: "W4VE [ 3 ]"
    private
    void zwave()
    {
        // Always recreate wave label (random colors each frame)
        if (zwave_lbl != null) win.remove(zwave_lbl);

        zwave_lbl = new JLabel("W4VE [ 000 ]");
        zwave_lbl.setFont(new Font("Chiller", Font.PLAIN, 50));
        zwave_lbl.setOpaque(false);

        // Random RGB for flashy effect
        int r = rng(0, 255);
        int g = rng(0, 255);
        int b = rng(0, 255);
        zwave_lbl.setForeground(new Color(r, g, b));

        // Calculate size and position
        FontMetrics metrics = zwave_lbl.getFontMetrics(zwave_lbl.getFont());
        int width = metrics.stringWidth(zwave_lbl.getText());
        int height = metrics.getHeight();

        // Update with current wave number
        zwave_lbl.setText("W4VE [ "+Integer.toString(e.z_wave)+" ]");

        // Center at top of screen
        zwave_lbl.setBounds((int)((win.width()/2.00)-(width/2.00)), height, width, height);

        win.add(zwave_lbl);
    }
    // ===========================
    // RESTART PROMPT
    // ===========================
    // Shows "press [R] to start over" below game over message
    private
    void reload()
    {
        reload_lbl = new JLabel("press [R] to start over");
        reload_lbl.setFont(new Font("Monospace", Font.PLAIN|Font.ITALIC, 50));
        reload_lbl.setOpaque(false);
        reload_lbl.setForeground(set.reload_color);

        FontMetrics metrics = reload_lbl.getFontMetrics(reload_lbl.getFont());
        int width = metrics.stringWidth(reload_lbl.getText());
        int height = metrics.getHeight();

        // Position below center (at 75% screen height)
        reload_lbl.setBounds((int)((win.width()/2.00)-(width/2.00)), (int)((win.height()*0.75)-(height/2.00)), width, height);

        win.add(reload_lbl);
    }
    // ===========================
    // CURRENCY DISPLAY
    // ===========================
    // Shows player's ZZZ currency at bottom-right with border
    // Currency earned by killing zombies ($44 per kill)
    // Used to purchase weapon upgrades
    // Format: "$ 1234"
    private
    void ZZZ()
    {
        // Create label only on first frame
        if (first_paint < 1)
        {
            ZZZ_lbl = new JLabel();
            ZZZ_lbl.setFont(new Font("Chiller", Font.PLAIN | Font.ITALIC, 50));
            ZZZ_lbl.setOpaque(false);
            ZZZ_lbl.setForeground(set.ZZZ_color);  // Magenta
            win.add(ZZZ_lbl);
        }

        // Use placeholder text to calculate max width
        ZZZ_lbl.setText("$ 000000000  ");
        FontMetrics metrics = ZZZ_lbl.getFontMetrics(ZZZ_lbl.getFont());
        int width = metrics.stringWidth(ZZZ_lbl.getText());
        int height = metrics.getHeight();

        // Update with actual currency value
        ZZZ_lbl.setText("$ " + Integer.toString(e.ZZZ));
        ZZZ_lbl.setBounds(win.width() - width, win.height() - height, width, height);
        ZZZ_lbl.setBorder(BorderFactory.createLineBorder(set.ZZZ_color, 2));

        ZZZ_lbl.revalidate();
        ZZZ_lbl.repaint();
    }    
    // ===========================
    // WEAPON STATS DISPLAY
    // ===========================
    // Shows weapon power and upgrade cost at bottom-left
    // Format: "Power ⛈ (25) | Upgrade $400, press [U] to upgrade"
    private
    void stats()
    {
        String s = "  Power ⛈ ("+Integer.toString(e.lazar_power())+") | Upgrade $"+e.lazar_uprice + ",     press [U] to upgrade";

        // Create label only on first frame
        if (first_paint < 1)
        {
            stats_lbl = new JLabel(s);
            stats_lbl.setFont(new Font("Arial Unicode MS", Font.PLAIN, 30));
            stats_lbl.setOpaque(false);
            stats_lbl.setForeground(set.stats_color);  // Red
        }

        FontMetrics metrics = stats_lbl.getFontMetrics(stats_lbl.getFont());
        int width = metrics.stringWidth(stats_lbl.getText());
        int height = metrics.getHeight();

        stats_lbl.setText(s);

        // Position at bottom-left
        stats_lbl.setBounds(0, (int)((win.height()-height))-5, width, height);

        if (first_paint < 1)
            win.add(stats_lbl);
    }
    // ===========================
    // PAUSE SCREEN
    // ===========================
    // Shows pause message and keybinds help when ESC pressed
    // Only displays when paused AND player is alive
    private
    void pause()
    {
        // Always clean up previous pause UI (removed every frame)
        if (pause_lbl != null)
            win.remove(pause_lbl);

        if (keybinds_lbl != null)
            for (JLabel lbl : keybinds_lbl)
                if (lbl != null)
                    win.remove(lbl);

        // Only show pause UI if paused AND alive (not when dead)
        if (e.is_paused && e.is_p_alive)
        {
            String s = "🎮  Game Paused: Hit [ESC] to Resume";
            pause_lbl = new JLabel(s);
            pause_lbl.setFont(new Font("Arial Unicode MS", Font.PLAIN|Font.ITALIC, 75));
            pause_lbl.setOpaque(false);
            pause_lbl.setForeground(set.pause_color);

            FontMetrics metrics = pause_lbl.getFontMetrics(pause_lbl.getFont());
            int width = metrics.stringWidth(pause_lbl.getText());
            int height = metrics.getHeight();

            // Center on screen
            pause_lbl.setBounds((int)((win.width()/2.00)-(width/2.00)), (int)((win.height()/2.00)-(height/2.00)), width, height);

            win.add(pause_lbl);

            // Show keybindings help below pause message
            show_keybinds();
        }
    }
    // ===========================
    // AMMO COUNTER
    // ===========================
    // Shows current lazar ammo with gun emoji at bottom-right
    // Format: "🔫 8"
    private
    void lzammo()
    {
        // Create label only on first frame
        if (first_paint < 1)
        {
            String s = "000000";  // Placeholder for sizing
            lzammo_lbl = new JLabel(s);
            lzammo_lbl.setFont(new Font("Arial Unicode MS", Font.PLAIN|Font.ITALIC, 50));
            lzammo_lbl.setOpaque(false);
            lzammo_lbl.setForeground(set.lzammo_color);  // Light green
        }

        FontMetrics metrics = lzammo_lbl.getFontMetrics(lzammo_lbl.getFont());
        int width = metrics.stringWidth(lzammo_lbl.getText());
        int height = metrics.getHeight();

        // Update with current ammo count
        lzammo_lbl.setText("🔫  "+Integer.toString(e.lazar_mag));

        // Position above currency display at bottom-right
        lzammo_lbl.setBounds((int)(win.width()-(width))-50, (int)((win.height()-(height*2))), width+10, height);

        if (first_paint < 1)
            win.add(lzammo_lbl);

        // Show reload prompt if out of ammo
        lazar_reload_prompt();

        // NOTE: first_paint is incremented here because lzammo() is called last in update()
        first_paint++;
    }
    // ===========================
    // RELOAD PROMPT
    // ===========================
    // Shows "Reload with [CTRL]" when ammo is empty
    // Disappears when ammo is reloaded
    private
    void lazar_reload_prompt()
    {
        // Always remove previous prompt
        if (lzrel_lbl != null)
            win.remove(lzrel_lbl);

        // Only show when out of ammo
        if (e.lazar_mag <= 0)
        {
            String s = "   Reload with [CTRL]   ";
            lzrel_lbl = new JLabel(s);
            lzrel_lbl.setFont(new Font("Jokerman", Font.PLAIN, 20));
            lzrel_lbl.setOpaque(false);
            lzrel_lbl.setForeground(set.reload_prompt_color);

            // Add border for visibility
            int border_width = 2;
            lzrel_lbl.setBorder(BorderFactory.createLineBorder(set.reload_prompt_color, border_width));

            FontMetrics metrics = lzrel_lbl.getFontMetrics(lzrel_lbl.getFont());
            int width = metrics.stringWidth(lzrel_lbl.getText())+(int)(border_width*2);
            int height = metrics.getHeight();

            // Position above ammo counter
            lzrel_lbl.setBounds((int)(win.width()-((int)(width*1.5))), (int)((win.height()-(height*6))), width, height);

            win.add(lzrel_lbl);
        }
    }
    // ===========================
    // KEYBINDINGS HELP
    // ===========================
    // Shows list of all game controls when paused
    // Displayed below the pause message
    private
    void show_keybinds()
    {
        // All game controls with descriptions
        final String[] controls =
        {
            "🖮 KEYBINDINGS ⌨",
            "_________________",
            "1. Move:.................[W][A][S][D]",
            "2. Shoot:.................Arrow Keys ",
            "3. Reload:................[CTRL]",
            "4. Upgrade Weapon:........[U]",
            "5. Speed Boost:...........[SPACE]",
            "6. Pause/Resume:..........[ESC]",
            "7. Restart When Dead:.....[R]",
        };

        keybinds_lbl = new JLabel[controls.length];

        // Create label for each control line
        for (int i = 0; i < controls.length; i++)
        {
            keybinds_lbl[i] = new JLabel(controls[i]);
            keybinds_lbl[i].setFont(new Font("Arial Unicode MS", Font.PLAIN|Font.ITALIC, 25));
            keybinds_lbl[i].setOpaque(false);
            keybinds_lbl[i].setForeground(set.keybinds_color);  // Yellow
            FontMetrics metrics = keybinds_lbl[i].getFontMetrics(keybinds_lbl[i].getFont());
        }

        // Calculate positioning
        FontMetrics metrics = keybinds_lbl[0].getFontMetrics(keybinds_lbl[0].getFont());
        int height = metrics.getHeight();
        int width = 150+metrics.stringWidth(keybinds_lbl[0].getText());

        // Position each line vertically stacked below center
        for (int i = 0; i < controls.length; i++)
        {
            int x = (int)(win.width()/2.00 - (int)(width/2.00));
            int y = 100+(int)(win.height()/2.00 - (int)(height/2.00));
            keybinds_lbl[i].setBounds(x, y+height*i, width, height);
            win.add(keybinds_lbl[i]);
        }
    }

    // ===========================
    // RESET METHODS
    // ===========================

    // Complete UI reset - removes all labels and resets state
    // Called when restarting game after death
    public
    void resetUI()
    {
        // Reset optimization flag so labels get recreated
        first_paint = 0;

        // Remove all existing labels
        if (p_health_lbl != null) win.remove(p_health_lbl);
        if (zkill_lbl != null) win.remove(zkill_lbl);
        if (zwave_lbl != null) win.remove(zwave_lbl);
        if (ZZZ_lbl != null) win.remove(ZZZ_lbl);
        if (stats_lbl != null) win.remove(stats_lbl);
        if (pause_lbl != null) win.remove(pause_lbl);
        if (lzammo_lbl != null) win.remove(lzammo_lbl);
        if (lzrel_lbl != null) win.remove(lzrel_lbl);
        if (go_lbl != null) win.remove(go_lbl);
        if (reload_lbl != null) win.remove(reload_lbl);

        // Null out references
        p_health_lbl = zkill_lbl = zwave_lbl = ZZZ_lbl = null;
        stats_lbl = pause_lbl = lzammo_lbl = lzrel_lbl = null;
        go_lbl = reload_lbl = null;

        // Remove keybinds help if showing
        if (keybinds_lbl != null)
        {
            for (JLabel lbl : keybinds_lbl)
            {
                if (lbl != null) win.remove(lbl);
            }
            keybinds_lbl = null;
        }

        // Reset background to normal (remove game-over red)
        win.setBackground(set.window_bg);
    }

    // Called each frame when player is alive
    // Cleans up game-over UI if transitioning from dead to alive (restart)
    public
    void new_game()
    {
        // If game-over labels exist, player just restarted - do full reset
        if (go_lbl != null && reload_lbl != null)
        {
            resetUI();
        }
        win.setBackground(set.window_bg);
    }


    // ===========================
    // UTILITY
    // ===========================
    // Generate random int in range [min, max)
    protected
    int rng(int min, int max)
    {
        if (max-min > 0)
            return rand.nextInt(max-min)+min;
        else
            return min;
    }


}// END OF CLASS //
