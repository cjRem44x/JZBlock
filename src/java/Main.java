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
        INP.win(WIN);
        WIN.size(800, 600);
        WIN.build();
    }

}