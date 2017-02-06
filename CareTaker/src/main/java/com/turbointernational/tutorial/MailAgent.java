package com.turbointernational.tutorial;

import org.simplejavamail.email.Email;
import org.simplejavamail.mailer.Mailer;
import org.simplejavamail.mailer.config.ProxyConfig;
import org.simplejavamail.mailer.config.ServerConfig;
import org.simplejavamail.mailer.config.TransportStrategy;

import javax.mail.Message;


/**
 * Created by kshakirov on 2/6/17.
 */
public class MailAgent {
    public static void main(String[] args) {
        Email email = new Email();
        email.setFromAddress("kirill", "kyrylo.shakirov@zorallabs.com");
        email.setReplyToAddress("lollypop", "lolly.pop@othermail.com");
        email.addRecipient("Kirill Shakirov", "kirill.shakirov4@gmail.com", Message.RecipientType.TO);
        email.setSubject("hey");
        email.setText("We should meet up! ;)");
        email.setTextHTML("&lt;img src=&#39;cid:wink1&#39;&gt;&lt;b&gt;We should meet up!&lt;/b&gt;&lt;img src=&#39;cid:wink2&#39;&gt;");

        Mailer mailer = new

                Mailer("smtp.office365.com", 587, "kyrylo.shakirov@zorallabs.com", "",                TransportStrategy.SMTP_TLS);
                mailer.sendMail(email);
                mailer.validate(email);
    }
}
