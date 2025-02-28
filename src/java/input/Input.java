// AUTHOR: cjRem44x //
//
package input;

import java.awt.event.*;
import javax.swing.Timer;
//
import core.*;
import dat.*;
import graphics.*;
import opt.*;

public class Input 
implements KeyListener, WindowListener 
{
    // FIELDS //
    //
    private Window win;
    private Engine e;
    private Settings set;
    protected Timer set_t;
    //
    // Movement keys
    private int       W     = KeyEvent.VK_W,
                      A     = KeyEvent.VK_A,
                      S     = KeyEvent.VK_S,
                      D     = KeyEvent.VK_D,
                      SPACE = KeyEvent.VK_SPACE,
                      RARR  = KeyEvent.VK_RIGHT,
                      LARR  = KeyEvent.VK_LEFT,
                      UARR  = KeyEvent.VK_UP,
                      DARR  = KeyEvent.VK_DOWN,
                      U     = KeyEvent.VK_U,
                      ESC   = KeyEvent.VK_ESCAPE;
    //
    // Key states
    private boolean up_press    = false,
                    dwn_press   = false,
                    right_press = false,
                    left_press  = false,
                    space_press = false,
                    speed_boost = false,
                    rarr_press  = false,
                    larr_pres   = false,
                    uarr_press  = false,
                    darr_press  = false;


    // ACCESS //
    //
    public void win(Window win) 
    {
        this.win = win;
        this.win.get().addKeyListener(this);
        this.win.get().addWindowListener(this);
    }
    //
    public void engine(Engine e) 
    {
        this.e = e;
        startMovementLoop();
    }
    //
    public void settings(Settings set) 
    {
        this.set = set;
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