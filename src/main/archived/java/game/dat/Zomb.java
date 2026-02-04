// AUTHOR: cjRem44x //
//
// ============================================================================
// Zomb.java - Zombie Entity Data Class
// ============================================================================
// Data container for zombie enemies. Each zombie instance tracks its own
// position, health, and state. Zombies chase the player using simple AI
// (see Engine.chase()) and deal damage on collision.
//
// The zombie is rendered as a green square with a devil emoji (😈).
// Zombies spawn at random positions and chase the player relentlessly.
//
// Properties:
//   - x, y: Position coordinates on screen
//   - size: Width/height of zombie square (default: 32px)
//   - last_hit_t: Timestamp of last damage dealt to player (1-second cooldown)
//   - speed: Movement speed toward player
//   - body_color: Fill color (green when alive, dark green when dead)
//   - health: Current health points (starts at 100, increases each wave)
//   - is_dead: Death flag - when true, zombie stops and awaits cleanup
//
// Zombie Behavior:
//   - Spawns at random position when wave starts
//   - Chases player using normalized direction vector
//   - Deals 25 damage per hit with 1-second cooldown
//   - Health increases by 25 each wave
//   - Killed zombies reward player with $44 (ZZZ currency)
// ============================================================================
//
package game.dat;

import java.awt.*;

public class Zomb
{
    public int     x, y, size;       // Position and square size in pixels
    public long    last_hit_t = 0;   // Timestamp of last player hit (for cooldown)
    public double  speed;            // Chase speed in pixels per tick
    public Color   body_color;       // Fill color for rendering
    public int     health = 100;     // Current health points
    public boolean is_dead = false;  // True when zombie has been killed
}
