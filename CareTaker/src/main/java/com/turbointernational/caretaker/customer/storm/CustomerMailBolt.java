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
    private static  final Logger LOG = LoggerFactory.getLogger(CustomerMailBolt.class);
    private  String admin_email;
    private  String admin_email_password;
    private String admin_smtp;
    private String hostDnsName;

    public CustomerMailBolt(String admin_email, String admin_email_password, String admin_smtp, String hostDnsName){
        this.admin_email = admin_email;
        this.admin_email_password = admin_email_password;
        this.admin_smtp = admin_smtp;
        this.hostDnsName = hostDnsName;
    }

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
        String emailHtmlBody = prepareEmaiHtmlBody(emailAddress, password, hostDnsName);
        Email email = prepareEmail(emailAddress, emailHtmlBody);
        mailer.validate(email);
        mailer.sendMail(email);
        LOG.info("Emitting  " + email);
        collector.emit("forgottenPassword", new Values(emailAddress, password));
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

        declarer.declareStream("forgottenPassword", new Fields("email", "passowrd"));
    }
}

