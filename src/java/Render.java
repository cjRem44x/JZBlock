// AUTHOR: cjRem44x //
//
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class Render {
    // FIELDS //
    //
    private Player     p;
    private List<Zomb> zombs;


    // ACCESS //
    //
    // get player
    public void player(Player p) {
        this.p = p;
    }
    //
    // get zombs
    public void zombs(List<Zomb> zombs) {
        this.zombs = zombs;
    }
    
    
    // RENDER //
    //
    public void update(Graphics g) {
        // draw player
        g.setColor(p.body_color);
        g.fillRect(p.x, p.y, p.size, p.size);
        //
        // draw zombs
        for (Zomb z : zombs) {
            g.setColor(z.body_color);
            g.fillRect(z.x, z.y, z.size, z.size);
        }
    }
}
