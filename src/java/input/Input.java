// AUTHOR: cjRem44x //
//
package input;

import java.awt.event.*; // Import AWT event package
import javax.swing.Timer; // Import Swing Timer
//
import core.*; // Import core package
import dat.*; // Import dat package
import graphics.*; // Import graphics package
import opt.*; // Import opt package

public class Input 
implements KeyListener, WindowListener 
{
    // FIELDS //
    //
    private Window win; // Window object
    private Engine e; // Engine object
    private Settings set; // Settings object
    protected Timer set_t; // Timer object
    //
    // Movement keys
    private int       W     = KeyEvent.VK_W, // W key
                      A     = KeyEvent.VK_A, // A key
                      S     = KeyEvent.VK_S, // S key
                      D     = KeyEvent.VK_D, // D key
                      SPACE = KeyEvent.VK_SPACE, // Space key
                      RARR  = KeyEvent.VK_RIGHT, // Right arrow key
                      LARR  = KeyEvent.VK_LEFT, // Left arrow key
                      UARR  = KeyEvent.VK_UP, // Up arrow key
                      DARR  = KeyEvent.VK_DOWN, // Down arrow key
                      U     = KeyEvent.VK_U, // U key
                      ESC   = KeyEvent.VK_ESCAPE, // Escape key
                      CTRL  = KeyEvent.VK_CONTROL; // Control key
    //
    // Key states
    private boolean up_press    = false, // Up key pressed
                    dwn_press   = false, // Down key pressed
                    right_press = false, // Right key pressed
                    left_press  = false, // Left key pressed
                    space_press = false, // Space key pressed
                    speed_boost = false, // Speed boost active
                    rarr_press  = false, // Right arrow key pressed
                    larr_pres   = false, // Left arrow key pressed
                    uarr_press  = false, // Up arrow key pressed
                    darr_press  = false; // Down arrow key pressed


    // ACCESS //
    //
    // Set the window
    public void win(Window win) 
    {
        this.win = win; // Assign window object
        this.win.get().addKeyListener(this); // Add key listener to window
        this.win.get().addWindowListener(this); // Add window listener to window
    }
    //
    // Set the engine
    public void engine(Engine e) 
    {
        this.e = e; // Assign engine object
        startMovementLoop(); // Start the movement loop
    }
    //
    // Set the settings
    public void settings(Settings set) 
    {
        this.set = set; // Assign settings object
        if (set.switch_controls) 
        {
            UARR  = KeyEvent.VK_W;
            LARR  = KeyEvent.VK_A;
            DARR  = KeyEvent.VK_S;
            RARR  = KeyEvent.VK_D;
            SPACE = KeyEvent.VK_SPACE;
            D     = KeyEvent.VK_RIGHT;
            A     = KeyEvent.VK_LEFT;
            W     = KeyEvent.VK_UP;
            S     = KeyEvent.VK_DOWN;
        }
    }


    // MOVEMENT //
    //
    // Handle key presses
    @Override public 
    void keyPressed(KeyEvent e) 
    {
        int n = e.getKeyCode();

        if (n == ESC)
        {
            if (this.e.is_paused)
            {
                this.e.resume_game();
                this.e.is_paused = false;
            } else
            {
                this.e.pause_game();
                this.e.is_paused = true;
            }
        }

        if (n == CTRL)
        {
            this.e.reload_lazar();
        }

        if (n == KeyEvent.VK_R && 
            !this.e.is_p_alive) 
        {
            this.e.restart();
            this.win.clear();
        }

        if (n == U) {
            this.e.upgrade_blaster();
        }

        if (n == W) up_press      = true;
        if (n == S) dwn_press    = true;
        if (n == A) left_press    = true;
        if (n == D) right_press   = true;
        if (n == SPACE) space_press = true;

        if (n == RARR) rarr_press = true;
        if (n == LARR) larr_pres = true;
        if (n == UARR) uarr_press = true;
        if (n == DARR) darr_press = true;
    }
    //
    // Handle key releases
    @Override public 
    void keyReleased(KeyEvent e) 
    {
        int n = e.getKeyCode();
        if (n == W) up_press      = false;
        if (n == S) dwn_press    = false;
        if (n == A) left_press    = false;
        if (n == D) right_press   = false;
        if (n == SPACE) space_press = false;

        
        if (n == RARR) rarr_press = false;
        if (n == LARR) larr_pres = false;
        if (n == UARR) uarr_press = false;
        if (n == DARR) darr_press = false;
    }
    //
    // Continuous movement loop
    private 
    void startMovementLoop() 
    {
        new javax.swing.Timer(e.p_throt, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (up_press)    e.p.y -= e.p.speed;
                if (dwn_press)   e.p.y += e.p.speed;
                if (left_press)  e.p.x -= e.p.speed;
                if (right_press) e.p.x += e.p.speed;

                if (rarr_press) e.blast("right");
                if (larr_pres)  e.blast("left");
                if (uarr_press) e.blast("up");
                if (darr_press) e.blast("down");
                
                if (space_press && !speed_boost) {
                    e.p.speed *= 2;
                    speed_boost = true;
                }
                if (!space_press && speed_boost) {
                    e.p.speed /= 2;
                    speed_boost = false;
                }
            }
        }).start();
    }


    // WINDOW //
    //
    @Override public 
    void windowClosing(WindowEvent e) 
    {
        System.exit(0);
    }


    // UNUSED //
    //
    @Override public void windowOpened(WindowEvent e) {}
    @Override public void windowClosed(WindowEvent e) {}
    @Override public void windowIconified(WindowEvent e) {}
    @Override public void windowDeiconified(WindowEvent e) {}
    @Override public void windowActivated(WindowEvent e) {}
    @Override public void windowDeactivated(WindowEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}