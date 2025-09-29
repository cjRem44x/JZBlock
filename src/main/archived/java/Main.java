// AUTHOR: cjRem44x //
//
import game.*;
import menu.*;
//
import states.GameStates;

public class Main 
{
    /// FIELDS ///
    ///
    static GameStates game_state;
    ///
    static final MainMenu     MAIN_MENU = new MainMenu();
    static final GameLauncher GL        = new GameLauncher();


    /// ENTRY ///
    ///
    public static 
    void main(final String[] args) 
    {
        // launch game to MAIN_MENU
        game_state = GameStates.MAIN_MENU;
        MAIN_MENU.launcher(GL);
        MAIN_MENU.build(game_state);
    }

} // END CLASS //
