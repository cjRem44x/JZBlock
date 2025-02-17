// AUTHOR: cjRem44x //
//
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class Engine {
    // FIELDS //
    //
    protected int         z_throt = 200, p_throt = 120;
    private   int         z_num   = 1, p_size  = 32, z_size = 32;
    private   double      p_speed = 10.0, z_speed = 10.0;
    private   Color       p_color = new Color(0,0,255);
    private   Color       z_color = new Color(255, 0, 0);
    protected Player      p;
    protected List<Zomb>  zombs   = new ArrayList<>();
    protected List<Lazar> lazars  = new ArrayList<>();


    // ACCESS //
    //
    // get player
    public void player(Player p) {
        this.p = p;
    }


    // ENGINE //
    //
    // start engine
    public void start() {
        init_player();
        init_zombs();
    }
    //
    // update engine
    public void update() {
    }


    // START //
    //
    // start player
    private void init_player() {
        p.size = p_size;
        p.body_color = p_color;
        p.speed = p_speed;
    }
    //
    // start zombs
    private void init_zombs() {
        for (int i=0; i<z_num; i++) {
            Zomb z = new Zomb();
            zombs.add(z);
        }
        for (Zomb z : zombs) {
            z.size = z_size;
            z.body_color = z_color;
            z.speed = z_speed;
        }
    }


    // BLASTER //
    //
    protected void blast(String d) {
        switch (d) {
            case "right" -> {}
            case "left" -> {}
            case "up" -> {}
            case "down" -> {}
        }
    }
    

    // MOVEMENT //
    //
    // zombs chase
    protected void chase() {
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
