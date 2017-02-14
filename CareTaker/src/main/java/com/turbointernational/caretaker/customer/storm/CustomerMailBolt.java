package com.turbointernational.caretaker.customer.storm;


import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;
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
    private static final String admin_email = System.getProperty("admin_email");
    private static final String admin_email_password = System.getProperty("admin_email_password");
    private static final String admin_smtp= System.getProperty("admin_smtp");

    @Override
    public void prepare(Map conf, TopologyContext context) {
        mailer = new Mailer(admin_smtp, 587,
                admin_email,
                admin_email_password,
                TransportStrategy.SMTP_TLS);
    }

    @Override
    public void execute(Tuple tuple, BasicOutputCollector collector) {
        String emailAddress = tuple.getString(0);
        String password = tuple.getString(1);
        String emailHtmlBody = prepareEmaiHtmlBody(emailAddress, password,"localhost");
        Email email = prepareEmail(emailAddress, emailHtmlBody);
        mailer.validate(email);
        mailer.sendMail(email);
        LOG.info("Emitting  " + email);
        collector.emit(new Values(email, "Sent Email"));
    }

    private Email prepareEmail(String emailAddress, String emailHtmlBody) {
        Email email = new Email();
        email.setFromAddress("Admin", admin_email);
        email.setReplyToAddress("Admin", admin_email);
        email.addRecipient("User", emailAddress, Message.RecipientType.TO);
        email.setSubject("TurboInternational");
        email.setTextHTML(emailHtmlBody);
        return email;
    }

    private String prepareEmaiHtmlBody(String emailAddress, String password, String server){
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/forgotten_password.twig");
        JtwigModel model = JtwigModel.newModel()
                .with("email", emailAddress)
                .with("password", password)
                .with("server", server);

        return  template.render(model);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {

        declarer.declareStream("audit", new Fields("email", "message"));
    }
}

