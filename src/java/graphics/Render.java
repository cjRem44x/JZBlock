// AUTHOR: cjRem44x //
//
package graphics;

import java.awt.*; // Import AWT library
import java.util.List; // Import List
import java.util.ArrayList; // Import ArrayList
//
import core.*; // Import core package
import dat.*; // Import dat package

public class Render 
{
    // FIELDS //
    //
    private Engine e; // Engine object


    // ACCESS //
    //
    // Method to set the engine object
    public 
    void engine(Engine e) 
    {
        this.e = e;
    }
    
    
    // RENDER //
    //
    // Method to update the graphics by rendering player, zombies, and lazars
    public 
    void update(Graphics g) 
    {
        // draw player
        g.setColor(e.p.body_color);
        g.fillRect(e.p.x, e.p.y, e.p.size, e.p.size);
        g.setColor(Color.black);
        g.setFont( new Font("Arial Unicode MS", Font.PLAIN|Font.BOLD, 31));
        g.drawString("😃", e.p.x, e.p.y+e.p.size-4);
        //
        // draw zombs
        for (Zomb z : e.zombs) 
        {
            g.setColor(z.body_color);
            g.fillRect(z.x, z.y, z.size, z.size);
            g.setColor(Color.black);
            g.setFont( new Font("Arial Unicode MS", Font.PLAIN|Font.BOLD, 31));
            g.drawString("😈", z.x, z.y+z.size-4);
        }
        //
        // draw lazars
        for (Lazar l : e.lazars) 
        {
            // Draw shadow first to appear behind lazar
            g.setColor(l.shadow_color);
            g.fillRect(l.x + 3, l.y, l.size+2, l.size+1);  // Shadow offset can be adjusted

            g.setColor(l.body_color);
            g.fillRect(l.x, l.y, l.size, l.size);
        }
    }

}