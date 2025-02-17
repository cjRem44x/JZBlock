// AUTHOR: cjRem44x //
//
import java.awt.event.*;

public class Input implements KeyListener, WindowListener {
    // FIELDS //
    //
    private Window win;
    private Engine e;
    //
    // Movement keys
    private final int W     = KeyEvent.VK_W,
                      A     = KeyEvent.VK_A,
                      S     = KeyEvent.VK_S,
                      D     = KeyEvent.VK_D,
                      SPACE = KeyEvent.VK_SPACE;
    //
    // Key states
    private boolean up_press    = false,
                    dwn_press   = false,
                    right_press = false,
                    left_press  = false,
                    space_press = false,
                    speed_boost = false; 


    // ACCESS //
    //
    public void win(Window win) {
        this.win = win;
        this.win.get().addKeyListener(this);
        this.win.get().addWindowListener(this);
    }
    //
    public void engine(Engine e) {
        this.e = e;
        startMovementLoop();
    }

    // MOVEMENT //
    //
    // Handle key presses
    @Override
    public void keyPressed(KeyEvent e) {
        int n = e.getKeyCode();

        if (n == W) up_press      = true;
        if (n == S) dwn_press    = true;
        if (n == A) left_press    = true;
        if (n == D) right_press   = true;
        if (n == SPACE) space_press = true;
    }
    //
    // Handle key releases
    @Override
    public void keyReleased(KeyEvent e) {
        int n = e.getKeyCode();
        if (n == W) up_press      = false;
        if (n == S) dwn_press    = false;
        if (n == A) left_press    = false;
        if (n == D) right_press   = false;
        if (n == SPACE) space_press = false;
    }
    //
    // Continuous movement loop
    private void startMovementLoop() {
        new javax.swing.Timer(e.p_throt, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (up_press) e.p.y -= e.p.speed;
                if (dwn_press) e.p.y += e.p.speed;
                if (left_press) e.p.x -= e.p.speed;
                if (right_press) e.p.x += e.p.speed;
                
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
    @Override
    public void windowClosing(WindowEvent e) {
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
