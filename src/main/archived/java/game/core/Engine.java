// AUTHOR: cjRem44x //
//
// ============================================================================
// Engine.java - Core Game Logic Controller
// ============================================================================
// The heart of JZBlock - manages all game mechanics including player health,
// zombie AI, projectile physics, collision detection, wave spawning, and
// the upgrade/currency system.
//
// GAME MECHANICS OVERVIEW:
//
// 1. WAVE SYSTEM
//    - Game starts with 1 zombie
//    - When all zombies killed, new wave spawns with 1.5x zombies
//    - Zombie health increases by 25 each wave
//    - Max 100 zombies per wave
//
// 2. COMBAT
//    - Player shoots lazars with arrow keys (8 ammo, CTRL to reload)
//    - Lazars deal 25 * lazar_f damage (upgradeable)
//    - Zombies deal 25 damage on collision (1 second cooldown)
//    - Player auto-heals +25 HP every 8 seconds (when not at max)
//
// 3. ECONOMY
//    - Kill zombie = +$44 ZZZ currency
//    - Upgrade weapon = $400 (doubles each upgrade)
//    - Upgrade doubles: damage multiplier (lazar_f) and ammo capacity
//
// 4. ZOMBIE AI (chase method)
//    - Calculate direction vector toward player
//    - Normalize and multiply by speed
//    - Push zombies apart to prevent bunching
//
// 5. TIMER-BASED SYSTEMS
//    - z_move_t (100ms): Zombie chase AI
//    - lazar_motion_t (120ms): Projectile movement
//    - lazar_collision_t (100ms): Hit detection
//    - lazar_rem_t (200ms): Remove hit projectiles
//    - cleanup_t (4000ms): Remove dead zombies
//    - heal_t (8000ms): Auto-heal player
// ============================================================================
//
package game.core;

import java.awt.*;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.Timer;
//
import game.dat.*;
import game.opt.*;

public class Engine
{
    // ===========================
    // GAME BALANCE CONSTANTS
    // ===========================

    // Ammo system
    public  int    lazar_ammo = 8;            // Shots per magazine (upgradeable)
    public  int    lazar_mag = lazar_ammo;    // Current ammo in magazine
    public  int    lazar_ammo_org = lazar_ammo;  // Original ammo (for restart)
    public  int    lazar_reload_delay = 2000; // Reload time: 2 seconds

    // Update throttling (milliseconds between updates)
    public  int    z_throt = 100;    // Zombie AI update interval
    public  int    p_throt = 120;    // Player movement update interval

    // Entity counts
    private int    max_z_num = 100;  // Maximum zombies per wave
    private int    z_num = 1;        // Current wave zombie count (starts at 1)

    // Entity sizes (pixels)
    private int    p_size = 32;      // Player square size
    private int    z_size = 32;      // Zombie square size
    private int    lazar_size = 5;   // Projectile size

    // Entity speeds (pixels per update)
    private double p_speed = 15.0;     // Player movement speed
    private double z_speed = 10.0;     // Zombie chase speed
    private double lazar_speed = 20.0; // Projectile travel speed

    // ===========================
    // ENTITY COLLECTIONS
    // ===========================
    public  Player      p;                              // The player entity
    public  List<Zomb>  zombs   = new ArrayList<>();    // Active zombies
    public  List<Lazar> lazars  = new ArrayList<>();    // Active projectiles

    // ===========================
    // GAME LOOP TIMERS
    // ===========================
    public  Timer  z_move_t;          // Zombie chase AI
    public  Timer  lazar_motion_t;    // Projectile movement
    public  Timer  lazar_collision_t; // Hit detection
    public  Timer  lazar_rem_t;       // Cleanup hit projectiles
    public  Timer  cleanup_t;         // Remove dead zombies
    public  Timer  heal_t;            // Player auto-heal

    // ===========================
    // STATE VARIABLES
    // ===========================
    private Random  rand = new Random();  // For random spawning
    private Config  set;                  // Configuration reference
    public  int     screen_width;         // Current screen width (for bounds)
    public  int     screen_height;        // Current screen height
    private int     z_hit_stren = 25;     // Damage zombies deal per hit
    public  Color   screen_color;         // Background color (for dead zombie effect)

    // ===========================
    // GAME STATE FLAGS
    // ===========================
    public  boolean is_p_alive = true;    // Player alive state
    private boolean is_respawn = false;   // Allow wave respawning after first wave
    public  boolean is_paused = false;    // Game pause state

