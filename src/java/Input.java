// AUTHOR: cjRem44x //
//
import java.awt.event.*;

public class Input implements KeyListener, WindowListener {
    // FIELDS //
    //
    private Window win;

    // ACCESS //
    //
    // get window
    public void win(Window win) {
        this.win = win;
        this.win.get().addKeyListener(this);
        this.win.get().addWindowListener(this);
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
        System.out.println("keys");
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
