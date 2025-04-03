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
                        lazar_reload_delay = 2000; // Delay for reloading lazar ammo
    public  int         z_throt = 100, p_throt = 120; // Throttle values for zombie and player movement
    private int         max_z_num = 100, z_num   = 1, // Maximum number of zombies and current zombie count
                        p_size  = 32, z_size = 32, lazar_size = 5; // Sizes for player, zombies, and lazar
    private double      p_speed = 15.0, z_speed = 10.0, lazar_speed = 20.0; // Speeds for player, zombies, and lazar
    //
    public  Player      p; // Player object
    public  List<Zomb>  zombs   = new ArrayList<>(); // List of zombies
    public  List<Lazar> lazars  = new ArrayList<>(); // List of lazars
    public  Timer       z_move_t, // Timer for zombie movement
                        lazar_motion_t, lazar_collision_t, lazar_rem_t, // Timers for lazar actions
                        cleanup_t, heal_t; // Timers for cleanup and healing
    private Random      rand    = new Random(); // Random object for generating random values
    private Settings    set; // Settings object
    public  int         screen_width, screen_height; // Screen dimensions
    private int         z_hit_stren = 25; // Strength of zombie hits
    public  Color       screen_color; // Screen color
    public  boolean     is_p_alive = true; // Flag to check if player is alive
    private boolean     is_respawn = false; // Flag for respawn state
    public  int         z_kills = 0, z_wave = 1, zwf = 5, // Zombie kills, wave count, and wave frequency
                        ZZZ = 0, ZZZ_unit = 44, // Player's currency and unit value
                        lazar_uprice = 400, lazar_f = 1; // Lazar upgrade price and factor
    public  boolean     is_paused = false; // Flag for game pause state


    // ACCESS //
    //
    // get player
    public 
    void player(Player p) 
    {
        this.p = p; // Assign player object
    }
    //
    // get settings
    public
    void settings(Settings set) 
    {
        this.set = set; // Assign settings object
    }


    // ENGINE //
    //
    // start engine
    public 
    void start() 
    {
        init_player(); // Initialize player
        init_zombs(); // Initialize zombies

        z_move_t = new Timer(z_throt, e -> chase()); // Timer for zombie movement
        z_move_t.start();

        lazar_motion_t = new Timer(120, e -> lazar_move()); // Timer for lazar movement
        lazar_motion_t.start();

        lazar_collision_t = new Timer(100, e -> lazar_hits()); // Timer for lazar collision detection
        lazar_collision_t.start();

        lazar_rem_t = new Timer(200, e -> rem_hits()); // Timer for removing hit lazars
        lazar_rem_t.start();

        cleanup_t = new Timer(4000, e -> clean_up_z()); // Timer for cleaning up dead zombies
        cleanup_t.start();

        heal_t = new Timer(8000, e -> heal_p()); // Timer for healing player
    }
    //
    // update engine
    public 
    void update() 
    {
        keep_in_bounds(); // Ensure entities stay within screen bounds
        health_check(); // Check player's health
        z_hits(); // Check zombie hits on player
        is_z_dead(); // Check if zombies are dead
        spawn_check(); // Check and handle zombie spawning
    }


    // START //
    //
    // start player
    private 
    void init_player() 
    {
        p.health = 100; // Set player's health
        p.size = p_size; // Set player's size
        p.body_color = set.p_color; // Set player's color
        p.speed = p_speed;

        p.x = (int)((screen_width/2.00)-(p.size/2.00));
        p.y = (int)((screen_height/2.00)-(p.size/2.00));
    }
    //
    // start zombs
    private 
    void init_zombs() 
    {
        for (int i=0; i<z_num; i++) {
            Zomb z = new Zomb();
            zombs.add(z);
        }
    
        for (Zomb z : zombs) {
            z.size = z_size;
            z.body_color = set.z_color;
            // Set speed based on pause state
            z.speed = is_paused ? 0.0 : z_speed;
            z.x = rand.nextInt(screen_width-z.size)+z.size;
            z.y = rand.nextInt(screen_height-z.size)+z.size;
        }
        is_respawn = true; // Allow respawning of zombies after the first wave
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

            if (p.health >= 100)
            {
                heal_t.stop(); // Stop healing timer if player is at max health
                p.health = 100; // Ensure health does not exceed 100:w
            }
            heal_t.start(); // Restart healing timer if player is alive
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
        if (lazar_mag >= lazar_ammo) 
        {
            // Already full ammo
            return;
        }
        
        // Check if the game is paused
        // If the game is paused, do not reload lazar ammo
        if (is_paused) 
        {
            // If game is paused, do not reload
            return;
        }

        // If lazar_mag is less than lazar_ammo, reload it
        if  (lazar_mag <= 0)
        {
            // Reload lazar ammo with a delay
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