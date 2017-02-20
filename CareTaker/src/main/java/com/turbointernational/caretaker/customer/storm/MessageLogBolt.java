package com.turbointernational.caretaker.customer.storm;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.turbointernational.caretaker.customer.auxillary.RestUtils;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by kshakirov on 2/14/17.
 */
public class MessageLogBolt extends BaseBasicBolt {
    private static final Logger LOG = LoggerFactory.getLogger(MessageLogBolt.class);
    private   String url;
    private   String turboHost;
    private   String turboHostPort;
    private String token;
    private   String bearer;

    public MessageLogBolt(String turboHost, String turboHostPort, String token){
        this.turboHost = turboHost;
        this.turboHostPort = turboHostPort;
        this.token = token;
    }

    @Override
    public void prepare(Map conf, TopologyContext context){
        turboHost = RestUtils.getTurboHost(turboHost, turboHostPort);
        bearer = RestUtils.getBearer(bearer, token);
        url = RestUtils.getMessageLogPath();
    }

    @Override
    public void execute(Tuple tuple, BasicOutputCollector collector) {
        String emailAddress = tuple.getString(0);
        String password = tuple.getString(1);
        try {
            commitMessageLog(emailAddress, password);
            LOG.info("Added Message Log");
        } catch (UnirestException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declareStream("forgottenPassword", new Fields("mail", "password"));
    }

    private JSONObject preparePayload(String emailAddress, String password){
        JSONObject payload = new JSONObject();
        payload.put("email", emailAddress);
        payload.put("password", password);
        return  payload;
    }

    private void commitMessageLog(String emailAddress, String password) throws UnirestException, ParseException {
        HttpResponse<JsonNode> customerResponse = null;
        JSONObject payload = preparePayload(emailAddress, password);
        String path  = "http://" + turboHost + url;
        customerResponse = Unirest.post(path)
                .header("Authorization", bearer)
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .body(payload.toJSONString()).asJson();
    }
}