    // ===========================
    // SCORE/PROGRESSION
    // ===========================
    public  int     z_kills = 0;       // Total zombies killed this run
    public  int     z_wave = 1;        // Current wave number
    public  int     zwf = 5;           // Wave frequency for speed increase (unused)
    public  int     ZZZ = 0;           // Player currency
    public  int     ZZZ_unit = 44;     // Currency earned per kill
    public  int     lazar_uprice = 400; // Current upgrade cost (doubles each upgrade)
    public  int     lazar_f = 1;        // Damage multiplier (doubles each upgrade)


    // ===========================
    // DEPENDENCY INJECTION
    // ===========================
    // Inject player entity
    public
    void player(Player p)
    {
        this.p = p;
    }
    //
    // Inject configuration
    public
    void config(Config set)
    {
        this.set = set;
    }


    // ===========================
    // ENGINE LIFECYCLE
    // ===========================

    // Initialize engine - called once at game start
    // Sets up player, spawns first zombies, starts all game timers
    public
    void start()
    {
        init_player();  // Set player starting state
        init_zombs();   // Spawn first wave zombies

        // Start all game loop timers
        // Each timer runs a specific game system at its own interval

        // Zombie AI: Update chase direction every 100ms
        z_move_t = new Timer(z_throt, e -> chase());
        z_move_t.start();

        // Projectile movement: Move lazars every 120ms
        lazar_motion_t = new Timer(120, e -> lazar_move());
        lazar_motion_t.start();

        // Hit detection: Check lazar-zombie collisions every 100ms
        lazar_collision_t = new Timer(100, e -> lazar_hits());
        lazar_collision_t.start();

        // Projectile cleanup: Remove hit lazars every 200ms
        lazar_rem_t = new Timer(200, e -> rem_hits());
        lazar_rem_t.start();

        // Dead zombie cleanup: Remove dead zombies every 4 seconds
        cleanup_t = new Timer(4000, e -> clean_up_z());
        cleanup_t.start();

        // Auto-heal: Restore 25 HP every 8 seconds (started when damaged)
        heal_t = new Timer(8000, e -> heal_p());
        // Note: heal_t is started/stopped dynamically based on player health
    }

    // Main update loop - called every frame from GameLauncher.loop_game()
    // Processes all per-frame game logic
    public
    void update()
    {
        keep_in_bounds();  // Clamp entities to screen edges
        health_check();    // Check if player died
        z_hits();          // Process zombie collisions with player
        is_z_dead();       // Mark killed zombies and award currency
        spawn_check();     // Spawn new wave if all zombies killed
    }


    // ===========================
    // INITIALIZATION
    // ===========================

    // Initialize player with starting stats and center on screen
    private
    void init_player()
    {
        p.health = 100;            // Full health
        p.size = p_size;           // 32x32 pixels
        p.body_color = set.p_color; // Purple
        p.speed = p_speed;         // 15 pixels per update

        // Center player on screen
        p.x = (int)((screen_width/2.00)-(p.size/2.00));
        p.y = (int)((screen_height/2.00)-(p.size/2.00));
    }

    // Spawn zombies for current wave
    // Creates z_num zombies at random positions
    private
    void init_zombs()
    {
        // Create zombie entities
        for (int i=0; i<z_num; i++)
        {
            Zomb z = new Zomb();
            zombs.add(z);
        }

        // Initialize each zombie's properties
        for (Zomb z : zombs)
        {
            z.size = z_size;           // 32x32 pixels
            z.body_color = set.z_color; // Green

            // Don't move if game is paused
            z.speed = is_paused ? 0.0 : z_speed;

            // Random position within screen bounds
            z.x = rand.nextInt(screen_width-z.size)+z.size;
            z.y = rand.nextInt(screen_height-z.size)+z.size;
        }

        // Enable wave respawning after first wave
        is_respawn = true;
    }


    // ===========================
    // PLAYER HEALTH SYSTEM
    // ===========================

    // Check if player should die (called every frame)
    public
    void health_check()
    {
        if (p.health <= 0)
        {
            is_p_alive = false;
            p_dead();
        }
    }

    // Handle player death - freeze movement
    public
    void p_dead()
    {
        p.speed = 0.0;  // Can't move when dead
    }

