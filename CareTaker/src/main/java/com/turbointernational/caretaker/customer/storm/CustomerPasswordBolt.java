package com.turbointernational.caretaker.customer.storm;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
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
    private static final Logger LOG = LoggerFactory.getLogger(CustomerPasswordBolt.class);
    private static final String url = "/admin/customer/password/reset/";
    private static  String turboHost = System.getProperty("turboHost");
    private static final String turboHostPort = System.getProperty("turboHostPort");
    private static  String bearer = "Bearer ";

    @Override
    public void prepare(Map conf, TopologyContext context) {
        if (!turboHostPort.isEmpty()){
             turboHost = turboHost.concat(":" + turboHostPort);
        }
        bearer = bearer.concat(System.getProperty("token"));
    }

    @Override
    public void execute(Tuple tuple, BasicOutputCollector collector) {
        String mail_address = tuple.getString(0);
        String password = null;
        try {
            password = preparePassToEmail(mail_address);
            if (password != null) {
                LOG.info("Emitting password " + password + " for email " + mail_address);
                collector.emit("forgottenPassword", new Values(mail_address, password));
            } else {
                LOG.info("There is no User with Email " + mail_address);
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
        jsonObject.put("email", mail_address);
        String path  = "http://" + turboHost + url;
        customerResponse = Unirest.put(path)
                .header("Authorization", bearer)
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

        declarer.declareStream("forgottenPassword", new Fields("mail", "password"));
    }
}
