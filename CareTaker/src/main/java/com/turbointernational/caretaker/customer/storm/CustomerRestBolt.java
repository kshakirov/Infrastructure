package com.turbointernational.caretaker.customer.storm;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.turbointernational.caretaker.customer.auxillary.BoltUtils;
import com.turbointernational.caretaker.customer.auxillary.RestUtils;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kshakirov on 2/6/17.
 */
public class CustomerRestBolt extends BaseBasicBolt {
    private static final Logger LOG = LoggerFactory.getLogger(CustomerRestBolt.class);

    private String turboHost;
    private String turboHostPort;
    private String token;
    private String bearer = "Bearer ";
    private String templateFileUrl = "/admin/template/process";

    private void prepareData(JSONObject templateData, HashMap tuple) {
        tuple.put("template", templateData.get("file"));
        tuple.put("admin_email", templateData.get("admin_email"));
        tuple.put("admin_name", templateData.get("admin_name"));
    }


    private void preparePasswordData(String password, JSONObject templateData, HashMap<String, Object> tuple) {
        tuple.put("password", password);
        prepareData(templateData, tuple);
    }

    public CustomerRestBolt(String turboHost, String turboHostPort, String token) {
        this.turboHost = turboHost;
        this.turboHostPort = turboHostPort;
        this.token = token;
    }

    @Override
    public void prepare(Map conf, TopologyContext context) {
        if (!turboHostPort.isEmpty()) {
            turboHost = turboHost.concat(":" + turboHostPort);
        }
        bearer = bearer.concat(token);
    }

    @Override
    public void execute(Tuple tuple, BasicOutputCollector collector) {
        String streamId = tuple.getSourceStreamId();
        if (BoltUtils.isForgottenPassword(streamId)) {
            String url = "/admin/customer/password/reset/";
            processUserPassword(tuple, collector, url, "forgottenPassword");
        } else if (BoltUtils.isNewUser(streamId)) {
            String url = "/admin/customer/new/";
            processUserPassword(tuple, collector, url, "newUser");
        } else if (BoltUtils.isOrder(streamId)) {
            prepareOrderMessage(tuple, collector, streamId);
        } else if (BoltUtils.isNotification(streamId)) {
            prepareNotificationMessage(tuple, collector, streamId);
        }


    }

    private void processUserPassword(Tuple tuple, BasicOutputCollector collector, String url, String streamId) {
        String mail_address = tuple.getString(0);
        String password = null;
        HashMap data = (HashMap) tuple.getValue(1);
        url = turboHost + url;
        try {
            password = RestUtils.resetPassword(mail_address, url, bearer);
            if (password != null) {
                JSONObject templateData = RestUtils.getTemplateData(mail_address, password, turboHost + templateFileUrl, bearer);
                preparePasswordData(password, templateData, data);
                LOG.info("Emitting password " + password + " for email " + mail_address);
                collector.emit(streamId, new Values(mail_address, data));
            } else {
                LOG.info("There is no User with Email " + mail_address);
            }
        } catch (UnirestException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void prepareOrderMessage(Tuple tuple, BasicOutputCollector collector, String streamId) {
        String email = tuple.getString(0);
        HashMap data = (HashMap) tuple.getValue(1);
        Long orderId = (Long) data.get("order_id");
        String url = turboHost + "/admin/template/process";
        try {
            JSONObject templateData = RestUtils.getOrderTemplateData(orderId, url, bearer);
            prepareData(templateData, data);
            LOG.info("Emitting order " + orderId + " for email " + email);
            collector.emit(streamId, new Values(email, data));
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }

    private void prepareNotificationMessage(Tuple tuple, BasicOutputCollector collector, String streamId) {
        String email = tuple.getString(0);
        HashMap data = (HashMap) tuple.getValue(1);
        String url = turboHost + "/admin/template/process";
        try {
            JSONObject templateData = RestUtils.getNotificationTemplateData(data, url, bearer);
            prepareData(templateData, data);
            LOG.info("Emitting notification for email " + email);
            collector.emit(streamId, new Values(email, data));
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {

        declarer.declareStream("forgottenPassword", new Fields("mail", "data"));
        declarer.declareStream("newUser", new Fields("mail", "data"));
        declarer.declareStream("order", new Fields("email", "data"));
        declarer.declareStream("notification", new Fields("email", "data"));
    }
}
