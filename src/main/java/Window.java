package main.java;

import java.awt.*;
import javax.swing.*;

public class Window 
    extends JPanel
{
    public static Window o = new Window();

    private JFrame frame;


    public Window()
    {
        init_Window(0,0,"");
    }
    public Window(int width, int height, String title)
    {
        init_Window(width, height, title);
    }
    public void init_Window(int width, int height, String title)
    {
        frame = new JFrame();
        this.setLayout(null);
        this.setPreferredSize(
            new Dimension(width, height)
        );
        frame.add(this);
        frame.pack();
        frame.setTitle(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        frame.setAlwaysOnTop(true);
    }
}