    // Auto-heal callback - restores 25 HP
    // Called every 8 seconds by heal_t timer
    private
    void heal_p()
    {
        if (p.health < 100)
        {
            p.health += 25;
        }
    }


    // ===========================
    // CLEANUP SYSTEMS
    // ===========================

    // Remove dead zombies from the game
    // Called every 4 seconds by cleanup_t timer
    // Zombies are marked dead when killed but stay visible briefly
    public
    void clean_up_z()
    {
        Iterator<Zomb> iterator = zombs.iterator();

        while (iterator.hasNext())
        {
            Zomb z = iterator.next();
            if (z.is_dead)
            {
                z.body_color = screen_color;  // Fade out effect
                iterator.remove();            // Remove from list
            }
        }
    }


    // ===========================
    // GAME RESTART
    // ===========================

    // Reset game to initial state (called when pressing R after death)
    public
    void restart()
    {
        // Clear all entities
        kill_all();
        lazars.clear();

        // Reset player state
        p.health = 100;
        p.speed = p_speed;
        is_p_alive = true;

        // Reset wave progression
        z_num = 1;
        z_kills = 0;
        z_wave = 1;

        // Reset economy
        ZZZ = 0;
        lazar_f = 1;
        lazar_uprice = 400;

        // Reset ammo
        lazar_ammo = lazar_ammo_org;
        lazar_mag = lazar_ammo;

        // Spawn first wave
        init_zombs();
    }

    // Clear all zombies
    public
    void kill_all()
    {
        zombs.clear();
    }


    // ===========================
    // WAVE SPAWNING
    // ===========================

    // Check if new wave should spawn (all zombies dead)
    // Called every frame from update()
    public
    void spawn_check()
    {
        // Only spawn if all zombies dead and spawning enabled
        if (zombs.isEmpty() && is_respawn)
        {
            // Cap zombie count at max
            if (z_num >= max_z_num) {
                z_num = max_z_num;
            }

            // Calculate next wave zombie count
            // Formula: 1.5x previous (z_num + z_num/2)
            if (z_num == 1) {
                z_num++;  // Wave 2 has 2 zombies
            } else {
                z_num += (int)(z_num/2.00);  // +50% each wave
            }

            z_wave++;      // Increment wave counter
            init_zombs();  // Spawn new zombies
            inc_z_health(); // Make them tougher
            // speed_inc(); // Commented out: would increase speeds
        }
    }

    // Increase zombie health for new wave (+25 HP each wave)
    void
    inc_z_health()
    {
        for (Zomb z : zombs)
        {
            z.health += 25;  // Wave 2: 125 HP, Wave 3: 150 HP, etc.
        }
    }


    // ===========================
    // ZOMBIE COLLISION (DAMAGE TO PLAYER)
    // ===========================

    // Check for zombie collisions with player
    // Each zombie has 1-second cooldown between hits
    public
    void z_hits()
    {
        if (!is_paused)
        {
            // Create player hitbox
            Rectangle p_rect = new Rectangle(p.x, p.y, p.size, p.size);
            long currentTime = System.currentTimeMillis();

            for (Zomb z : zombs)
            {
                Rectangle z_rect = new Rectangle(z.x, z.y, z.size, z.size);

                // Check collision with cooldown (1000ms = 1 second)
                if (p_rect.intersects(z_rect) &&
                    (currentTime - z.last_hit_t >= 1000) &&
                    !z.is_dead)
                {
                    z.last_hit_t = currentTime;  // Reset this zombie's cooldown
                    p_dec();                      // Deal damage to player
                }
            }

            // Manage auto-heal timer
            if (p.health >= 100)
            {
                heal_t.stop();       // Don't heal at max health
                p.health = 100;      // Cap at 100
            }
            heal_t.start();          // Start/continue healing when damaged
        }
    }

    // Decrease player health by zombie hit strength (25)
    public void p_dec() {
        p.health -= z_hit_stren;
        if (p.health <= 0) p.health = 0;  // Floor at 0
    }


    // ===========================
    // WEAPON SYSTEM (LAZAR BLASTER)
    // ===========================

