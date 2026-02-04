// AUTHOR: cjRem44x //
//
// ============================================================================
// Lazar.java - Projectile Entity Data Class
// ============================================================================
// Data container for the player's projectiles (lazars). Each lazar travels
// in a straight line until it hits a zombie or goes off-screen.
//
// Visual: Pink square with a lighter pink shadow offset for depth effect.
// When hitting a zombie, the lazar "blows up" (doubles in size, turns white).
//
// Properties:
//   - x, y: Position coordinates (spawns at player center)
//   - size: Width/height of projectile (default: 5px, doubles on hit)
//   - speed: Travel speed in pixels per tick (default: 20)
//   - body_color: Main projectile color (pink)
//   - shadow_color: Shadow effect color (light pink)
//   - dir: Direction of travel ("up", "down", "left", "right")
//   - is_hit: Flag set when projectile hits a zombie (triggers removal)
//   - stren: Base damage dealt to zombies (25, multiplied by upgrade factor)
//
// Ammo System:
//   - Player has limited ammo (default: 8 shots per magazine)
//   - Press CTRL to reload (2-second delay)
//   - Upgrading increases ammo capacity and damage
// ============================================================================
//
package game.dat;

import java.awt.*;

public class Lazar
{
    public int     x, y, size;                  // Position and square size
    public double  speed;                        // Travel speed in pixels per tick
    public Color   body_color, shadow_color;     // Main and shadow colors for rendering
    public String  dir = "up";                   // Travel direction: "up"/"down"/"left"/"right"
    public boolean is_hit = false;               // True when projectile hit a zombie
    public int     stren = 25;                   // Base damage (multiplied by lazar_f)
}
