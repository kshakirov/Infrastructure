package com.turbointernational.caretaker.customer.storm;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kshakirov on 2/6/17.
 */
public class CustomerPasswordBolt extends BaseBasicBolt {
    Map<String, Integer> counts = new HashMap<String, Integer>();
    private static final Logger LOG = LoggerFactory.getLogger(CustomerPasswordBolt.class);
    private static final String url = "/admin/customer/password/reset/";
    private static final String turboHost = "http://localhost:4700";
    private static final String bearer = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE1MDc0NjA0NzUsImlhdCI6MTQ4NTg2MDQ3NSwiaXNzIjoiem9yYWwuY29tIiwic2NvcGVzIjpbInZpZXdfcHJpY2VzIl0sImN1c3RvbWVyIjp7ImlkIjo0ODcsImdyb3VwIjoiRSIsIm5hbWUiOiJLaXJpbGwgU2hha2lyb3YifX0.eO_Nix-jDxgF_6QezL5MSJcMg9lAFWwy878dZ9Fyr_c";

    @Override
    public void prepare(Map conf, TopologyContext context) {

    }

    @Override
    public void execute(Tuple tuple, BasicOutputCollector collector) {
        String mail_address = tuple.getString(0);
        String password = null;
        try {
            password = preparePassToEmail(mail_address);
            if (password != null) {
                LOG.info("Emitting password " + password + " for email " + mail_address);
                collector.emit(new Values(mail_address, password));
            } else {

            }
        } catch (UnirestException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    private String preparePassToEmail(String mail_address) throws UnirestException, ParseException {
        HttpResponse<JsonNode> customerResponse = null;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("email", "kshakirov@zoral.com.ua");
        customerResponse = Unirest.put(this.turboHost + this.url)
                .header("Authorization", this.bearer)
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .body(jsonObject.toJSONString()).asJson();
        JSONParser parser = new JSONParser();
        JSONObject response = (JSONObject) parser.parse(customerResponse.getBody().toString());
        return getPasswordOrFail(response);
    }

    private boolean hasPassword(JSONObject response) {
        if ((Boolean) response.get("result"))
            return true;
        return false;
    }

    private String getPasswordOrFail(JSONObject response) {
        if (hasPassword(response))
            return (String) response.get("password");
        return null;
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {

        declarer.declare(new Fields("mail", "password"));
    }
}