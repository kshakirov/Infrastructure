package com.turbointernational.caretaker.customer.storm;

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

    @Override
    public void execute(Tuple tuple, BasicOutputCollector collector) {
        String mail_address = tuple.getString(0);
        String  password = null;
        try {
            password = preparePassToEmail(mail_address);
            collector.emit(new Values(mail_address, password));
        } catch (UnirestException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }


    private String preparePassToEmail(String message) throws UnirestException, ParseException {
        HttpResponse<JsonNode> customerResponse = null;
        JSONObject jsonObject;
        String url = "http://store.turbointernational.com/attrsreader/product/{id}/standard_oversize/";
        customerResponse = Unirest.get(url)
                .routeParam("id", "45523")
                .asJson();
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(customerResponse.getBody().toString());
        jsonObject = (JSONObject) obj;
        return "new_password";
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {

        declarer.declare(new Fields("mail", "password"));
    }
}
