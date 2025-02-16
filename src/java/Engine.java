// AUTHOR: cjRem44x //
//
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class Engine {
    // FIELDS //
    //
    private int p_size = 32, z_size = 32;
    private Color p_color = new Color(0,0,255);
    private Color z_color = new Color(255, 0, 0);
    private Player     p;
    private List<Zomb> zombs  = new ArrayList<>();


    // ACCESS //
    //
    // get player
    public void player(Player p) {
        this.p = p;
    }

    // START //
    //
    // start player
    private void init_player() {
        p.size = p_size;
        p.body_color = p_color;
    }
    //
    // start zombs
    private void init_zombs() {
        for (Zomb z : zombs) {
            z.size = z_size;
            z.body_color = z_color;
        }
    }
    

    // MOVEMENT //
    //
    // zombs chase
    private void chase() {
        if (!zombs.isEmpty()) {
            for (Zomb z : zombs) {
                // directions
                double dx = (double)p.x-z.x;
                double dy = (double)p.y-z.y;

                double L = Math.sqrt((dx*dx) + (dy*dy));
                if (L == 0.0) continue;

                double ux = dx/L;
                double uy = dy/L;
                
                double  xval = ux*z.speed,
                        yval = uy*z.speed;
                z.x += (int)xval;
                z.y += (int)yval;
            }
        }
    }
}
