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
    private boolean upPressed    = false,
                    downPressed  = false, 
                    leftPressed  = false,
                    rightPressed = false,
                    spcPressed   = false,
                    speedBoosted = false;


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

        if (n == W) upPressed      = true;
        if (n == S) downPressed    = true;
        if (n == A) leftPressed    = true;
        if (n == D) rightPressed   = true;
        if (n == SPACE) spcPressed = true;
    }
    //
    // Handle key releases
    @Override
    public void keyReleased(KeyEvent e) {
        int n = e.getKeyCode();
        if (n == W) upPressed      = false;
        if (n == S) downPressed    = false;
        if (n == A) leftPressed    = false;
        if (n == D) rightPressed   = false;
        if (n == SPACE) spcPressed = false;
    }
    //
    // Continuous movement loop
    private void startMovementLoop() {
        new javax.swing.Timer(e.p_throt, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (upPressed) e.p.y -= e.p.speed;
                if (downPressed) e.p.y += e.p.speed;
                if (leftPressed) e.p.x -= e.p.speed;
                if (rightPressed) e.p.x += e.p.speed;
                
                if (spcPressed && !speedBoosted) {
                    e.p.speed *= 2;
                    speedBoosted = true;
                }
                if (!spcPressed && speedBoosted) {
                    e.p.speed /= 2;
                    speedBoosted = false;
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
