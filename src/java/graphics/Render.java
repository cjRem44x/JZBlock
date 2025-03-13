// AUTHOR: cjRem44x //
//
package graphics;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;
//
import core.*;
import dat.*;

public class Render 
{
    // FIELDS //
    //
    private Engine e;


    // ACCESS //
    //
    // get engine
    public 
    void engine(Engine e) 
    {
        this.e = e;
    }
    
    
    // RENDER //
    //
    public 
    void update(Graphics g) 
    {
        // draw player
        g.setColor(e.p.body_color);
        g.fillRect(e.p.x, e.p.y, e.p.size, e.p.size);
        //
        // draw zombs
        for (Zomb z : e.zombs) 
        {
            g.setColor(z.body_color);
            g.fillRect(z.x, z.y, z.size, z.size);
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