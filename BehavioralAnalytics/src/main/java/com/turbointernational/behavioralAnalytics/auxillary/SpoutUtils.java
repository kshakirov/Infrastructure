package com.turbointernational.behavioralAnalytics.auxillary;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Created by kshakirov on 6/23/17.
 */
public class SpoutUtils {
    public static JSONObject readMessage(String message) throws ParseException {
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(message);
        return (JSONObject) obj;
    }

    public static boolean isVisitorLog(JSONObject message){

        return ((String) message.get("type")).equalsIgnoreCase("visitor");
    }
    public static boolean isCustomerLog(JSONObject message){
        return ((String) message.get("type")).equalsIgnoreCase("customer");
    }
}
