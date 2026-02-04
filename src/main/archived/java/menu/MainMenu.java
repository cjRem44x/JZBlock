package menu;

// ============================================================================
// MainMenu.java - Main Menu Screen
// ============================================================================
// Displays the initial menu screen when the game launches. Provides buttons
// for starting the game, accessing settings (not implemented), and exiting.
//
// Features:
//   - Background image loaded from resources
//   - Game title displayed with stylized font
//   - Three menu buttons: Play Game, Settings, Exit
//   - Transparent/undecorated window for cleaner look
//
// Flow:
//   1. Main.java creates MainMenu and calls launcher() + build()
//   2. Menu is displayed with background image
//   3. User clicks "Play Game"
//   4. Menu window is disposed and GameLauncher.start() is called
//   5. Game window opens and gameplay begins
//
// Button Actions:
//   - Play Game: Disposes menu, starts game via GameLauncher.start()
//   - Settings: Placeholder (prints to console)
//   - Exit: Terminates application via System.exit(0)
// ============================================================================

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
//
import game.*;
import states.*;

public class MainMenu
    extends JPanel
{
    // ===========================
    // LAYOUT CONSTANTS
    // ===========================
    public static final int SCREEN_WIDTH = 800;       // Menu window width
    public static final int SCREEN_HEIGHT = 600;      // Menu window height
    public static final int BUTTON_WIDTH = 250;       // Button width
    public static final int BUTTON_HEIGHT = 50;       // Button height

    // ===========================
    // FIELDS
    // ===========================
    private JFrame f = new JFrame();                  // Menu window frame
    private GameStates g_state;                       // Current game state
    private JButton startButton, optionsButton, exitButton;  // Menu buttons

    // Theme properties (easily adjustable for reskinning)
    private Color backgroundColor = new Color(0, 0, 0);      // Black background
    private Color buttonColor = new Color(100, 100, 200);    // Blue-ish buttons
    private Color textColor = Color.WHITE;                    // White text
    private Font buttonFont = new Font("Arial Unicode MS", Font.BOLD, 25);
    private Font titleFont = new Font("Arial Unicode MS", Font.BOLD, 100);

    private JLabel bg_lbl = new JLabel(get_mm_img()); // Background image label
    private GameLauncher GL;                           // Reference to game launcher
    private JLabel titleLabel;                         // Title text label 

    // ===========================
    // DEPENDENCY INJECTION
    // ===========================
    // Store reference to GameLauncher so we can start the game from menu
    public
    void launcher(GameLauncher GL) {
        this.GL = GL;
    }

    // ===========================
    // RESOURCE LOADING
    // ===========================
    // Load and scale the background image for the main menu
    public
    ImageIcon get_mm_img() {
        final var img_path = System.getProperty("user.dir")+"../../../res/img/main_menu_img.png";
        ImageIcon org = new ImageIcon(img_path);
        // Scale image to fit menu window size
        Image scaled = org.getImage().getScaledInstance(SCREEN_WIDTH, SCREEN_HEIGHT, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    // ===========================
    // TITLE DISPLAY
    // ===========================
    // Add the game title to the top of the menu screen
    public
    void addTitle(String titleText) {
        titleLabel = new JLabel(titleText);
        titleLabel.setFont(titleFont != null ? titleFont : new Font("Arial", Font.BOLD, 24));

        // Calculate text dimensions for centering
        FontMetrics metrics = this.getFontMetrics(titleLabel.getFont());
        int textWidth = metrics.stringWidth(titleText);
        int textHeight = metrics.getHeight();

        // Center horizontally, place near top
        int x = (SCREEN_WIDTH - textWidth) / 2;
        int y = 80;

        titleLabel.setBounds(x, y, textWidth, textHeight);
        titleLabel.setForeground(textColor != null ? textColor : Color.BLACK);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        this.add(titleLabel);
        this.repaint();
    }

    // ===========================
    // MENU BUTTONS
    // ===========================
    // Create and configure all menu buttons with their action listeners
    public
    void add_btns() {
        // Create three centered buttons stacked vertically
        startButton = createButton("Play Game", SCREEN_WIDTH/2 - BUTTON_WIDTH/2, 200);
        optionsButton = createButton("Settings", SCREEN_WIDTH/2 - BUTTON_WIDTH/2, 280);
        exitButton = createButton("Exit", SCREEN_WIDTH/2 - BUTTON_WIDTH/2, 360);

        // Make buttons transparent to show background
        startButton.setOpaque(false);
        optionsButton.setOpaque(false);
        exitButton.setOpaque(false);

        // ===========================
        // BUTTON ACTION: PLAY GAME
        // ===========================
        // Closes menu and launches the game
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                f.setVisible(false);  // Hide menu window
                kill();               // Dispose of menu resources
                g_state = GameStates.IN_GAME;
                GL.start(g_state);    // Start the actual game
            }
        });

        // ===========================
        // BUTTON ACTION: SETTINGS
        // ===========================
        // Placeholder for settings menu (not yet implemented)
        optionsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Options button clicked!");
                // TODO: Implement showOptionsMenu();
            }
        });

        // ===========================
        // BUTTON ACTION: EXIT
        // ===========================
        // Terminates the application
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Exit button clicked!");
                System.exit(0);
            }
        });

        // Add buttons to panel
        this.add(startButton);
        this.add(optionsButton);
        this.add(exitButton);
    }

    // Helper method to create a styled button
    private
    JButton createButton(String text, int x, int y) {
        JButton button = new JButton(text);
        button.setBounds(x, y, BUTTON_WIDTH, BUTTON_HEIGHT);
        button.setBackground(buttonColor);
        button.setForeground(textColor);
        button.setFont(buttonFont);
        button.setFocusPainted(false);   // No focus highlight
        button.setBorderPainted(false);  // No border
        return button;
    }
    
    
    // ===========================
    // WINDOW BUILDING
    // ===========================
    // Build and display the main menu window
    public
    void build(GameStates g_state) {
        this.g_state = g_state;

        // Add UI components
        addTitle(GL.title());  // Game title at top
        add_btns();            // Menu buttons

        // Window decorations
        f.setUndecorated(true);  // Remove title bar for cleaner look

        // Panel configuration
        this.setLayout(null);  // Absolute positioning
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));

        // Add background image (added last so it's behind other components)
        bg_lbl.setBounds(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        this.add(bg_lbl);

        this.setBackground(backgroundColor);

        // Frame configuration
        f.setIconImage(GL.icon());
        f.setTitle(GL.title());
        f.add(this);
        f.pack();
        f.setResizable(true);
        f.setVisible(true);
        f.setLocationRelativeTo(null);  // Center on screen
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // ===========================
    // CLEANUP
    // ===========================
    // Dispose of menu window and request garbage collection
    public
    void kill()
    {
        f.dispose();   // Release window resources
        System.gc();   // Suggest garbage collection
    }

}
