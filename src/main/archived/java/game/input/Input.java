// AUTHOR: cjRem44x //
//
// ============================================================================
// Input.java - Keyboard Input Handler
// ============================================================================
// Handles all keyboard input for player movement, shooting, and game controls.
// Implements KeyListener for keyboard events and WindowListener for close events.
//
// Control Scheme (Default):
//   Movement:       W/A/S/D keys
//   Shooting:       Arrow keys (direction of shot)
//   Speed Boost:    SPACE (hold for boost, 5s duration, 5s recharge)
//   Reload:         CTRL (reloads lazar ammo, 2s delay)
//   Upgrade:        U (upgrade blaster if enough currency)
//   Pause/Resume:   ESC
//   Restart:        R (only when dead)
//
// Alternate Control Scheme (Config.switch_controls = true):
//   Movement:       Arrow keys
//   Shooting:       W/A/S/D keys
//
// Input Processing:
//   - Uses keyPressed/keyReleased for state tracking
//   - Movement timer polls key states at p_throt interval (120ms)
//   - Allows diagonal movement (multiple keys held)
//   - Speed boost has cooldown system with duration and recharge timers
//
// Speed Boost System:
//   - Hold SPACE to activate (doubles player speed)
//   - Lasts 5 seconds or until released
//   - 5 second recharge before can use again
// ============================================================================
//
package game.input;

import java.awt.event.*;
import javax.swing.Timer;
//
import game.core.*;
import game.dat.*;
import game.graphics.*;
import game.opt.*;

