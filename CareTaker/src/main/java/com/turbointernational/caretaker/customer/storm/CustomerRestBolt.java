package com.turbointernational.caretaker.customer.storm;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.turbointernational.caretaker.customer.auxillary.RestUtils;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by kshakirov on 2/6/17.
 */
public class CustomerRestBolt extends BaseBasicBolt {
    private static final Logger LOG = LoggerFactory.getLogger(CustomerRestBolt.class);

    private   String turboHost;
    private   String turboHostPort;
    private String token;
    private   String bearer = "Bearer ";

    public CustomerRestBolt(String turboHost, String turboHostPort, String  token){
        this.turboHost =turboHost;
        this.turboHostPort = turboHostPort;
        this.token = token;
    }

    @Override
    public void prepare(Map conf, TopologyContext context) {
        if (!turboHostPort.isEmpty()){
             turboHost = turboHost.concat(":" + turboHostPort);
        }
        bearer = bearer.concat(token);
    }

    @Override
    public void execute(Tuple tuple, BasicOutputCollector collector) {
        String streamId = tuple.getSourceStreamId();
       if(streamId.equalsIgnoreCase("forgottenPassword")){
           String url = "/admin/customer/password/reset/";
           processUserPassword(tuple, collector, url, "forgottenPassword");
       }else if(streamId.equalsIgnoreCase("newUser")){
           String url = "/admin/customer/new/";
           processUserPassword(tuple, collector, url, "newUser");
       }

    }

    private void processUserPassword(Tuple tuple, BasicOutputCollector collector, String url, String streamId){
        String mail_address = tuple.getString(0);
        String password = null;
        url = turboHost + url;
        try {
            password = RestUtils.preparePassToEmail(mail_address, url, bearer);
            if (password != null) {
                LOG.info("Emitting password " + password + " for email " + mail_address);
                collector.emit(streamId, new Values(mail_address, password));
            } else {
                LOG.info("There is no User with Email " + mail_address);
            }
        } catch (UnirestException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {

        declarer.declareStream("forgottenPassword", new Fields("mail", "password"));
        declarer.declareStream("newUser", new Fields("mail", "password"));
    }
}
