// AUTHOR: cjRem44x //
//
import core.*;
import graphics.*;
import dat.*;
import opt.*;
import input.*;
import ui.*;

public class Main 
{
    // FIELDS //
    //
    static final Window    WIN     = new Window();
    static final UI        UI      = new UI();
    static final Input     INP     = new Input();
    static final Render    REND    = new Render();
    static final Settings  SET     = new Settings();
    static final Engine    ENGINE  = new Engine();
    static final Player    PLAYER  = new Player();


    // DRIVER //
    //
    public static 
    void main(String[] args) 
    {
        // init game,
        // then loop game.
        init();
        loop();
    }


    // GAME //
    //
    // game init
    private static 
    void init() 
    {
        // optional (start) settings:
        //
        // SET.switch_controls = true;
        // SET.fps = 120;

        INP.win(WIN);
        INP.engine(ENGINE);
        REND.engine(ENGINE);
        ENGINE.player(PLAYER);
        ENGINE.settings(SET);
        UI.settings(SET);
        WIN.settings(SET);
        WIN.rend(REND);
        WIN.size(SET.screen_width, SET.screen_height);
        WIN.build();
        WIN.engine(ENGINE);
        INP.settings(SET);

        ENGINE.screen_width = WIN.width();
        ENGINE.screen_height = WIN.height();
        ENGINE.start();

        WIN.title(SET.GAME_TITLE);
        WIN.ui(UI);
    }
    //
    // game loop
    private static 
    void loop() 
    {
        // vars to keep track of looping time
        long fps_start = 0, fps_prev, fps_steps = 0;
        
        // core game loop
        while (true) 
        {
            // update engine
            ENGINE.update();
            ENGINE.screen_width = WIN.width();
            ENGINE.screen_height = WIN.height();

            // window refresh;
            // loop-time calculation
            fps_prev = fps_start;
            fps_start = System.currentTimeMillis();
            fps_steps += (fps_start-fps_prev);
            
            if (SET.fps > 0) 
            {
                if (fps_steps >= (int)(1000.0/SET.fps)) 
                {
                    WIN.ref();
                    // System.out.println("winref @"+fps_steps);
                    fps_steps = 0;
                }
            } else {WIN.ref();}


            // let system brake to
            // prevent resource eating.  
            brake();
        }
    }


    // SLEEP //
    //
    private static 
    void brake() 
    {
        try 
        {
            Thread.sleep(1);
            // System.out.println("sleeping");
        } catch (InterruptedException ex) 
        {
            ex.printStackTrace();
        }
    }


} // END CLASS //