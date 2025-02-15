// AUTHOR: cjRem44x //
//
public class Main {
    // FIELDS //
    //
    static final Window    WIN  = new Window();
    static final Input     INP  = new Input();
    static final Render    REND = new Render();
    static final Settings  SET = new Settings();

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
        WIN.rend(REND);
        WIN.size(800, 600);
        WIN.build();
    }
    //
    // game loop
    static void loop() {
        long fps_start = 0, fps_prev, fps_steps = 0;
        
        while (true) {

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