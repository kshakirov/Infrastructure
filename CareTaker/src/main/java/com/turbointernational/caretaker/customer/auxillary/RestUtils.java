package com.turbointernational.caretaker.customer.auxillary;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Created by kshakirov on 2/14/17.
 */
public class RestUtils {

    public static String getTurboHost(String turboHost, String turboHostPort){
        if (!turboHostPort.isEmpty()){
            turboHost = turboHost.concat(":" + turboHostPort);
        };
        return turboHost;

    }

    public static String getBearer(String bearer, String token){
        bearer = "Bearer ";
        return  bearer.concat(token);
    }

    public static String preparePassToEmail(String mail_address, String url, String bearer) throws UnirestException, ParseException {
        HttpResponse<JsonNode> customerResponse = null;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("email", mail_address);
        String path  = "http://" + url;
        customerResponse = Unirest.put(path)
                .header("Authorization", bearer)
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .body(jsonObject.toJSONString()).asJson();
        JSONParser parser = new JSONParser();
        JSONObject response = (JSONObject) parser.parse(customerResponse.getBody().toString());
        return getPasswordOrFail(response);
    }

    private static boolean hasPassword(JSONObject response) {
        if ((Boolean) response.get("result"))
            return true;
        return false;
    }

    private static String getPasswordOrFail(JSONObject response) {
        if (hasPassword(response))
            return (String) response.get("password");
        return null;
    }

    public static String getMessageLogPath(){
        return "/admin/message/";
    }

}
