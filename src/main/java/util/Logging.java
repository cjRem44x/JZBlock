package main.java.util;

public class Logging 
{
    public static Logging o = new Logging();

    public void new_log_out(String comp, Object status, Object msg) 
    {
        System.out.println("\n***\n[LOG]");
        System.out.println("~"+comp+"~ (_"+status+"_): "+msg);
    }
}