public class Input
implements KeyListener, WindowListener
{
    // ===========================
    // FIELDS
    // ===========================
    private Window win;       // Window reference for attaching listeners
    private Engine e;         // Engine reference for game actions
    private Config set;       // Config reference for control scheme
    protected Timer set_t;    // Unused timer (legacy)

    // ===========================
    // KEY BINDINGS
    // ===========================
    // Movement keys (default: WASD)
    private int W     = KeyEvent.VK_W;
    private int A     = KeyEvent.VK_A;
    private int S     = KeyEvent.VK_S;
    private int D     = KeyEvent.VK_D;
    // Shooting keys (default: Arrow keys)
    private int RARR  = KeyEvent.VK_RIGHT;
    private int LARR  = KeyEvent.VK_LEFT;
    private int UARR  = KeyEvent.VK_UP;
    private int DARR  = KeyEvent.VK_DOWN;
    // Action keys
    private int SPACE = KeyEvent.VK_SPACE;    // Speed boost
    private int U     = KeyEvent.VK_U;        // Upgrade weapon
    private int ESC   = KeyEvent.VK_ESCAPE;   // Pause/resume
    private int CTRL  = KeyEvent.VK_CONTROL;  // Reload

    // ===========================
    // KEY STATES
    // ===========================
    // Movement state flags (true while held)
    private boolean up_press    = false;
    private boolean dwn_press   = false;
    private boolean right_press = false;
    private boolean left_press  = false;
    // Shooting state flags
    private boolean rarr_press  = false;
    private boolean larr_pres   = false;
    private boolean uarr_press  = false;
    private boolean darr_press  = false;
    // Boost state
    private boolean space_press = false;
    private boolean speed_boost = false;  // Currently boosting

    // ===========================
    // SPEED BOOST SYSTEM
    // ===========================
    private long boostStartTime = 0;            // When boost was activated
    private final long BOOST_DURATION = 5000;   // Max boost duration: 5 seconds
    private final long RECHARGE_TIME = 5000;    // Cooldown after boost: 5 seconds
    private boolean canUseBoost = true;         // True if boost is available
    private boolean energyDepleted = false;     // True during recharge period


    // ===========================
    // DEPENDENCY INJECTION
    // ===========================
    //
    // Attach this input handler to the window
    public void win(Window win)
    {
        this.win = win;
        this.win.get().addKeyListener(this);    // Listen for keyboard events
        this.win.get().addWindowListener(this); // Listen for window close
    }
    //
    // Inject engine dependency and start the movement polling timer
    public void engine(Engine e)
    {
        this.e = e;
        startMovementLoop();  // Start timer that polls key states
    }
    //
    // Inject config and apply control scheme
    public void config(Config set)
    {
        this.set = set;
        // If switch_controls is true, swap movement and shooting keys
        // Default: WASD = move, Arrows = shoot
        // Switched: Arrows = move, WASD = shoot
        if (set.switch_controls)
        {
            // Shooting becomes WASD
            UARR  = KeyEvent.VK_W;
            LARR  = KeyEvent.VK_A;
            DARR  = KeyEvent.VK_S;
            RARR  = KeyEvent.VK_D;
            // Movement becomes Arrow keys
            W     = KeyEvent.VK_UP;
            A     = KeyEvent.VK_LEFT;
            S     = KeyEvent.VK_DOWN;
            D     = KeyEvent.VK_RIGHT;
            // SPACE stays the same
            SPACE = KeyEvent.VK_SPACE;
        }
    }


    // ===========================
    // KEY EVENT HANDLERS
    // ===========================
    //
    // Handle key press events - set state flags and trigger instant actions
    @Override public void keyPressed(KeyEvent e)
    {
        int n = e.getKeyCode();

        // ===========================
        // INSTANT ACTIONS (not polled)
        // ===========================

        // ESC - Toggle pause/resume
        if (n == ESC)
        {
            if (this.e.is_paused)
            {
                this.e.resume_game();
                this.e.is_paused = false;
            } else
            {
                this.e.pause_game();
                this.e.is_paused = true;
            }
        }

        // CTRL - Reload lazar ammo
        if (n == CTRL)
        {
            this.e.reload_lazar();
        }

        // R - Restart game (only when dead)
        if (n == KeyEvent.VK_R && !this.e.is_p_alive)
        {
            this.e.restart();
            this.win.clear();  // Clear UI elements for fresh start
        }

        // U - Upgrade blaster (if enough currency)
        if (n == U) {
            this.e.upgrade_blaster();
        }

        // ===========================
        // STATE FLAGS (polled by movement loop)
        // ===========================

        // Movement keys
        if (n == W) up_press      = true;
        if (n == S) dwn_press     = true;
        if (n == A) left_press    = true;
        if (n == D) right_press   = true;

        // Boost key
        if (n == SPACE) space_press = true;

        // Shooting keys
        if (n == RARR) rarr_press = true;
        if (n == LARR) larr_pres  = true;
        if (n == UARR) uarr_press = true;
        if (n == DARR) darr_press = true;
    }
    //
    // Handle key release events - clear state flags
    @Override public void keyReleased(KeyEvent e)
    {
        int n = e.getKeyCode();

        // Movement keys
        if (n == W) up_press      = false;
        if (n == S) dwn_press     = false;
        if (n == A) left_press    = false;
        if (n == D) right_press   = false;

        // Boost key - special handling to end boost early
        if (n == SPACE) {
            space_press = false;
            // If boost was active, end it early and start recharge
            if (speed_boost) {
                this.e.p.speed /= 2;      // Reset speed to normal
                speed_boost = false;
                energyDepleted = true;    // Start recharge timer
            }
        }

        // Shooting keys
        if (n == RARR) rarr_press = false;
        if (n == LARR) larr_pres  = false;
        if (n == UARR) uarr_press = false;
        if (n == DARR) darr_press = false;
    }
    // ===========================
    // MOVEMENT POLLING LOOP
    // ===========================
    // Timer that runs at p_throt interval (120ms) to process held keys
    // This allows smooth continuous movement and shooting while keys are held
    private void startMovementLoop()
    {
        new javax.swing.Timer(e.p_throt, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {

                // ===========================
                // PROCESS MOVEMENT
                // ===========================
                // Apply movement based on which keys are held
                // Multiple keys can be held for diagonal movement
                if (up_press)    e.p.y -= e.p.speed;  // Move up
                if (dwn_press)   e.p.y += e.p.speed;  // Move down
                if (left_press)  e.p.x -= e.p.speed;  // Move left
                if (right_press) e.p.x += e.p.speed;  // Move right

                // ===========================
                // PROCESS SHOOTING
                // ===========================
                // Fire lazars in direction of held arrow keys
                if (rarr_press) e.blast("right");
                if (larr_pres)  e.blast("left");
                if (uarr_press) e.blast("up");
                if (darr_press) e.blast("down");

                // ===========================
                // SPEED BOOST SYSTEM
                // ===========================

                // Activate boost when SPACE held and boost is available
                if (space_press && canUseBoost && !speed_boost) {
                    e.p.speed *= 2;              // Double speed
                    speed_boost = true;
                    boostStartTime = System.currentTimeMillis();
                    canUseBoost = false;         // Can't use again until recharged
                }

                // Auto-end boost after BOOST_DURATION (5 seconds)
                if (speed_boost && System.currentTimeMillis() - boostStartTime >= BOOST_DURATION) {
                    e.p.speed /= 2;              // Reset to normal speed
                    speed_boost = false;
                    energyDepleted = true;       // Start recharge period
                }

                // Re-enable boost after recharge period completes
                if (energyDepleted && System.currentTimeMillis() - boostStartTime >= BOOST_DURATION + RECHARGE_TIME) {
                    canUseBoost = true;          // Boost available again
                    energyDepleted = false;
                }
            }
        }).start();
    }


    // ===========================
    // WINDOW LISTENER
    // ===========================
    // Handle window close event - exit application
    @Override public void windowClosing(WindowEvent e)
    {
        System.exit(0);
    }

    // ===========================
    // UNUSED LISTENER METHODS
    // ===========================
    // Required by interfaces but not used
    @Override public void windowOpened(WindowEvent e) {}
    @Override public void windowClosed(WindowEvent e) {}
    @Override public void windowIconified(WindowEvent e) {}
    @Override public void windowDeiconified(WindowEvent e) {}
    @Override public void windowActivated(WindowEvent e) {}
    @Override public void windowDeactivated(WindowEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}
