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
        email.setFromAddress("lollypop", "lolly.pop@mymail.com");
        email.setReplyToAddress("lollypop", "lolly.pop@othermail.com");
        email.addRecipient("lollypop", "lolly.pop@somemail.com", Message.RecipientType.TO);
        email.addRecipient("C. Cane", "candycane@candyshop.org", Message.RecipientType.TO);
        email.addRecipient("C. Bo", "chocobo@candyshop.org", Message.RecipientType.CC);
        email.setSubject("hey");
        email.setText("We should meet up! ;)");
        email.setTextHTML("&lt;img src=&#39;cid:wink1&#39;&gt;&lt;b&gt;We should meet up!&lt;/b&gt;&lt;img src=&#39;cid:wink2&#39;&gt;");
        //email.addEmbeddedImage("wink1", imageByteArray, "image/png");
        //email.addEmbeddedImage("wink2", imageDatesource);
        //email.addAttachment("invitation", pdfByteArray, "application/pdf");
        //email.addAttachment("dresscode", odfDatasource);

        //email.signWithDomainKey(privateKeyData, "somemail.com", "selector");

        new

                Mailer(
                new ServerConfig("smtp.host.com", 587, "user@host.com", "password"),

                TransportStrategy.SMTP_TLS,
                new

                        ProxyConfig("socksproxy.host.com", 1080, "proxy user", "proxy password")
        ).

                sendMail(email);
    }
}
