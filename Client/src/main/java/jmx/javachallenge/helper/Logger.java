package jmx.javachallenge.helper;

import com.google.gson.Gson;

/**
 * Created by megam on 2015. 11. 15..
 */
public class Logger {
    private static boolean enabled = false;
    private static Gson gson = new Gson();

    public static void log(String message) {
        if (Logger.enabled) log(new Message(message));
    }

    public static void log(Object message) {
        if (Logger.enabled) System.out.println("##START##" + gson.toJson(message) + "##END##");
    }

    private static class Message {
        private String message;
        public Message(String s) {
            this.message = s;
        }
    }
}
