package main.java;

public class LocalSys 
{
    public static LocalSys o = new LocalSys();

    public String curr_dir()
    {
        return System.getProperty("user.dir");
    }

    public String os_tag()
    {
        String name = System.getProperty("os.name").toLowerCase();
        if (name.contains("win"))
            return ".windows";
        else if (name.contains("nux"))
            return ".linux";
        
            return "";
    }
}
