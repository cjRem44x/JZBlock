// AUTHOR: cjRem44x //
//
import java.awt.*;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.Timer;

public class Engine {
    // FIELDS //
    //
    protected int         z_throt = 200, p_throt = 120;
    private   int         z_num   = 5, p_size  = 32, z_size = 32, lazar_size = 5;
    private   double      p_speed = 10.0, z_speed = 10.0, lazar_speed = 20.0;
    private   Color       p_color     = new Color(0,0,255),
                          z_color     = new Color(255, 0, 0),
                          lazar_color = new Color(255, 255, 0),
                          lazar_blow_color = new Color(255, 255, 255);
    protected Player      p;
    protected List<Zomb>  zombs   = new ArrayList<>();
    protected List<Lazar> lazars  = new ArrayList<>();
    protected Timer       lazar_motion_t, lazar_collision_t, lazar_rem_t;
    private   Random      rand    = new Random();

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

        lazar_motion_t = new Timer(120, e -> lazar_move());
        lazar_motion_t.start();

        lazar_collision_t = new Timer(100, e -> lazar_hits());
        lazar_collision_t.start();

        lazar_rem_t = new Timer(200, e -> rem_hits());
        lazar_rem_t.start();
    }
    //
    // update engine
    public void update() {
    }


    // START //
    //
    // start player
    private void init_player() {
        p.health = 100;
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
            z.x = rand.nextInt(screen_width-z.size)+z.size;
            z.y = rand.nextInt(screen_height-z.size)+z.size;
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
    //
    public void lazar_hits() {
        for (Lazar l : lazars) {
            Rectangle lazar_rect = new Rectangle(l.x, l.y, l.size, l.size);

            for (Zomb z : zombs) {
                Rectangle z_rect = new Rectangle(z.x, z.y, z.size, z.size);

                if (lazar_rect.intersects(z_rect)) {
                    blow_lazar(l);
                }
            }
        }
    }
    //
    public void blow_lazar(Lazar l) {
        if (l.size < 10) l.size *= 2;
        l.body_color = lazar_blow_color;
        l.is_hit = true;
    }
    //
    public void rem_hits() {
        Iterator<Lazar> iterator = lazars.iterator();
        while (iterator.hasNext()) {
            Lazar l = iterator.next();
            if (l.is_hit) {
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

                // prevent zombies from bunching up
                for (Zomb other : zombs) {
                    if (other != z) {
                        double distX = other.x - z.x;
                        double distY = other.y - z.y;
                        double dist = Math.sqrt(distX * distX + distY * distY);

                        if (dist < z.size * 1.5) { // If too close, push away slightly
                            z.x -= distX * 0.05;
                            z.y -= distY * 0.05;
                        }
                    }
                }
            }
        }
    }


} // class end
