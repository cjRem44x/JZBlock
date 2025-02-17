// AUTHOR: cjRem44x //
//
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.Timer;

public class Engine {
    // FIELDS //
    //
    protected int         z_throt = 200, p_throt = 120;
    private   int         z_num   = 1, p_size  = 32, z_size = 32, lazar_size = 5;
    private   double      p_speed = 10.0, z_speed = 10.0, lazar_speed = 10.0;
    private   Color       p_color     = new Color(0,0,255),
                          z_color     = new Color(255, 0, 0),
                          lazar_color = new Color(255, 255, 0);
    protected Player      p;
    protected List<Zomb>  zombs   = new ArrayList<>();
    protected List<Lazar> lazars  = new ArrayList<>();

    protected int screen_width, screen_height;


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

        new Timer(120, e -> lazar_move()).start();
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
        Lazar l = new Lazar();
        l.speed = lazar_speed;
        l.size = lazar_size;
        l.body_color = lazar_color;
        l.dir = d;
        int x = p.x + (int)(p.size/2.00);
        int y = p.y + (int)(p.size/2.00);
        l.x = x;
        l.y = y;
        lazars.add(l);
    }
    //
    // lazar movement
    public void lazar_move() {
        Iterator<Lazar> iterator = lazars.iterator();

        while (iterator.hasNext()) {
            Lazar l = iterator.next();
    
            // Move the lazar
            switch (l.dir) {
                case "right" -> l.x += l.speed;
                case "left"  -> l.x -= l.speed;
                case "up"    -> l.y -= l.speed;
                case "down"  -> l.y += l.speed;
            }
    
            // Remove lazar if out of bounds
            if (l.x <= 0 || l.x >= screen_width || l.y <= 0 || l.y >= screen_height) {
                iterator.remove(); // Safe removal
            }
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
