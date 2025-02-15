// AUTHOR: cjRem44x //
//
public class Main {
    // FIELDS //
    //
    static final Window     WIN = new Window();
    static final Input      INP = new Input();

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
        WIN.size(800, 600);
        WIN.build();
    }
    //
    // game loop
    static void loop() {
        while (true) {

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