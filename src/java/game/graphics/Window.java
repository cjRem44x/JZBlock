//  AUTHOR: cjRem44x //
//
package game.graphics;

import java.awt.*; // Import AWT library
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.security.SecureRandom; // Import SecureRandom
import javax.swing.*; // Import Swing library
//
import game.core.*; // Import core package
import game.opt.*; // Import Settings class
import game.ui.UI; // Import UI class

public class Window 
extends JPanel 
{
    // FIELDS //
    //
    private SecureRandom rand = new SecureRandom(); // Create a new SecureRandom instance for random number generation
    private JFrame       f    = new JFrame(); // Create a new JFrame to represent the application window
    private Render       rend = null; // Render object for handling graphics rendering
    private Engine       e; // Engine object for game logic
    private UI           ui; // UI object for user interface management
    private Config     set; // Settings object for application configuration
    //
    // displaying stats
    private JLabel p_health_lbl, go_lbl, 
                   zkill_lbl, zwave_lbl, 
                   reload_lbl, ZZZ_lbl, 
                   stats_lbl; // Labels for displaying various stats
    //
    // timer to cycle through round_lbl colors
    private Timer rainbow_t; // Timer for cycling through colors
  
    
    // RENDER //
    //
    // paint
    @Override public 
    void paintComponent(Graphics g) 
    {
        super.paintComponent(g);

        // render graphics
        if (rend != null) rend.update(g); // Update graphics using the Render object
        else System.out.println("RENDER is NULL"); // Log if Render object is null
    }
    //
    // add render
    public 
    void rend(Render rend) 
    {
        this.rend = rend; // Set the Render object
    }
    //
    // window refresh
    public 
    void ref() 
    {
        ui.update(); // Update the UI
        this.repaint(); // Refresh the window
    }
    //
    // access to screen width
    public 
    int width() 
    {
        return f.getContentPane().getWidth(); // Get the width of the window's content pane
    }
    //
    // access to screen height
    public 
    int height() 
    {
        return f.getContentPane().getHeight(); // Get the height of the window's content pane
    }
    //
    // set title
    public 
    void title(String title)
    {
        f.setTitle(title); // Set the title of the window
    }


    // INIT //
    //
    // build window
    public 
    void build() 
    {
        this.setLayout(null); // Set layout to null
        f.add(this); // Add this JPanel to the JFrame
        f.pack(); // Pack the JFrame
        f.setVisible(true); // Make the JFrame visible
        f.setLocationRelativeTo(null); // Center the JFrame on the screen
        // f.setExtendedState(JFrame.MAXIMIZED_BOTH); // Uncomment to maximize the window
        f.setMinimumSize( new Dimension(800, 800) ); // Set minimum size for the window
        f.setResizable(false); // Disable resizing of the window
        f.setAlwaysOnTop(true);
        
        // Setiing up Invisible cursor when
        // screen is clicked on.
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

        // background color
        this.setBackground(set.window_bg); // Set the background color using settings
    }
    //
    // set size
    public 
    void size(int x, int y) 
    {
        this.setPreferredSize( new Dimension(x, y) ); // Set preferred size of the JPanel
    }
    //
    // clear elems from screen
    public 
    void clear() 
    {
        this.removeAll(); // Remove all components from the JPanel
        this.setBackground(set.window_bg); // Reset the background color
    }


    // ACCESS //
    //
    // get frame
    public 
    JFrame get() 
    {
        return this.f; // Return the JFrame object
    }
    //
    public 
    void engine(Engine e) 
    {
        this.e = e; // Set the Engine object
        e.screen_color = set.window_bg; // Set the screen color in the Engine
    }
    //
    public 
    void ui(UI ui) 
    {
        this.ui = ui; // Set the UI object
        ui.window(this); // Link the UI to this window
        ui.engine(e); // Link the UI to the Engine
    }
    //
    public 
    void config(Config set) 
    {
        this.set = set; // Set the Settings object
    } 
}
