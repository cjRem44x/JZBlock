//  AUTHOR: cjRem44x //
//
// ============================================================================
// Window.java - Game Window Management
// ============================================================================
// Manages the main game window using Java Swing. This class extends JPanel
// to provide a custom drawing surface and wraps a JFrame for the window.
//
// Responsibilities:
//   - Window creation and configuration (size, title, icon)
//   - Custom painting via paintComponent() -> delegates to Render
//   - Screen refresh coordination with UI updates
//   - Mouse handling (hides cursor when clicked)
//   - Dependency injection hub for Engine, UI, Render, and Config
//
// Window Features:
//   - Fixed size: 1400x900 (from Config), minimum 800x800
//   - Non-resizable, always on top
//   - Custom dark background color
//   - Invisible cursor during gameplay
//
// Rendering Pipeline:
//   Window.ref() -> UI.update() + repaint() -> paintComponent() -> Render.update()
// ============================================================================
//
package game.graphics;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.security.SecureRandom;
import javax.swing.*;
//
import game.core.*;
import game.opt.*;
import game.ui.UI;

public class Window
extends JPanel
{
    // FIELDS //
    //
    private SecureRandom rand = new SecureRandom();  // Random generator (unused but available)
    private JFrame       f    = new JFrame();        // Main application window frame
    private Render       rend = null;                // Graphics renderer for game entities
    private Engine       e;                          // Game engine reference
    private UI           ui;                         // UI system reference
    private Config       set;                        // Configuration/settings reference
    //
    // Legacy labels (now managed by UI class)
    private JLabel p_health_lbl, go_lbl,
                   zkill_lbl, zwave_lbl,
                   reload_lbl, ZZZ_lbl,
                   stats_lbl;
    //
    private Timer rainbow_t;  // Timer for color cycling effects (unused)
  
    
    // ===========================
    // RENDERING METHODS
    // ===========================
    //
    // Override paintComponent to render game graphics
    // Called automatically by Swing when repaint() is invoked
    @Override public
    void paintComponent(Graphics g)
    {
        super.paintComponent(g);  // Clear background

        // Delegate rendering to Render class
        if (rend != null) rend.update(g);
        else System.out.println("RENDER is NULL");  // Debug warning
    }
    //
    // Inject render dependency
    public
    void rend(Render rend)
    {
        this.rend = rend;
    }
    //
    // Refresh the window - called every frame from game loop
    // Updates UI elements first, then triggers a repaint
    public
    void ref()
    {
        ui.update();    // Update HUD labels (health, kills, wave, etc.)
        this.repaint(); // Request Swing to repaint the window
    }
    //
    // Get current window content width (may differ from initial if resized)
    public
    int width()
    {
        return f.getContentPane().getWidth();
    }
    //
    // Get current window content height
    public
    int height()
    {
        return f.getContentPane().getHeight();
    }
    //
    // Set window title bar text
    public
    void title(String title)
    {
        f.setTitle(title);
    }
    //
    // Get window icon image from config
    public
    Image icon() {
        return set.get_icon().getImage();
    }

    // ===========================
    // WINDOW INITIALIZATION
    // ===========================
    //
    // Build and display the game window with all configurations
    public
    void build()
    {
        // Set window icon from resources
        f.setIconImage(icon());

        // Use null layout for absolute positioning of UI elements
        this.setLayout(null);

        // Add this JPanel as the window's content
        f.add(this);
        f.pack();
        f.setVisible(true);

        // Center window on screen
        f.setLocationRelativeTo(null);

        // Window constraints
        // f.setExtendedState(JFrame.MAXIMIZED_BOTH); // Uncomment for fullscreen
        f.setMinimumSize( new Dimension(800, 800) );
        f.setResizable(false);   // Fixed size window
        f.setAlwaysOnTop(true);  // Keep game visible

        // ===========================
        // CURSOR HIDING
        // ===========================
        // Create an invisible cursor to hide mouse during gameplay
        // Activates when user clicks on the game window
        BufferedImage cursimg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
            cursimg, new Point(0, 0),
            "blank cursor"
        );
        f.addMouseListener(
            new MouseAdapter() {
                @Override public
                void mouseClicked(MouseEvent e)
                {
                    f.getContentPane().setCursor(blankCursor);
                }
            }
        );

        // Set dark background color from config
        this.setBackground(set.window_bg);
    }
    //
    // Set window size (called before build)
    public
    void size(int x, int y)
    {
        this.setPreferredSize( new Dimension(x, y) );
    }
    //
    // Clear all UI components from window (used on restart)
    public
    void clear()
    {
        this.removeAll();
        this.setBackground(set.window_bg);
    }


    // ===========================
    // DEPENDENCY INJECTION
    // ===========================
    //
    // Get underlying JFrame (used by Input for adding listeners)
    public
    JFrame get()
    {
        return this.f;
    }
    //
    // Inject Engine dependency and sync screen color
    public
    void engine(Engine e)
    {
        this.e = e;
        e.screen_color = set.window_bg;  // Engine needs background color for dead zombie effect
    }
    //
    // Inject UI dependency and establish bidirectional links
    public
    void ui(UI ui)
    {
        this.ui = ui;
        ui.window(this);  // UI needs window reference to add labels
        ui.engine(e);     // UI needs engine for game state data
    }
    //
    // Inject Config dependency
    public
    void config(Config set)
    {
        this.set = set;
    }
}
