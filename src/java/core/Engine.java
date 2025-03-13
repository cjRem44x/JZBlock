// AUTHOR: cjRem44x //
//
package core;

import java.awt.*;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.Timer;
//
import dat.*;
import opt.*;

public class Engine 
{
    // FIELDS //
    //
    // lazar ammo and mag
    public  int         lazar_ammo = 8, lazar_mag = lazar_ammo, 
                        lazar_ammo_org = lazar_ammo, 
                        lazar_reload_delay = 2000;
    public  int         z_throt = 100, p_throt = 120;
    private int         max_z_num = 100, z_num   = 1, 
                        p_size  = 32, z_size = 32, lazar_size = 5;
    private double      p_speed = 15.0, z_speed = 10.0, lazar_speed = 20.0;
    //
    public  Player      p;
    public  List<Zomb>  zombs   = new ArrayList<>();
    public  List<Lazar> lazars  = new ArrayList<>();
    public  Timer       z_move_t, 
                        lazar_motion_t, lazar_collision_t, lazar_rem_t,
                        cleanup_t, heal_t;
    private Random      rand    = new Random();
    private Settings    set;
    public  int         screen_width, screen_height;
    private int         z_hit_stren = 25;
    public  Color       screen_color;
    public  boolean     is_p_alive = true;
    private boolean     is_respawn = false;
    public  int         z_kills = 0, z_wave = 1, zwf = 5,
                        ZZZ = 0, ZZZ_unit = 44,
                        lazar_uprice = 400, lazar_f = 1;
    public  boolean     is_paused = false;


    // ACCESS //
    //
    // get player
    public 
    void player(Player p) 
    {
        this.p = p;
    }
    //
    // get settings
    public
    void settings(Settings set) 
    {
        this.set = set;
    }


    // ENGINE //
    //
    // start engine
    public 
    void start() 
    {
        init_player();
        init_zombs();

        z_move_t = new Timer(z_throt, e -> chase());
        z_move_t.start();

        lazar_motion_t = new Timer(120, e -> lazar_move());
        lazar_motion_t.start();

        lazar_collision_t = new Timer(100, e -> lazar_hits());
        lazar_collision_t.start();

        lazar_rem_t = new Timer(200, e -> rem_hits());
        lazar_rem_t.start();

        cleanup_t = new Timer(4000, e -> clean_up_z());
        cleanup_t.start();

        heal_t = new Timer(8000, e -> heal_p());
        heal_t.start();
    }
    //
    // update engine
    public 
    void update() 
    {
        keep_in_bounds();
        health_check();
        z_hits();
        is_z_dead();
        spawn_check();
    }


    // START //
    //
    // start player
    private 
    void init_player() 
    {
        p.health = 100;
        p.size = p_size;
        p.body_color = set.p_color;
        p.speed = p_speed;

        p.x = (int)((screen_width/2.00)-(p.size/2.00));
        p.y = (int)((screen_height/2.00)-(p.size/2.00));
    }
    //
    // start zombs
    private 
    void init_zombs() 
    {
        for (int i=0; i<z_num; i++) 
        {
            Zomb z = new Zomb();
            zombs.add(z);
        }

        for (Zomb z : zombs) 
        {
            z.size = z_size;
            z.body_color = set.z_color;
            z.speed = z_speed;
            z.x = rand.nextInt(screen_width-z.size)+z.size;
            z.y = rand.nextInt(screen_height-z.size)+z.size;
        }
        is_respawn = true;
    }


    // PLAYER //
    //
    public 
    void health_check() 
    {
        if (p.health <= 0) 
        {
            is_p_alive = false;
            p_dead();
        }
    }
    //
    public 
    void p_dead() 
    {
        p.speed = 0.0;
    }
    //
    private 
    void heal_p() 
    {
        if (p.health < 100) 
        {
            p.health += 25;
        }
    }


