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


    private static JSONObject sendPostRestQuery(JSONObject payload, String url, String bearer) throws UnirestException, ParseException {
        HttpResponse<JsonNode> customerResponse = null;
        String path  = "http://" + url;
        customerResponse = Unirest.post(path)
                .header("Authorization", bearer)
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .body(payload.toJSONString()).asJson();
        JSONParser parser = new JSONParser();
        JSONObject response = (JSONObject) parser.parse(customerResponse.getBody().toString());
        return  response;
    }

   private static JSONObject preparePostQuery(String email, String password, String url, String bearer) {
       JSONObject payload = new JSONObject();
       payload.put("password",password);
       payload.put("email", email);
       payload.put("action", "forgotten_password");
       return payload;
   }



    public static String getTemplate(String email, String password, String url, String bearer) throws ParseException, UnirestException{
        JSONObject query = preparePostQuery(email, password,url,bearer);
        JSONObject response = sendPostRestQuery(query, url, bearer);
        return (String) response.get("file");
    }

    public static String getOrderTemplate(Integer orderId, String url, String bearer) throws ParseException, UnirestException{
        JSONObject payload = new JSONObject();
        payload.put("orderId", orderId);
        JSONObject response = sendPostRestQuery(payload, url, bearer);
        return (String) response.get("file");
    }

    public static String resetPassword(String mail_address, String url, String bearer) throws UnirestException, ParseException {
        JSONObject payload = new JSONObject();
        payload.put("email", mail_address);
        JSONObject response = sendPostRestQuery(payload,url,bearer);
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

    public static void commitLog(JSONObject payload, String url, String bearer) throws  UnirestException, ParseException{
        sendPostRestQuery(payload,url,bearer );
    }

}
