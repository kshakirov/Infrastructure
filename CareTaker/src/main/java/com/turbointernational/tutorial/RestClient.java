package com.turbointernational.tutorial;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.body.RequestBodyEntity;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Created by kshakirov on 2/3/17.
 */
public class RestClient {
    public static void main(String[] args) throws UnirestException, ParseException {

        JSONObject ob = new JSONObject();
        ob.put("email","kshakirov@zoral.com.ua");
        System.out.println(ob.toJSONString());
        HttpResponse<JsonNode> password_data = Unirest.put("http://localhost:4700/admin/customer/password/reset/")

                .header("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE1MDc0NjA0NzUsImlhdCI6MTQ4NTg2MDQ3NSwiaXNzIjoiem9yYWwuY29tIiwic2NvcGVzIjpbInZpZXdfcHJpY2VzIl0sImN1c3RvbWVyIjp7ImlkIjo0ODcsImdyb3VwIjoiRSIsIm5hbWUiOiJLaXJpbGwgU2hha2lyb3YifX0.eO_Nix-jDxgF_6QezL5MSJcMg9lAFWwy878dZ9Fyr_c")
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .body(ob.toJSONString()).asJson();

        JSONParser parser = new JSONParser();
        Object obj = parser.parse(password_data.getBody().toString());
        JSONObject jsonObject = (JSONObject) obj;


        System.out.println(jsonObject);
    }
}