    // CLEAN UP //
    //
    public 
    void clean_up_z() 
    {
        Iterator<Zomb> iterator = zombs.iterator();
        
        while (iterator.hasNext()) 
        {
            Zomb z = iterator.next();
            if (z.is_dead) 
            {
                z.body_color = screen_color; // Optional visual effect
                iterator.remove(); // Safe removal
            }
        }
    }


    // RESTART //
    //
    public 
    void restart() 
    {
        kill_all();
        lazars.clear();  // Ensure all projectiles are removed
        p.health = 100;
        p.speed = p_speed;
        is_p_alive = true;
        z_num = 1;
        z_kills = 0;
        z_wave = 1;

        ZZZ = 0;
        lazar_f = 1;
        lazar_uprice = 400;

        lazar_ammo = lazar_ammo_org;
        lazar_mag = lazar_ammo;
        
        init_zombs(); // Restart zombies properly
    }
    //
    //
    public 
    void kill_all() 
    {
        zombs.clear();
    }


    // SPAWN // 
    //
    public 
    void spawn_check() 
    {
        if (zombs.isEmpty() && is_respawn) 
        {
            if (z_num >= max_z_num) {
                z_num = max_z_num;
            } 
            if (z_num == 1){
                z_num++;
            } else {
                z_num+=(int)(z_num/2.00);
            }
            z_wave++;
            init_zombs();
            inc_z_health();
            // speed_inc();
        }
    }
    //
    void 
    inc_z_health() 
    {
        for (Zomb z : zombs) 
        {
            z.health += 25;
        }
    }


    // ZOMB HITS //
    //
    public 
    void z_hits() 
    {
        if (!is_paused) 
        {
            Rectangle p_rect = new Rectangle(p.x, p.y, p.size, p.size);
            long currentTime = System.currentTimeMillis();
        
            for (Zomb z : zombs) 
            {
                Rectangle z_rect = new Rectangle(z.x, z.y, z.size, z.size);
        
                if (p_rect.intersects(z_rect) && 
                    (currentTime - z.last_hit_t >= 1000) &&
                    !z.is_dead
                ) 
                {
                    z.last_hit_t = currentTime; // Update last hit time
                    p_dec();
                }
            }
        }
    }
    //
    // dec player health
    public void p_dec() {
        p.health -= z_hit_stren;
        if (p.health <= 0) p.health = 0;
    }


