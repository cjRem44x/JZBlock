package ui;

// Import necessary libraries for UI components and event handling.
import java.awt.*; // Import AWT library
import java.util.Random; // Import Random class
import javax.swing.*; // Import Swing library
import java.awt.event.*; // Import AWT event library
//
import core.Engine; // Import Engine class
import graphics.Window; // Import Window class
import opt.Settings; // Import Settings class

public class UI 
{
    // FIELDS //
    //
    private Random rand = new Random(); // Random generator for dynamic UI effects.
    private Settings set; // Game settings.
    private JLabel p_health_lbl, go_lbl, 
    zkill_lbl, zwave_lbl, 
    reload_lbl, ZZZ_lbl, 
    stats_lbl, pause_lbl,
    lzammo_lbl, lzrel_lbl; // UI labels.
    private JLabel[] keybinds_lbl; // Array for displaying keybinds.
    private Timer tutorial_timer; // Timer for tutorial-related functionality.
    private Window win; // Reference to the game window.
    private Engine e; // Reference to the game engine.
    int first_paint = 0;


    // ACCESS //
    //
    // Method to set the game window reference.
    public void window(Window win) {
        this.win = win; // Set the window
    }
    //
    // Method to set the game engine reference.
    public void engine(Engine e) {
        this.e = e; // Set the engine
    }
    //
    // Method to set the game settings reference.
    public void settings(Settings set) {
        this.set = set; // Set the settings
    }

    
    // DISPLAY UI //
    //
    // Method to update all UI elements.
    public void update() {
        p_health(); // Update player health display.
        z_kills();  // Update zombie kills display.
        zwave();    // Update current wave display.
        ZZZ();      // Update in-game currency display.
        stats();    // Update player stats display.
        pause();    // Handle pause state UI.
        lzammo();   // Update lazar ammo display.

        // Handle game-over and reload UI if the player is dead.
        if (!e.is_p_alive) {
            g_over(); // Show game over screen
            reload(); // Show reload prompt
            first_paint = 0;
        } else {
            new_game(); // Reset UI for a new game.
        }
    }
    //
    // Method to display player health.
    private void p_health() {
        //if (p_health_lbl != null) win.remove(p_health_lbl); // Remove old label if it exists.
        
        String txt = "  💚  "+Integer.toString(e.p.health);
        // Create a new label with the player's current health.
        if (first_paint < 1)
        {
            p_health_lbl = new JLabel(); // Create health label
            p_health_lbl.setFont(new Font("Arial Unicode MS", Font.PLAIN|Font.ITALIC, 50)); // Set font style.
            p_health_lbl.setOpaque(false); // Make the label background transparent.
            p_health_lbl.setForeground(set.p_health_color); // Set text color.
        }

        // Measure text size for precise positioning.
        FontMetrics metrics = p_health_lbl.getFontMetrics(p_health_lbl.getFont()); // Get font metrics
        int width = metrics.stringWidth(p_health_lbl.getText()+5); // Get string width
        int height = metrics.getHeight(); // Get font height
    
        // Set the label's bounds based on text size.
        p_health_lbl.setBounds(0, 0, width, height);
    
        p_health_lbl.setText(txt);
        if (first_paint < 1)
            win.add(p_health_lbl); // Add the label to the window.
        //first_paint++;
    }
    //
    // Method to display the "Game Over" title.
    private void g_over() {
        zwave(); // Update the wave display.
        go_lbl = new JLabel("🙁 Game Over 😭"); // Create a new label for "Game Over".
        go_lbl.setFont(new Font("Arial Unicode MS", Font.PLAIN, 100)); // Set font style.
        go_lbl.setOpaque(false); // Make the label background transparent.
        go_lbl.setForeground(set.go_color); // Set text color.
    
        // Measure text size for precise positioning.
        FontMetrics metrics = go_lbl.getFontMetrics(go_lbl.getFont());
        int width = metrics.stringWidth(go_lbl.getText());
        int height = metrics.getHeight();
    
        // Center the label on the screen.
        go_lbl.setBounds((int)((win.width()/2.00)-(width/2.00)), (int)((win.height()/2.00)-(height/2.00)), width, height);
        win.setBackground(set.go_bg); // Set the background color for the game-over screen.

        win.add(go_lbl); // Add the label to the window.
    }
    //
    // Method to display zombie kills.
    private void z_kills() {
        //if (zkill_lbl != null) win.remove(zkill_lbl); // Remove old label if it exists.

        if (first_paint < 1)
        {
            // Create a new label for zombie kills.
            zkill_lbl = new JLabel("K1llZ [  000  ]");
            zkill_lbl.setFont(new Font("Arial Unicode MS", Font.PLAIN, 50)); // Set font style.
            zkill_lbl.setOpaque(false); // Make the label background transparent.
            zkill_lbl.setForeground(set.zkill_color); // Set text color.
        }

        // Measure text size for precise positioning.
        FontMetrics metrics = zkill_lbl.getFontMetrics(zkill_lbl.getFont());
        int width = metrics.stringWidth(zkill_lbl.getText());
        int height = metrics.getHeight();

        // Update the label text with the current kill count.
        zkill_lbl.setText("☠ "+Integer.toString(e.z_kills)+" ");
    
        // Position the label at the top-right corner.
        zkill_lbl.setBounds((int)((win.width()-width*2.00)), (int)(height/2.00), width, height);

        if (first_paint < 1)
            win.add(zkill_lbl); // Add the label to the window.
    }
    //
    // Method to display the current wave.
    private void zwave() {
        if (zwave_lbl != null) win.remove(zwave_lbl); // Remove old label if it exists.

        // Create a new label for the current wave.
        zwave_lbl = new JLabel("W4VE [ 000 ]");
        zwave_lbl.setFont(new Font("Chiller", Font.PLAIN, 50)); // Set font style.
        zwave_lbl.setOpaque(false); // Make the label background transparent.

        // Generate a random color for the text.
        int r = rng(0,255);
        int g = rng(0, 255);
        int b = rng(0, 255); 
        zwave_lbl.setForeground( new Color(r,g,b) );
    
        // Measure text size for precise positioning.
        FontMetrics metrics = zwave_lbl.getFontMetrics(zwave_lbl.getFont());
        int width = metrics.stringWidth(zwave_lbl.getText());
        int height = metrics.getHeight();

        // Update the label text with the current wave number.
        zwave_lbl.setText("W4VE [ "+Integer.toString(e.z_wave)+" ]");
    
        // Center the label at the top of the screen.
        zwave_lbl.setBounds((int)((win.width()/2.00)-(width/2.00)), height, width, height);

        win.add(zwave_lbl); // Add the label to the window.
    }
    //
    // Method to display the reload prompt.
    private void reload() {
        reload_lbl = new JLabel("press [R] to start over"); // Create a new label for the reload prompt.
        reload_lbl.setFont(new Font("Monospace", Font.PLAIN|Font.ITALIC, 50)); // Set font style.
        reload_lbl.setOpaque(false); // Make the label background transparent.
        reload_lbl.setForeground(set.reload_color); // Set text color.
    
        // Measure text size for precise positioning.
        FontMetrics metrics = reload_lbl.getFontMetrics(reload_lbl.getFont());
        int width = metrics.stringWidth(reload_lbl.getText());
        int height = metrics.getHeight();
    
        // Center the label at the bottom of the screen.
        reload_lbl.setBounds((int)((win.width()/2.00)-(width/2.00)), (int)((win.height()*0.75)-(height/2.00)), width, height);

        win.add(reload_lbl); // Add the label to the window.
    }
    //
    // Method to display in-game currency.
    private void ZZZ() {
        if (first_paint < 1) {
            ZZZ_lbl = new JLabel();
            ZZZ_lbl.setFont(new Font("Chiller", Font.PLAIN | Font.ITALIC, 50));
            ZZZ_lbl.setOpaque(false);
            ZZZ_lbl.setForeground(set.ZZZ_color);
            win.add(ZZZ_lbl); // moved inside here since it's only done once
        }
    
        ZZZ_lbl.setText("$ 000000000  ");
        FontMetrics metrics = ZZZ_lbl.getFontMetrics(ZZZ_lbl.getFont());
        int width = metrics.stringWidth(ZZZ_lbl.getText());
        int height = metrics.getHeight();
    
        ZZZ_lbl.setText("$ " + Integer.toString(e.ZZZ));
        ZZZ_lbl.setBounds(win.width() - width, win.height() - height, width, height);
        ZZZ_lbl.setBorder(BorderFactory.createLineBorder(set.ZZZ_color, 2));
    
        ZZZ_lbl.revalidate();
        ZZZ_lbl.repaint();
    }    
    //
    // Method to display player stats.
    private void stats() {
        //if (stats_lbl != null) 
            //win.remove(stats_lbl); // Remove old label if it exists.

        if (first_paint < 1)
        {
            // Create a new label for player stats.
            String s = "  Power ⛈ ("+Integer.toString(e.lazar_power())+") | Upgrade $"+e.lazar_uprice + ",     press [U] to upgrade";
            stats_lbl = new JLabel(s);
            stats_lbl.setFont(new Font("Arial Unicode MS", Font.PLAIN, 30)); // Set font style.
            stats_lbl.setOpaque(false); // Make the label background transparent.
            stats_lbl.setForeground(set.stats_color); // Set text color.
        }

        // Measure text size for precise positioning.
        FontMetrics metrics = stats_lbl.getFontMetrics(stats_lbl.getFont());
        int width = metrics.stringWidth(stats_lbl.getText());
        int height = metrics.getHeight();
    
        // Position the label at the bottom-left corner.
        stats_lbl.setBounds(0, (int)((win.height()-height))-5, width, height);

        if (first_paint < 1)
            win.add(stats_lbl); // Add the label to the window.
    }
    //
    // Method to handle pause state UI.
    private void pause() {
        // Always clean up previous pause UI elements
        if (pause_lbl != null)
            win.remove(pause_lbl); // Remove old label if it exists.
        
        if (keybinds_lbl != null)
            for (JLabel lbl : keybinds_lbl)
                if (lbl != null)
                    win.remove(lbl); // Remove keybind labels if they exist.
    
        // Only show pause UI if the game is paused AND the player is alive
        if (e.is_paused && e.is_p_alive) 
        {
            // Create a new label for the pause state.
            String s = "🎮  Game Paused: Hit [ESC] to Resume";
            pause_lbl = new JLabel(s);
            pause_lbl.setFont(new Font("Arial Unicode MS", Font.PLAIN|Font.ITALIC, 75)); // Set font style.
            pause_lbl.setOpaque(false); // Make the label background transparent.
            pause_lbl.setForeground(set.pause_color); // Set text color.
        
            // Measure text size for precise positioning.
            FontMetrics metrics = pause_lbl.getFontMetrics(pause_lbl.getFont());
            int width = metrics.stringWidth(pause_lbl.getText());
            int height = metrics.getHeight();
        
            // Center the label on the screen.
            pause_lbl.setBounds((int)((win.width()/2.00)-(width/2.00)), (int)((win.height()/2.00)-(height/2.00)), width, height);
    
            win.add(pause_lbl); // Add the label to the window.
            
            // Display keybinds when paused.
            show_keybinds();
        }
    }
    //
    // Method to display lazar ammo.
    private void lzammo() {
        //if (lzammo_lbl != null) 
            //win.remove(lzammo_lbl); // Remove old label if it exists.

        if (first_paint < 1)
        {
            // Create a new label for lazar ammo.
            String s = "000000";
            lzammo_lbl = new JLabel(s);
            lzammo_lbl.setFont(new Font("Arial Unicode MS", Font.PLAIN|Font.ITALIC, 50)); // Set font style.
            lzammo_lbl.setOpaque(false); // Make the label background transparent.
            lzammo_lbl.setForeground(set.lzammo_color); // Set text color.
        }

        // Measure text size for precise positioning.
        FontMetrics metrics = lzammo_lbl.getFontMetrics(lzammo_lbl.getFont());
        int width = metrics.stringWidth(lzammo_lbl.getText());
        int height = metrics.getHeight();
   
        // Update the label text with the current ammo count.
        lzammo_lbl.setText("🔫  "+Integer.toString(e.lazar_mag));
        // Position the label at the bottom-right corner.
        lzammo_lbl.setBounds((int)(win.width()-(width))-50, (int)((win.height()-(height*2))), width+10, height);

        if (first_paint < 1)
            win.add(lzammo_lbl); // Add the label to the window.
        lazar_reload_prompt(); // Display reload prompt if necessary.
        
        /// NOTE: 
        /// This is here becuase lzammo() is
        /// called last in update.
        first_paint++;
    }
    //
    // Method to display reload prompt for lazar ammo.
    private 
    void lazar_reload_prompt() {
        if (lzrel_lbl != null) 
            win.remove(lzrel_lbl); // Remove old label if it exists.

        if (e.lazar_mag <= 0)
        {
        // Create a new label for the reload prompt.
        String s = "   Reload with [CTRL]   ";
        lzrel_lbl = new JLabel(s);
        lzrel_lbl.setFont(new Font("Jokerman", Font.PLAIN, 20)); // Set font style.
        lzrel_lbl.setOpaque(false); // Make the label background transparent.
        lzrel_lbl.setForeground(set.reload_prompt_color); // Set text color.
        int border_width = 2;
        lzrel_lbl.setBorder(BorderFactory.createLineBorder(set.reload_prompt_color, border_width));
    
        // Measure text size for precise positioning.
        FontMetrics metrics = lzrel_lbl.getFontMetrics(lzrel_lbl.getFont());
        int width = metrics.stringWidth(lzrel_lbl.getText())+(int)(border_width*2);
        int height = metrics.getHeight();

        // Position the label at the bottom-right corner.
        lzrel_lbl.setBounds((int)(win.width()-((int)(width*1.5))), (int)((win.height()-(height*6))), width, height);

        win.add(lzrel_lbl); // Add the label to the window.
        }
    }
    //
    // Method to display keybinds when paused.
    private void show_keybinds() {
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

        for (int i = 0; i < controls.length; i++)
        {
            keybinds_lbl[i] = new JLabel(controls[i]);
            keybinds_lbl[i].setFont(new Font("Arial Unicode MS", Font.PLAIN|Font.ITALIC, 25)); // Set font style.
            keybinds_lbl[i].setOpaque(false); // Make the label background transparent.
            keybinds_lbl[i].setForeground(set.keybinds_color); // Set text color.
            FontMetrics metrics = keybinds_lbl[i].getFontMetrics(keybinds_lbl[i].getFont());
        }

        FontMetrics metrics = keybinds_lbl[0].getFontMetrics(keybinds_lbl[0].getFont());
        int height = metrics.getHeight();
        int width = 150+metrics.stringWidth(keybinds_lbl[0].getText());

        for (int i = 0; i < controls.length; i++)
        {
            int h = height;
            int x = (int)(win.width()/2.00 - (int)(width/2.00));
            int y = 100+(int)(win.height()/2.00 - (int)(height/2.00));
            keybinds_lbl[i].setBounds(x, y+height*i, width, height);
            win.add(keybinds_lbl[i]); // Add each keybind label to the window.
        }
    }

    // Method to reset all UI elements
    public void resetUI() {
        // Reset the first_paint counter
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
        
        // Reset references to null
        p_health_lbl = zkill_lbl = zwave_lbl = ZZZ_lbl = null;
        stats_lbl = pause_lbl = lzammo_lbl = lzrel_lbl = null;
        go_lbl = reload_lbl = null;
        
        // Clear keybinds if they exist
        if (keybinds_lbl != null) {
            for (JLabel lbl : keybinds_lbl) {
                if (lbl != null) win.remove(lbl);
            }
            keybinds_lbl = null;
        }
        
        // Reset background
        win.setBackground(set.window_bg);
    }

    // Method to reset UI for a new game.
    public void new_game() {
        if (go_lbl != null && reload_lbl != null) {
            resetUI(); // Use the comprehensive reset method instead of just removing two labels
        }
        win.setBackground(set.window_bg); // Reset the background color.
    }
    

    // Method to generate random numbers within a range.
    protected int rng(int min, int max) {
        if (max-min > 0)
            return rand.nextInt(max-min)+min;
        else
            return min;
    }


}// END OF CLASS //