    // Fire a lazar projectile in the given direction
    // Called by Input when arrow key is pressed
    // Direction: "up", "down", "left", "right"
    public
    void blast(String d)
    {
        // Can only shoot if not paused and have ammo
        if (!is_paused && lazar_mag > 0)
        {
            Lazar l = new Lazar();
            l.speed = lazar_speed;            // 20 pixels per update
            l.size = lazar_size;              // 5 pixels
            l.body_color = set.lazar_color;   // Pink
            l.shadow_color = set.lazar_shadow; // Light pink
            l.dir = d;                         // Travel direction

            // Spawn at player center
            int x = p.x + (int)(p.size/2.00);
            int y = p.y + (int)(p.size/2.00);
            l.x = x;
            l.y = y;

            lazars.add(l);
            lazar_mag--;  // Consume ammo
        }
    }

    // Move all active lazars in their direction
    // Called every 120ms by lazar_motion_t timer
    public void lazar_move() {
        Iterator<Lazar> iterator = lazars.iterator();

        while (iterator.hasNext()) {
            Lazar l = iterator.next();

            // Move lazar based on direction
            // NOTE: This uses Java 21+ switch expression syntax
            switch (l.dir) {
                case "right" -> l.x += l.speed;
                case "left"  -> l.x -= l.speed;
                case "up"    -> l.y -= l.speed;
                case "down"  -> l.y += l.speed;
            }

            // Remove if went off screen
            if (l.x <= 0 || l.x >= screen_width || l.y <= 0 || l.y >= screen_height) {
                iterator.remove();
            }
        }
    }

