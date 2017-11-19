package com.poc.service;

import com.poc.config.MailConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.List;

@Service
@EnableAsync
public class MailService {

    @Autowired
    MailConfig mailConfig;

    @Autowired
    Session session;

    private final Log log = LogFactory.getLog(MailService.class);

    /*
     * Send Message Asynchronously
     */
    @Async("workExecutor")
    public void sendMessage(String subject, String body, String... to) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(body);
        System.out.println("new MailConfig()");
        mailConfig.javaMailSender().send(msg);
        System.out.println("send(msg)");
    }

        @Async("workExecutor")
    public void sendMimeMessage(String subject, String content, String contentType, String to, String bcc) {
        MimeMessage mimeMessage = new MimeMessage(session);
        Transport transport = null;
        try {
            transport = session.getTransport("smtp");
            transport.connect();
            InternetAddress fromAddress = new InternetAddress(mailConfig.getFrom());
            mimeMessage.setSender(fromAddress);
            mimeMessage.setReplyTo(InternetAddress.parse(mailConfig.getFrom()));
            mimeMessage.setFrom(fromAddress);
            mimeMessage.setSubject(subject);
            mimeMessage.setContent(content, contentType);
            if (to != null && !to.isEmpty()) {
                mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            }
            if (bcc != null && !bcc.isEmpty()) {
                mimeMessage.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(bcc));
            }
            transport.send(mimeMessage);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
            if (transport != null) {
                try {
                    transport.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    @Async("workExecutor")
    public void sendMimeMessage(String subject, String content, String contentType, List<String> toMailIds) {
        toMailIds.stream().forEach((toMailId) -> {
            this.sendMimeMessage(subject, content, contentType, toMailId, null);
        });
    }

}
