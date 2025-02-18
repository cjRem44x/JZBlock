//  AUTHOR: cjRem44x //
//
import java.awt.*;
import java.security.SecureRandom;
import javax.swing.*;
public class Window extends JPanel {
    // FIELDS //
    //
    private SecureRandom rand = new SecureRandom();
    private JFrame f    = new JFrame();
    private Render rend = null;
    private Engine e;
    private Color bg = new Color(0, 10, 20), 
                  go_bg = new Color(38, 10, 0),
                  p_health_color = new Color(255, 70, 157),
                  go_color = new Color(255, 0, 0),
                  zkill_color = new Color(179, 255, 0),
                  zwave_color = new Color(255, 119, 0),
                  reload_color = new Color(43, 234, 244);
    //
    // displaying stats
    private JLabel p_health_lbl, go_lbl, zkill_lbl, zwave_lbl, reload_lbl;
    //
    private Timer rainbow_t;
   
    // RENDER //
    //
    // paint
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // render graphics
        if (rend != null) rend.update(g);
        else System.out.println("RENDER is NULL");
    }
    //
    // add render
    public void rend(Render rend) {
        this.rend = rend;
    }
    //
    // window refresh
    public void ref() {
        disp_stats();
        this.repaint();
    }
    //
    //
    public int width() {
        return f.getContentPane().getWidth();
    }
    //
    public int height() {
        return f.getContentPane().getHeight();
    }


    // INIT //
    //
    // build window
    public void build() {
        this.setLayout(null);
        f.add(this);
        f.pack();
        f.setVisible(true);
        f.setLocationRelativeTo(null);
        f.setExtendedState(JFrame.MAXIMIZED_BOTH);
        f.setMinimumSize( new Dimension(800, 800) );
        // f.setResizable(false);

        // temp color
        this.setBackground(bg);
    }
    //
    // set size
    public void size(int x, int y) {
        this.setPreferredSize( new Dimension(x, y) );
    }
    //
    //
    public void clear() {
        this.removeAll();
        this.setBackground(bg);
    }

    // ACCESS //
    //
    // get frame
    protected JFrame get() {
        return this.f;
    }
    //
    public void engine(Engine e) {
        this.e = e;
        e.screen_color = bg;
    }


    // DISPLAY //
    //
    // display
    public void disp_stats() {
        p_health();
        z_kills();
        zwave();

        if (!e.is_p_alive) {
            g_over();
            reload();
        }
    }
    //
    // player health
    void p_health() {
        if (p_health_lbl != null) this.remove(p_health_lbl);
        
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
    
        this.add(p_health_lbl);
    }
    //
    // game over title
    void g_over() {
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
        go_lbl.setBounds((int)((width()/2.00)-(width/2.00)), (int)((height()/2.00)-(height/2.00)), width, height);
        this.setBackground(go_bg);

        this.add(go_lbl);
    }
    //
    // zombie kills
    void z_kills() {
        if (zkill_lbl != null) this.remove(zkill_lbl);

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
        zkill_lbl.setBounds((int)((width()-width*2.00)), (int)(height/2.00), width, height);

        this.add(zkill_lbl);
    }
    //
    // zombie kills
    void zwave() {
        if (zwave_lbl != null) this.remove(zwave_lbl);

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
        zwave_lbl.setBounds((int)((width()/2.00)-(width/2.00)), height, width, height);

        this.add(zwave_lbl);
    }
    //
    //
        //
    // zombie kills
    void reload() {
        reload_lbl = new JLabel("press [R] to start over");
        reload_lbl.setFont(new Font("Monospacex", Font.PLAIN|Font.ITALIC, 50));
        reload_lbl.setOpaque(false);
        reload_lbl.setForeground(reload_color);
    
        // Measure text size
        FontMetrics metrics = reload_lbl.getFontMetrics(reload_lbl.getFont());
        int width = metrics.stringWidth(reload_lbl.getText());
        int height = metrics.getHeight();
    
        // Set precise bounds
        reload_lbl.setBounds((int)((width()/2.00)-(width/2.00)), (int)((height()*0.75)-(height/2.00)), width, height);

        this.add(reload_lbl);
    }

    protected int rng(int min, int max) {
        return rand.nextInt(max-min)+min;
    }
}
