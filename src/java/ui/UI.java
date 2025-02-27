// AUTHOR: cjRem44x //
//
package ui;

import java.awt.*;
import java.util.Random;
import javax.swing.*;
//
import core.Engine;
import graphics.Window;

public class UI 
{
    // FIELDS //
    //
    private Random rand = new Random();
    private Color bg             = new Color(0, 10, 20), 
                  go_bg          = new Color(38, 10, 0),
                  p_health_color = new Color(255, 70, 157),
                  go_color       = new Color(255, 0, 0),
                  zkill_color    = new Color(179, 255, 0),
                  zwave_color    = new Color(255, 119, 0),
                  reload_color   = new Color(43, 234, 244),
                  ZZZ_color      = new Color(225, 205, 255),
                  stats_color    = new Color(255, 0, 255);
    // displaying stats
    private JLabel p_health_lbl, go_lbl, 
    zkill_lbl, zwave_lbl, 
    reload_lbl, ZZZ_lbl, 
    stats_lbl;
    //
    private Window win;
    private Engine e;


    // ACCESS //
    //
    public 
    void window(Window win) 
    {
        this.win = win;
    }
    //
    public 
    void engine(Engine e) 
    {
        this.e = e;
    }

    
    // DISPLAY UI //
    //
    // display
    public 
    void update() 
    {
        p_health();
        z_kills();
        zwave();
        ZZZ();
        stats();

        if (!e.is_p_alive) 
        {
            g_over();
            reload();
        }
    }
    //
    // player health
    private
    void p_health() 
    {
        if (p_health_lbl != null) win.remove(p_health_lbl);
        
        p_health_lbl = new JLabel(Integer.toString(e.p.health));
        p_health_lbl.setFont(new Font("Jokerman", Font.PLAIN, 50));
        p_health_lbl.setOpaque(false);
        p_health_lbl.setForeground(p_health_color);
    
        // Measure text size
        FontMetrics metrics = p_health_lbl.getFontMetrics(p_health_lbl.getFont());
        int width = metrics.stringWidth(p_health_lbl.getText());
        int height = metrics.getHeight();
    
        // Set precise bounds
        p_health_lbl.setBounds(0, 0, width, height);
    
        win.add(p_health_lbl);
    }
    //
    // game over title
    private
    void g_over() 
    {
        zwave();
        go_lbl = new JLabel("Game Over");
        go_lbl.setFont(new Font("Jokerman", Font.PLAIN, 100));
        go_lbl.setOpaque(false);
        go_lbl.setForeground(go_color);
    
        // Measure text size
        FontMetrics metrics = go_lbl.getFontMetrics(go_lbl.getFont());
        int width = metrics.stringWidth(go_lbl.getText());
        int height = metrics.getHeight();
    
        // Set precise bounds
        go_lbl.setBounds((int)((win.width()/2.00)-(width/2.00)), (int)((win.height()/2.00)-(height/2.00)), width, height);
        win.setBackground(go_bg);

        win.add(go_lbl);
    }
    //
    // zombie kills
    private
    void z_kills() 
    {
        if (zkill_lbl != null) win.remove(zkill_lbl);

        zkill_lbl = new JLabel("000");
        zkill_lbl.setFont(new Font("Jokerman", Font.PLAIN, 50));
        zkill_lbl.setOpaque(false);
        zkill_lbl.setForeground(zkill_color);
    
        // Measure text size
        FontMetrics metrics = zkill_lbl.getFontMetrics(zkill_lbl.getFont());
        int width = metrics.stringWidth(zkill_lbl.getText());
        int height = metrics.getHeight();

        zkill_lbl.setText(Integer.toString(e.z_kills));
    
        // Set precise bounds
        zkill_lbl.setBounds((int)((win.width()-width*2.00)), (int)(height/2.00), width, height);

        win.add(zkill_lbl);
    }
    //
    // current wave
    private
    void zwave() 
    {
        if (zwave_lbl != null) win.remove(zwave_lbl);

        zwave_lbl = new JLabel("000");
        zwave_lbl.setFont(new Font("Chiller", Font.PLAIN, 120));
        zwave_lbl.setOpaque(false);
        //zwave_lbl.setForeground(zwave_color);

        // RAINBOW //
        int r = rng(0,255);
        int g = rng(0, 255);
        int b = rng(0, 255); 
        zwave_lbl.setForeground( new Color(r,g,b) );
    
        // Measure text size
        FontMetrics metrics = zwave_lbl.getFontMetrics(zwave_lbl.getFont());
        int width = metrics.stringWidth(zwave_lbl.getText());
        int height = metrics.getHeight();

        zwave_lbl.setText(Integer.toString(e.z_wave));
    
        // Set precise bounds
        zwave_lbl.setBounds((int)((win.width()/2.00)-(width/2.00)), height, width, height);

        win.add(zwave_lbl);
    }
    //
    // reload game
    private 
    void reload() 
    {
        reload_lbl = new JLabel("press [R] to start over");
        reload_lbl.setFont(new Font("Monospace", Font.PLAIN|Font.ITALIC, 50));
        reload_lbl.setOpaque(false);
        reload_lbl.setForeground(reload_color);
    
        // Measure text size
        FontMetrics metrics = reload_lbl.getFontMetrics(reload_lbl.getFont());
        int width = metrics.stringWidth(reload_lbl.getText());
        int height = metrics.getHeight();
    
        // Set precise bounds
        reload_lbl.setBounds((int)((win.width()/2.00)-(width/2.00)), (int)((win.height()*0.75)-(height/2.00)), width, height);

        win.add(reload_lbl);
    }
    //
    // ZZZ 
    private
    void ZZZ() 
    {
        if (ZZZ_lbl != null) win.remove(ZZZ_lbl);
        ZZZ_lbl = new JLabel("000000000");
        ZZZ_lbl.setFont(new Font("Chiller", Font.PLAIN|Font.ITALIC, 50));
        ZZZ_lbl.setOpaque(false);
        ZZZ_lbl.setForeground(ZZZ_color);
    
        // Measure text size
        FontMetrics metrics = ZZZ_lbl.getFontMetrics(ZZZ_lbl.getFont());
        int width = metrics.stringWidth(ZZZ_lbl.getText());
        int height = metrics.getHeight();
    
        ZZZ_lbl.setText("$"+Integer.toString(e.ZZZ));
        // Set precise bounds
        ZZZ_lbl.setBounds((int)(win.width()-width), (int)((win.height()-height)), width, height);

        ZZZ_lbl.setBorder(BorderFactory.createLineBorder(ZZZ_color, 2));
        win.add(ZZZ_lbl);
    }
    //
    //
    private
    void stats() 
    {
        if (stats_lbl != null) 
            win.remove(stats_lbl);

        String s = "Power ("+Integer.toString(e.lazar_power())+") | Upgrade $"+e.lazar_uprice + ",     press [U] to upgrade";
        stats_lbl = new JLabel(s);
        stats_lbl.setFont(new Font("Chiller", Font.PLAIN|Font.ITALIC, 30));
        stats_lbl.setOpaque(false);
        stats_lbl.setForeground(stats_color);
    
        // Measure text size
        FontMetrics metrics = stats_lbl.getFontMetrics(ZZZ_lbl.getFont());
        int width = metrics.stringWidth(stats_lbl.getText());
        int height = metrics.getHeight();
    
        // Set precise bounds
        stats_lbl.setBounds(0, (int)((win.height()-height)), width, height);

        win.add(stats_lbl);
    }
    

    // RANDOMS //
    //
    protected 
    int rng(int min, int max) 
    {
        return rand.nextInt(max-min)+min;
    }
}
