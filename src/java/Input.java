// AUTHOR: cjRem44x //
//
import java.awt.event.*;

public class Input implements KeyListener, WindowListener {
    // FIELDS //
    //
    private       Window win;
    private       Player player;
    private final int    W = KeyEvent.VK_W,
                         A = KeyEvent.VK_A,
                         S = KeyEvent.VK_S,
                         D = KeyEvent.VK_D;

    // ACCESS //
    //
    // get window
    public void win(Window win) {
        this.win = win;
        this.win.get().addKeyListener(this);
        this.win.get().addWindowListener(this);
    }
    //
    // get player
    public void player(Player player) {
        this.player = player;
    }

    // INPUTS //
    //
    // window input
    @Override
    public void windowClosing(WindowEvent e) {
        System.exit(0);
    }
    //
    // keyboard input
    @Override
    public void keyPressed(KeyEvent e) {
        int n = e.getKeyCode();

        // two way
        if (n == W && n == D) {
            move("up");
            move("right");
        } else if (n==W) {
            move("up");
        } else if (n==S) {
            move("down");
        } else if (n==A) {
            move("left");
        } else if (n==D) {
            move("right");
        }
    }


    // MOVE //
    //
    private void move(String s) {
        switch (s) {
            case "up" -> {
                player.y -= player.speed;
            }
            case "down" -> {
                player.y += player.speed;
            }
            case "left" -> {
                player.x -= player.speed;
            }
            case "right" -> {
                player.x += player.speed;
            }
        }
    }


    // UNUSED INPUTS //
    //
    @Override
    public void windowOpened(WindowEvent e) {}
    @Override
    public void windowClosed(WindowEvent e) {}
    @Override
    public void windowIconified(WindowEvent e) {}
    @Override
    public void windowDeiconified(WindowEvent e) {}
    @Override
    public void windowActivated(WindowEvent e) {}
    @Override
    public void windowDeactivated(WindowEvent e) {}
    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {}
    
}
