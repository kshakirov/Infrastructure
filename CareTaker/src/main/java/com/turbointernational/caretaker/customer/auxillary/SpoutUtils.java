package com.turbointernational.caretaker.customer.auxillary;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Created by kshakirov on 2/20/17.
 */
public class SpoutUtils {
    public static JSONObject readMessage(String message) throws ParseException {
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(message);
        return (JSONObject) obj;
    }
    public static  boolean isForgottenPassword (JSONObject message){

        return ((String) message.get("action")).equalsIgnoreCase("reset");
    }
    public static boolean isNewUser(JSONObject message){
        return ((String) message.get("action")).equalsIgnoreCase("new");
    }

    public static boolean isOrder(JSONObject message){
        return ((String) message.get("action")).equalsIgnoreCase("order");
    }

    public static String getEmailAddress (JSONObject message){
        return (String) message.get("email");
    }




}
