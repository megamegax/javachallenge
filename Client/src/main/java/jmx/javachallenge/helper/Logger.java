package jmx.javachallenge.helper;

/**
 * Created by megam on 2015. 11. 15..
 */
public class Logger {
    private static boolean enabled = false;

    public static void log(String message) {
        if (Logger.enabled)
            System.out.println(message);
    }

    public static void log(Object message) {
        if (Logger.enabled)
            System.out.println(message.toString());
    }
}
