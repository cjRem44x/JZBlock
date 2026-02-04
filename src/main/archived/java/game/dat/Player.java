// AUTHOR: cjRem44x //
//
// ============================================================================
// Player.java - Player Entity Data Class
// ============================================================================
// Simple data container (POJO) for the player character. Holds all properties
// that define the player's state in the game world.
//
// The player is rendered as a colored square with a face emoji (😃).
// Player starts at center of screen with 100 health.
//
// Properties:
//   - health: Current health points (0-100, dies at 0)
//   - x, y: Position coordinates on screen (top-left of player square)
//   - size: Width/height of player square (default: 32px)
//   - speed: Movement speed in pixels per update tick
//   - body_color: The fill color of the player square (default: purple)
// ============================================================================
//
package game.dat;

import java.awt.*;

public class Player
{
    public int    health;      // Current health points (0-100)
    public int    x, y, size;  // Position (x,y) and square size in pixels
    public double speed;       // Movement speed in pixels per tick
    public Color  body_color;  // Fill color for rendering
}
