//  AUTHOR: cjRem44x //
//
package graphics;

import java.awt.*;
import java.security.SecureRandom;
import javax.swing.*;
//
import core.*;
import ui.UI;

public class Window extends JPanel {
    // FIELDS //
    //
    private SecureRandom rand = new SecureRandom();
    private JFrame       f    = new JFrame();
    private Render       rend = null;
    private Engine       e;
    private UI           ui;
    //
    // colors
    // @TODO: make separate 'Theme.java' for this 
    private Color bg             = new Color(0, 10, 20), 
                  go_bg          = new Color(38, 10, 0),
                  p_health_color = new Color(255, 70, 157),
                  go_color       = new Color(255, 0, 0),
                  zkill_color    = new Color(179, 255, 0),
                  zwave_color    = new Color(255, 119, 0),
                  reload_color   = new Color(43, 234, 244),
                  ZZZ_color      = new Color(225, 205, 255),
                  stats_color    = new Color(255, 0, 255);
    //
    // displaying stats
    private JLabel p_health_lbl, go_lbl, 
                   zkill_lbl, zwave_lbl, 
                   reload_lbl, ZZZ_lbl, 
                   stats_lbl;
    //
    // timer to cycle through round_lbl colors
    private Timer rainbow_t;
  
    
    // RENDER //
    //
    // paint
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // render graphics
        if (rend != null) rend.update(g);
        else System.out.println("RENDER is NULL");
    }
    //
    // add render
    public void rend(Render rend) {
        this.rend = rend;
    }
    //
    // window refresh
    public void ref() {
        ui.update();
        this.repaint();
    }
    //
    // access to screen width
    public int width() {
        return f.getContentPane().getWidth();
    }
    //
    // access to screen height
    public int height() {
        return f.getContentPane().getHeight();
    }


    // INIT //
    //
    // build window
    public void build() {
        this.setLayout(null);
        f.add(this);
        f.pack();
        f.setVisible(true);
        f.setLocationRelativeTo(null);
        f.setExtendedState(JFrame.MAXIMIZED_BOTH);
        f.setMinimumSize( new Dimension(800, 800) );
        // f.setResizable(false);

        // background color
        this.setBackground(bg);
    }
    //
    // set size
    public void size(int x, int y) {
        this.setPreferredSize( new Dimension(x, y) );
    }
    //
    // clear elems from screen
    public void clear() {
        this.removeAll();
        this.setBackground(bg);
    }


    // ACCESS //
    //
    // get frame
    public JFrame get() {
        return this.f;
    }
    //
    public void engine(Engine e) {
        this.e = e;
        e.screen_color = bg;
    }
    //
    public void ui(UI ui) {
        this.ui = ui;
        ui.window(this);
        ui.engine(e);
    } 
}