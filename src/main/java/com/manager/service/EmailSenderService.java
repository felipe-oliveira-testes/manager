package com.manager.service;

import com.manager.entity.Item;
import com.manager.entity.Order;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

@Service
@Primary
public class EmailSenderService implements NotificationSender {
    private static final Logger logger = LogManager.getLogger(EmailSenderService.class);

    @Value("${email.from.email}")
    private String emailFrom;

    @Value("${email.from.password}")
    private String emailFromPassword;






    @Value("${email.smtp.auth}")
    private String emailSmtpAuth;
    @Value("${email.smtp.starttls.enable}")
    private String emailSmtpTls;
    @Value("${email.smtp.host}")
    private String emailSmtpHost;
    @Value("${mail.smtp.port}")
    private String emailSmtpPort;


    @Override
    public void sendNotification(Order order) {
        String emailTo = order.getUser().getEmail();
        Item item = order.getItem();
        String subject = String.format("The order %s delivered", order.getId());
        String body = String.format("The order %s requesting item '%s' with quantity %s was delivered",
                order.getId(), item.getName(), order.getQuantity());

        Session session = buildSession(emailFrom, emailFromPassword);
        sendEmail(session, emailFrom, emailTo,subject, body);
    }

    private Session buildSession(String emailFrom, String password) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp-mail.outlook.com");
        props.put("mail.smtp.port", "587");

        //create Authenticator object to pass in Session.getInstance argument
        Authenticator auth = new Authenticator() {
            //override the getPasswordAuthentication method
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailFrom, password);
            }
        };

        return Session.getInstance(props, auth);
    }
    private void sendEmail(Session session, String emailFrom, String toEmail, String subject, String body){
        try {
            MimeMessage msg = new MimeMessage(session);
            //set message headers
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");

            msg.setFrom(new InternetAddress(emailFrom));

            msg.setSubject(subject, "UTF-8");

            msg.setText(body, "UTF-8");

            msg.setSentDate(new Date());

            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
            Transport.send(msg);
            logger.info(String.format("Email sent to '%s' with subject '%s'", toEmail, subject));
        } catch (Exception e) {
            logger.error("error getUsers");
        }
    }
}
