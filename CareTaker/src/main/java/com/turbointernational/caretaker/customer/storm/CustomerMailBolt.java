package com.turbointernational.caretaker.customer.storm;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import org.simplejavamail.email.Email;
import org.simplejavamail.mailer.Mailer;
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
    private static final Logger LOG = LoggerFactory.getLogger(CustomerMailBolt.class);

    @Override
    public void prepare(Map conf, TopologyContext context) {
        mailer = new Mailer("smtp.office365.com", 587,
                "kyrylo.shakirov@zorallabs.com",
                "",
                TransportStrategy.SMTP_TLS);
    }

    @Override
    public void execute(Tuple tuple, BasicOutputCollector collector) {
        String word = tuple.getString(0);
        sendMail(word, "");
        LOG.info("Emitting  " + word);
        collector.emit(new Values(word, ""));
    }

    private void sendMail(String userEmail, String emailBody) {
        Email email = new Email();
        email.setFromAddress("Admin", "kyrylo.shakirov@zorallabs.com");
        email.setReplyToAddress("Admin", "kyrylo.shakirov@zorallabs.com");
        email.addRecipient("User", userEmail, Message.RecipientType.TO);
        email.setSubject("hey");
        email.setTextHTML(emailBody);

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {

        declarer.declareStream("audit", new Fields("email", "count"));
    }
}

