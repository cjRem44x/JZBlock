// AUTHOR: cjRem44x //
//
// ============================================================================
// Render.java - Graphics Rendering System
// ============================================================================
// Handles all visual rendering of game entities (player, zombies, lazars).
// This class is called from Window.paintComponent() on each frame refresh.
//
// Rendering Order (back to front):
//   1. Player square (purple) with 😃 emoji overlay
//   2. Zombie squares (green) with 😈 emoji overlay
//   3. Lazar projectiles with shadow effect
//
// The render system uses Java AWT Graphics for immediate-mode rendering.
// Each entity is drawn as a filled rectangle with optional emoji decorations.
//
// Visual Effects:
//   - Player: Colored square with smiling face emoji
//   - Zombies: Colored squares with devil face emoji (turn dark when dead)
//   - Lazars: Small squares with offset shadow for depth effect
//   - Lazar explosion: Size doubles and turns white on hit
// ============================================================================
//
package game.graphics;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;
//
import game.core.*;
import game.dat.*;

public class Render
{
    // FIELDS //
    //
    private Engine e;  // Reference to game engine (source of entity data)


    // ACCESS //
    //
    // Inject the engine dependency for accessing game state
    public
    void engine(Engine e)
    {
        this.e = e;
    }


    // RENDER //
    //
    // Main rendering method - called every frame by Window.paintComponent()
    // Draws all game entities to the provided Graphics context
    public
    void update(Graphics g)
    {
        // ===========================
        // DRAW PLAYER
        // ===========================
        // Draw player as a colored square with emoji face overlay
        g.setColor(e.p.body_color);
        g.fillRect(e.p.x, e.p.y, e.p.size, e.p.size);
        // Draw emoji face on top of player square
        g.setColor(Color.black);
        g.setFont( new Font("Arial Unicode MS", Font.PLAIN|Font.BOLD, 31));
        g.drawString("😃", e.p.x, e.p.y+e.p.size-4);

        // ===========================
        // DRAW ZOMBIES
        // ===========================
        // Iterate through all zombies and render each one
        for (Zomb z : e.zombs)
        {
            g.setColor(z.body_color);  // Green when alive, dark green when dead
            g.fillRect(z.x, z.y, z.size, z.size);
            // Draw devil emoji on top of zombie square
            g.setColor(Color.black);
            g.setFont( new Font("Arial Unicode MS", Font.PLAIN|Font.BOLD, 31));
            g.drawString("😈", z.x, z.y+z.size-4);
        }

        // ===========================
        // DRAW LAZARS (PROJECTILES)
        // ===========================
        // Draw each lazar with a shadow effect for depth
        for (Lazar l : e.lazars)
        {
            // Draw shadow first (offset to right) so it appears behind lazar
            g.setColor(l.shadow_color);
            g.fillRect(l.x + 3, l.y, l.size+2, l.size+1);

            // Draw main lazar body on top
            g.setColor(l.body_color);
            g.fillRect(l.x, l.y, l.size, l.size);
        }
    }

}
