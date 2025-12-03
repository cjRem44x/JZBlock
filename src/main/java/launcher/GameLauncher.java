package main.java.launcher;

import main.java.util.*;

public class GameLauncher 
{
    public void run_GameLauncher() 
    {
        System.out.println("Game Launcher is running...");
        Logging.o.new_log_out("GameLauncher", "INFO", "Local System is Running OS => "+LocalSys.o.os_tag());
    }
}
