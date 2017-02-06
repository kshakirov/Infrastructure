package com.turbointernational.tutorial;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kshakirov on 2/3/17.
 */
public class RestClient {
    public static void main (String [] args) throws UnirestException, ParseException {

        HttpResponse<JsonNode> bookResponse = Unirest.get("http://store.turbointernational.com/attrsreader/product/{id}/standard_oversize/")
                .routeParam("id", "45523")
                .asJson();
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(bookResponse.getBody().toString());
        JSONObject jsonObject = (JSONObject) obj;



        System.out.println(jsonObject);
    }
}
