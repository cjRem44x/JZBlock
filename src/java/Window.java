//  AUTHOR: cjRem44x //
//
import java.awt.*;
import javax.swing.*;

public class Window {
    // FIELDS //
    //
    private JFrame f = new JFrame();
    
    
    // INIT //
    //
    // build window
    public void build() {
        f.setVisible(true);
        f.setLocationRelativeTo(null);
    }
    //
    // set size
    public void size(int x, int y) {
        f.setSize(x, y);
    }

    // ACCESS //
    //
    // get frame
    protected JFrame get() {
        return this.f;
    }
}