    // Reload lazar ammo (triggered by CTRL key)
    // Has 2-second delay before ammo is restored
    public
    void reload_lazar()
    {
        // Skip if already full
        if (lazar_mag >= lazar_ammo)
        {
            return;
        }

        // Can't reload while paused
        if (is_paused)
        {
            return;
        }

        // Only reload when completely empty
        if (lazar_mag <= 0)
        {
            // Reload with delay in background thread
            new Thread(() ->
            {
                try
                {
                    Thread.sleep(lazar_reload_delay);  // 2 seconds
                    lazar_mag = lazar_ammo;            // Restore full ammo
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }).start();
        }
    }
    
    // ===========================
    // LAZAR-ZOMBIE COLLISION
    // ===========================

    // Check for lazar hits on zombies
    // Called every 100ms by lazar_collision_t timer
    public void lazar_hits() {
        for (Lazar l : lazars) {
            Rectangle lazar_rect = new Rectangle(l.x, l.y, l.size, l.size);

            for (Zomb z : zombs) {
                Rectangle z_rect = new Rectangle(z.x, z.y, z.size, z.size);

                if (lazar_rect.intersects(z_rect)) {
                    // Deal damage: base 25 * upgrade multiplier
                    z.health -= (l.stren * lazar_f);
                    blow_lazar(l);  // Visual explosion effect
                }
            }
        }
    }

    // Create explosion effect when lazar hits target
    // Doubles size and turns white
    public
    void blow_lazar(Lazar l)
    {
        if (l.size < 10) l.size *= 2;  // Expand (only once)
        l.body_color = set.lazar_blow_color;    // White
        l.shadow_color = set.lazar_blow_color;
        l.is_hit = true;  // Mark for removal
    }

    // Remove lazars that have hit targets
    // Called every 200ms by lazar_rem_t timer
    public
    void rem_hits()
    {
        Iterator<Lazar> iterator = lazars.iterator();
        while (iterator.hasNext())
        {
            Lazar l = iterator.next();
            if (l.is_hit)
            {
                iterator.remove();
            }
        }
    }

    // ===========================
    // ZOMBIE DEATH & REWARDS
    // ===========================

    // Check for zombie deaths and award currency
    // Called every frame from update()
    public
    void is_z_dead()
    {
        for (Zomb z : zombs)
        {
            // Check if just died (health <= 0 and not already marked)
            if (z.health <= 0 && !z.is_dead)
            {
                z.speed = 0;                      // Stop movement
                z.body_color = set.z_dead_color;  // Dark green
                z.is_dead = true;                 // Mark as dead

                // Reward player
                z_kills++;           // Increment kill counter
                ZZZ += ZZZ_unit;     // +$44 currency
            }
        }
    }

    // ===========================
    // UPGRADE SYSTEM
    // ===========================

    // Purchase weapon upgrade (triggered by U key)
    // Doubles damage multiplier, upgrade cost, and ammo capacity
    public
    void upgrade_blaster()
    {
        if (ZZZ >= lazar_uprice)
        {
            ZZZ -= lazar_uprice;      // Pay the cost
            lazar_f *= 2;             // Double damage (25 -> 50 -> 100 -> ...)
            lazar_uprice *= 2;        // Double next upgrade cost ($400 -> $800 -> ...)
            lazar_ammo *= 2;          // Double ammo capacity (8 -> 16 -> ...)
        }
    }

    // Get current weapon damage for UI display
    public
    int lazar_power()
    {
        return 25 * lazar_f;  // Base damage * multiplier
    }
    

    // ===========================
    // BOUNDS CHECKING
    // ===========================

    // Clamp all entities to screen boundaries
    // Called every frame from update()
    private
    void keep_in_bounds()
    {
        // Clamp player to screen edges
        if (p.x < 0) p.x = 0;
        if (p.x + p.size > screen_width) p.x = screen_width - p.size;
        if (p.y < 0) p.y = 0;
        if (p.y + p.size > screen_height) p.y = screen_height - p.size;

        // Clamp all zombies to screen edges
        for (Zomb z : zombs) {
            if (z.x < 0) z.x = 0;
            if (z.x + z.size > screen_width) z.x = screen_width - z.size;
            if (z.y < 0) z.y = 0;
            if (z.y + z.size > screen_height) z.y = screen_height - z.size;
        }
    }

    // Increase speeds as game progresses (currently disabled)
    // Would make game harder over time
    private
    void speed_inc()
    {
        if (z_wave >= zwf)  // After wave 5
        {
            p.speed += 0.3;
            for (Zomb z : zombs)
            {
                z.speed += 0.5;
            }
        }
    }

    // ===========================
    // ZOMBIE AI
    // ===========================

    // Main zombie chase AI - called every 100ms by z_move_t timer
    // Each zombie moves toward player using normalized direction vector
    // Also pushes zombies apart to prevent clumping
    protected
    void chase()
    {
        if (!zombs.isEmpty())
        {
            for (Zomb z : zombs) {

                // ===========================
                // DIRECTION CALCULATION
                // ===========================
                // Calculate vector from zombie to player
                double dx = (double)p.x - z.x;
                double dy = (double)p.y - z.y;

                // Calculate distance (vector length)
                double L = Math.sqrt((dx*dx) + (dy*dy));
                if (L == 0.0) continue;  // Skip if on top of player

                // Normalize direction vector (unit vector)
                double ux = dx / L;
                double uy = dy / L;

                // Scale by speed and move
                double xval = ux * z.speed;
                double yval = uy * z.speed;
                z.x += (int)xval;
                z.y += (int)yval;

                // ===========================
                // SEPARATION (ANTI-BUNCHING)
                // ===========================
                // Push zombies apart if too close to each other
                for (Zomb other : zombs)
                {
                    if (other != z)
                    {
                        double distX = other.x - z.x;
                        double distY = other.y - z.y;
                        double dist = Math.sqrt(distX * distX + distY * distY);

                        // If closer than 1.5x zombie size, push apart
                        if (dist < z.size * 1.5)
                        {
                            z.x -= distX * 0.05;  // Gentle push
                            z.y -= distY * 0.05;
                        }

                        // COMMENTED OUT: Random jitter (was causing erratic movement)
                        // if ( rng(1, 10) == 2) // 1/10 chance
                        // {
                        //     int n = rng(1,10);
                        //     double fac = 0.55;
                        //     if (n%2==0)
                        //     {
                        //         z.x -= fac;
                        //         z.y += fac;
                        //     } else
                        //     {
                        //         z.x += fac;
                        //         z.y -= fac;
                        //     }
                        // }
                    }
                }
            }
        }
    }


    // ===========================
    // PAUSE SYSTEM
    // ===========================

    // Pause game - freeze all movement and stop healing
    // Called by Input when ESC pressed
    public
    void pause_game()
    {
        // Freeze player
        p.speed = 0.0;

        // Freeze all zombies
        for (Zomb z : zombs)
        {
            z.speed = 0.0;
        }

        // Stop healing during pause
        heal_t.stop();
    }

    // Resume game - restore all movement speeds
    // Called by Input when ESC pressed again
    public
    void resume_game()
    {
        // Restore player speed
        p.speed = p_speed;

        // Restore zombie speeds
        for (Zomb z : zombs)
        {
            z.speed = z_speed;
        }

        // Resume healing
        heal_t.start();
    }


    // ===========================
    // UTILITY
    // ===========================

    // Generate random int in range [min, max)
    private
    int rng(int min, int max)
    {
        return rand.nextInt(max-min)+min;
    }

} // END CLASS //
