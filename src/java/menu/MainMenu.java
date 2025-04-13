package menu;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
//
import states.*;

public class MainMenu 
    extends JPanel 
{
    public static final int SCREEN_WIDTH = 800, 
                            SCREEN_HEIGHT = 600,
                            BUTTON_WIDTH = 250,
                            BUTTON_HEIGHT = 50;
    private JFrame f =  new JFrame();
    private GameStates g_state;
    private JButton startButton, optionsButton, exitButton;
    // Theme properties (adjustable)
    private Color backgroundColor = new Color(50, 50, 50);
    private Color buttonColor = new Color(100, 100, 200);
    private Color textColor = Color.WHITE;
    private Font buttonFont = new Font("Arial Unicode MS", Font.BOLD, 25);
    ///
    private JLabel bg_lbl = new JLabel("BG");
    
    public 
    void add_btns() {
        // Create buttons
        startButton = createButton("Play Game 🤪", SCREEN_WIDTH/2 - BUTTON_WIDTH/2, 200);
        optionsButton = createButton("Settings", SCREEN_WIDTH/2 - BUTTON_WIDTH/2, 280);
        exitButton = createButton("Exit", SCREEN_WIDTH/2 - BUTTON_WIDTH/2, 360);
        
        // Add action listeners
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                g_state = GameStates.IN_GAME;
            }
        });
        
        optionsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Add your options menu logic here
                System.out.println("Options button clicked!");
                // Example: showOptionsMenu();
            }
        });
        
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Exit the application
                System.out.println("Exit button clicked!");
                System.exit(0);
            }
        });
        
        // Add buttons to panel
        this.add(startButton);
        this.add(optionsButton);
        this.add(exitButton);
    }
    
    private 
    JButton createButton(String text, int x, int y) {
        JButton button = new JButton(text);
        button.setBounds(x, y, BUTTON_WIDTH, BUTTON_HEIGHT);
        button.setBackground(buttonColor);
        button.setForeground(textColor);
        button.setFont(buttonFont);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        return button;
    }
    
    
    public 
    void build(GameStates g_state) {
        this.g_state = g_state;
        
        add_btns();
        f.setUndecorated(true);

        this.setLayout(null);
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        //this.setBackground(backgroundColor);
        bg_lbl.setBounds(0,0, SCREEN_WIDTH, SCREEN_HEIGHT);
        this.add(bg_lbl);
        
        f.add(this);
        f.pack();
        f.setResizable(true);
        f.setVisible(true);
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    
    }

    public 
    void kill() 
    {
        f.dispose();
        System.gc();
    }
}
