// AUTHOR: cjRem44x //
//
package dat;

import java.awt.*;

public class Zomb 
{
    public int     x, y, size; // Position and size
    public long    last_hit_t = 0; // Last hit time
    public double  speed; // Speed
    public Color   body_color; // Color
    public int     health = 100; // Health
    public boolean is_dead = false; // Dead flag
}