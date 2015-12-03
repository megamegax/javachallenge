package hu.jmx.javachallenge.logger;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by megam on 2015. 11. 15..
 */
public class Logger {
    private static LogLevels[] enabled;
    private static Gson gson = new Gson();

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public static void init(LogLevels[] levels){
        enabled = levels;
    }
    public static void log( String message) {
        //FIXME log(LogLevels.DEBUG,new Message(message));
        System.out.println(sdf.format(new Date()) + ": " + message);
    }
    public static void log(LogLevels level, String message) {
        log(level,new Message(message));
    }

    public static void log(Object message) {
        log("##START##" + gson.toJson(message) + "##END##");
    }
    public static void log(LogLevels level, Object message) {
        for(LogLevels logLevel : enabled){
            if(level.equals(logLevel)){
                System.out.println("##START##"
                        +"##STARTLEVEL##"+ level.name() + "##ENDLEVEL##"
                        +"##STARTMESSAGE##"+ gson.toJson(message) + "##ENDMESSAGE##"
                        +"##END##");
            }
        }
    }

    private static class Message {
        private String message;
        public Message(String s) {
            this.message = s;
        }
    }
}
