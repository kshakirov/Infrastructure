package com.turbointernational.caretaker.customer.storm;


import com.turbointernational.caretaker.customer.auxillary.BoltUtils;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.simplejavamail.email.Email;
import org.simplejavamail.mailer.Mailer;
import org.simplejavamail.mailer.config.ServerConfig;
import org.simplejavamail.mailer.config.TransportStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Message;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kshakirov on 2/6/17.
 */
public class CustomerMailBolt extends BaseBasicBolt {
    Map<String, Integer> counts = new HashMap<String, Integer>();
    private Mailer mailer;
    private static  final Logger LOG = LoggerFactory.getLogger(CustomerMailBolt.class);
    private  String admin_email;
    private  String admin_email_password;
    private String admin_smtp;
    private Integer admin_smtp_port;

    public CustomerMailBolt(String admin_email, String admin_email_password, String admin_smtp, String admin_smtp_port){
        this.admin_email = admin_email;
        this.admin_email_password = admin_email_password;
        this.admin_smtp = admin_smtp;
        this.admin_smtp_port = Integer.valueOf(admin_smtp_port);
    }


    private Mailer buildMailer(){
        if (this.admin_email_password == null){
            return new Mailer(new ServerConfig(this.admin_smtp, this.admin_smtp_port) );
        }else{
            return  new Mailer(admin_smtp, admin_smtp_port, admin_email, admin_email_password,
                    TransportStrategy.SMTP_TLS);
        }
    }

    @Override
    public void prepare(Map conf, TopologyContext context) {
        mailer = buildMailer();
    }

    private void sendOrderMailAndGo(Tuple tuple, BasicOutputCollector collector){
        HashMap tpl = (HashMap<String,Object>) tuple.getValue(1);
        String emailAddress = tuple.getString(0);
        String emailHtmlBody = (String) tpl.get("template");
        Email email = prepareEmail(emailAddress, emailHtmlBody, (String) tpl.get("admin_email"),
                (String) tpl.get("admin_name"));
        mailer.validate(email);
        mailer.sendMail(email);
        collector.emit("order", new Values(emailAddress, tpl));
    }

    private void sendPasswordMailAndGo(Tuple tuple, BasicOutputCollector collector){
        String emailAddress = tuple.getString(0);
        HashMap tpl = (HashMap<String,String>) tuple.getValue(1);
        String emailHtmlBody = (String) tpl.get("template");
        Email email = prepareEmail(emailAddress, emailHtmlBody, (String) tpl.get("admin_email"),
                (String) tpl.get("admin_name"));
        mailer.validate(email);
        mailer.sendMail(email);
        collector.emit("forgottenPassword", new Values(emailAddress, tpl));
    }


    private void sendNotificationAndGo(Tuple tuple, BasicOutputCollector collector){
        String emailAddress = tuple.getString(0);
        HashMap tpl = (HashMap<String,String>) tuple.getValue(1);
        String emailHtmlBody = (String) tpl.get("template");
        Email email = prepareEmail(emailAddress, emailHtmlBody, (String) tpl.get("admin_email"),
                (String) tpl.get("admin_name"));
        mailer.validate(email);
        mailer.sendMail(email);
        collector.emit("notification", new Values(emailAddress, tpl));
    }


    @Override
    public void execute(Tuple tuple, BasicOutputCollector collector) {
        String streamId = tuple.getSourceStreamId();
        if(BoltUtils.isOrder(streamId)){
            sendOrderMailAndGo(tuple, collector);
        }else if(BoltUtils.isNotification(streamId)) {
            sendNotificationAndGo(tuple, collector);
        }
        else {
            sendPasswordMailAndGo(tuple, collector);
        }


    }

    private Email prepareEmail(String emailAddress, String emailHtmlBody, String ad_email, String ad_name) {
        Email email = new Email();
        email.setFromAddress(ad_name, ad_email);
        email.setReplyToAddress(ad_name, ad_email);
        email.addRecipient("User", emailAddress, Message.RecipientType.TO);
        email.setSubject("TurboInternational");
        email.setTextHTML(emailHtmlBody);
        return email;
    }


    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {

        declarer.declareStream("forgottenPassword", new Fields("email", "data"));
        declarer.declareStream("order", new Fields("email", "data"));
        declarer.declareStream("notification", new Fields("email", "data"));
    }
}