    // BLASTER //
    //
    public 
    void blast(String d) 
    {
        if (!is_paused && lazar_mag > 0)
        {
            Lazar l = new Lazar();
            l.speed = lazar_speed;
            l.size = lazar_size;
            l.body_color = set.lazar_color;
            l.shadow_color = set.lazar_shadow;
            l.dir = d;
            int x = p.x + (int)(p.size/2.00);
            int y = p.y + (int)(p.size/2.00);
            l.x = x;
            l.y = y;
            lazars.add(l);
            lazar_mag--;
        }
    }
    //
    // lazar movement
    public void lazar_move() {
        Iterator<Lazar> iterator = lazars.iterator();

        while (iterator.hasNext()) {
            Lazar l = iterator.next();
    
            // Move the lazar
            switch (l.dir) {
                // @NOTE: Ignore VSCode, this is
                // a valid switch format for 
                // JDK21 (Java 21)+.
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
    //
    public 
    void reload_lazar() 
    {
        new Thread(() -> 
        {
            try 
            {
                Thread.sleep(lazar_reload_delay); // 2-second delay
                lazar_mag = lazar_ammo;
                // System.out.println("Lazar reloaded: " + lazar_mag);
            } catch (InterruptedException e) 
            {
                e.printStackTrace();
            }
        }).start();
    }
    
    //
    public void lazar_hits() {
        for (Lazar l : lazars) {
            Rectangle lazar_rect = new Rectangle(l.x, l.y, l.size, l.size);

            for (Zomb z : zombs) {
                Rectangle z_rect = new Rectangle(z.x, z.y, z.size, z.size);

                if (lazar_rect.intersects(z_rect)) {
                    z.health -= (l.stren*lazar_f);
                    blow_lazar(l);
                }
            }
        }
    }
    //
    // Blowup the lazar when target
    // is hit.  
    public 
    void blow_lazar(Lazar l) 
    {
        if (l.size < 10) l.size *= 2;
        l.body_color = set.lazar_blow_color;
        l.shadow_color = set.lazar_blow_color;
        l.is_hit = true;
    }
    //
    public 
    void rem_hits() 
    {
        Iterator<Lazar> iterator = lazars.iterator();
        while (iterator.hasNext()) 
        {
            Lazar l = iterator.next();
            if (l.is_hit) 
            {
                iterator.remove(); // Safe removal
            }
        }
    }
    //
    public 
    void is_z_dead() 
    {
        for (Zomb z : zombs) 
        {
            if (z.health <= 0 && !z.is_dead) 
            {
                z.speed = 0;                  // stop dead zomb
                z.body_color = set.z_dead_color;  // set death color
                z.is_dead = true;             // mark as dead

                // reward player
                z_kills++;
                ZZZ+=ZZZ_unit;
            }
        }
    }
    //
    public 
    void upgrade_blaster() 
    {
        if (ZZZ >= lazar_uprice) 
        {
            // player pays for upgrade
            ZZZ -= lazar_uprice;
            lazar_f *= 2;         // scale lazar power (lazar 
                                  // power = 1 * lazar_f)
            lazar_uprice *= 2;
            lazar_ammo *= 2;
        }
    }
    //
    public 
    int lazar_power() 
    {
        return 25*lazar_f;
    }
    

    // MOVEMENT //
    private
    void keep_in_bounds() 
    {
        // Ensure player stays within bounds
        if (p.x < 0) p.x = 0;
        if (p.x + p.size > screen_width) p.x = screen_width - p.size;
        if (p.y < 0) p.y = 0;
        if (p.y + p.size > screen_height) p.y = screen_height - p.size;

        // Ensure zombies stay within bounds
        for (Zomb z : zombs) {
            if (z.x < 0) z.x = 0;
            if (z.x + z.size > screen_width) z.x = screen_width - z.size;
            if (z.y < 0) z.y = 0;
            if (z.y + z.size > screen_height) z.y = screen_height - z.size;
        }
    }
    //
    //
    private
    void speed_inc() 
    {
        if (z_wave >= zwf) 
        {
            p.speed += 0.3;
            for (Zomb z : zombs) 
            {
                z.speed += 0.5;
            }
        }
    }
    //
    // AI zombie movement; chase
    // player without bunching
    protected 
    void chase() 
    {
        if (!zombs.isEmpty()) 
        {
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
                for (Zomb other : zombs) 
                {
                    if (other != z) 
                    {
                        double distX = other.x - z.x;
                        double distY = other.y - z.y;
                        double dist = Math.sqrt(distX * distX + distY * distY);

                        if (dist < z.size * 1.5) 
                        { // If too close, push away slightly
                            z.x -= distX * 0.05;
                            z.y -= distY * 0.05;
                        }

                        if ( rng(1, 10) == 2) // 1/10 chance 
                        { 
                            // sporadically move zomb
                            int n = rng(1,10);
                            double fac = 0.55;
                            if (n%2==0) 
                            {
                                z.x -= fac;
                                z.y += fac;
                            } else
                            {
                                z.x += fac;
                                z.y -= fac;
                            }
                        }
                    }
                }
            }
        }
    }


    // PAUSE //
    //
    public
    void pause_game() 
    {
        p.speed = 0.0;

        for (Zomb z : zombs)
        {
            z.speed = 0.0;
        }

        heal_t.stop();
    }
    //
    public
    void resume_game() 
    {
        p.speed = p_speed;

        for (Zomb z : zombs)
        {
            z.speed = z_speed;
        }

        heal_t.start();
    }
    

    // RANDOMS //
    //
    private
    int rng(int min, int max) 
    {
        return rand.nextInt(max-min)+min;
    }

} // END CLASS //