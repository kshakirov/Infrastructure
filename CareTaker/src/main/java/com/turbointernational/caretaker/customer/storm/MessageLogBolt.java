package com.turbointernational.caretaker.customer.storm;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.turbointernational.caretaker.customer.auxillary.BoltUtils;
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

import java.util.HashMap;
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


    private String getMessageLogPath(){
        return "/admin/message/";
    }

    private String createForgottenPassMessage(Tuple tuple, HashMap data){
        return  "Customer [" +  tuple.getString(0)   +  "] Password [" + data.get("password") + "] Reset and Sent";
    }

    private String creatOrderMessage(HashMap data){
        return  "Order [" + data.get("order_id").toString() + "] sent";
    }

    public MessageLogBolt(String turboHost, String turboHostPort, String token){
        this.turboHost = turboHost;
        this.turboHostPort = turboHostPort;
        this.token = token;
    }

    @Override
    public void prepare(Map conf, TopologyContext context){
        turboHost = RestUtils.getTurboHost(turboHost, turboHostPort);
        bearer = RestUtils.getBearer(bearer, token);
    }

    @Override
    public void execute(Tuple tuple, BasicOutputCollector collector) {
        String streamId = tuple.getSourceStreamId();
        String emailAddress = tuple.getString(0);
        String message = "";
        HashMap data = (HashMap) tuple.getValue(1);
        if(BoltUtils.isOrder(streamId)){
            message = creatOrderMessage(data);
        }else{
            message = createForgottenPassMessage(tuple, data);
        }
        try {
            commitMessageLog(emailAddress, message, data);
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

    private JSONObject preparePayload(String emailAddress, String message, HashMap data){
        JSONObject payload = new JSONObject();
        payload.put("email", emailAddress);
        payload.put("message", message);
        payload.put("status", "Success");
        payload.put("id", (String) data.get("id"));
        return  payload;
    }

    private void commitMessageLog(String emailAddress, String password, HashMap data) throws UnirestException, ParseException {
        HttpResponse<JsonNode> customerResponse = null;
        JSONObject payload = preparePayload(emailAddress, password, data);
        String url = turboHost + getMessageLogPath();
        RestUtils.commitLog(payload, url, bearer);
    }
}
