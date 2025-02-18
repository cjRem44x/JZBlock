// AUTHOR: cjRem44x //
//
public class Main {
    // FIELDS //
    //
    static final Window    WIN     = new Window();
    static final Input     INP     = new Input();
    static final Render    REND    = new Render();
    static final Settings  SET     = new Settings();
    static final Engine    ENGINE  = new Engine();
    static final Player    PLAYER  = new Player();


    // DRIVER //
    //
    public static void main(String[] args) {
        init();
        loop();
    }


    // GAME //
    //
    // game init
    static void init() {
        INP.win(WIN);
        INP.engine(ENGINE);
        REND.engine(ENGINE);
        ENGINE.player(PLAYER);
        WIN.rend(REND);
        WIN.size(800, 600);
        WIN.build();
        WIN.engine(ENGINE);

        ENGINE.screen_width = WIN.width();
        ENGINE.screen_height = WIN.height();
        ENGINE.start();
    }
    //
    // game loop
    static void loop() {
        long fps_start = 0, fps_prev, fps_steps = 0;
        
        while (true) {
            // update engine
            ENGINE.update();
            ENGINE.screen_width = WIN.width();
            ENGINE.screen_height = WIN.height();

            // window refresh
            fps_prev = fps_start;
            fps_start = System.currentTimeMillis();
            fps_steps += (fps_start-fps_prev);
            if(SET.fps > 0) {
                if (fps_steps >= (int)(1000.0/SET.fps)) {
                    WIN.ref();
                    // System.out.println("winref @"+fps_steps);
                    fps_steps = 0;
                }
            } else {WIN.ref();}

            brake();
        }
    }


    // SLEEP //
    //
    static void brake() {
        try {
            Thread.sleep(1);
            // System.out.println("sleeping");
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

}