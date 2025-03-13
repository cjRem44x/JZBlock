// AUTHOR: cjRem44x //
//
package opt;

import java.awt.*;

public class Settings 
{
    // FIELDS //
    //
    public final String GAME_TITLE = "JZBlock";
    //
    public int     screen_width = 800, 
                   screen_height = 800,
                   fps = 60;
    public boolean switch_controls = false;
    public Color   p_color     = new Color(137,0,255),
                   z_color     = new Color(0, 255, 1),
                   z_dead_color = new Color(0, 55, 1),
                   lazar_color = new Color(255, 0, 159),
                   lazar_shadow = new Color(252, 146, 212),
                   lazar_blow_color = new Color(255, 255, 255);
    public Color   window_bg = new Color(0, 7, 53);
    public Color  go_bg          = new Color(38, 10, 0),
                  p_health_color = new Color(255, 0 ,0),
                  go_color       = new Color(255, 0, 0),
                  zkill_color    = new Color(255, 0, 0),
                  zwave_color    = new Color(255, 0, 0),
                  reload_color   = new Color(255, 0, 0),
                  ZZZ_color      = new Color(255, 0, 0),
                  stats_color    = new Color(255, 0, 0),
                  pause_color    = new Color(255, 0, 0),
                  lzammo_color   = new Color(255, 0, 0);
}