//  AUTHOR: cjRem44x //
//
import java.awt.*;
import javax.swing.*;

public class Window extends JPanel {
    // FIELDS //
    //
    private JFrame f    = new JFrame();
    private Render rend = null;
    
   
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
        this.repaint();
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
    }
    //
    // set size
    public void size(int x, int y) {
        this.setPreferredSize( new Dimension(x, y) );
    }

    // ACCESS //
    //
    // get frame
    protected JFrame get() {
        return this.f;
    }
}
