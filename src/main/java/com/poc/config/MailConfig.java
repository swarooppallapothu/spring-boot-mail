package com.poc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Properties;
import javax.mail.PasswordAuthentication;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;

/**
 * Handle the configuration of a JavaMailSender bean.
 */
@Configuration
@EnableAsync
@EnableScheduling
public class MailConfig {

    @Value("${mail.protocol:smtp}")
    private String protocol;
    @Value("${mail.host:}")
    private String host;
    @Value("${mail.smtp.ssl.trust:}")
    private String sslTrust;
    @Value("${mail.port:0}")
    private int port;
    @Value("${mail.smtp.auth:false}")
    private boolean auth;
    @Value("${mail.smtp.starttls.enable:false}")
    private boolean starttls;
    @Value("${mail.from:}")
    private String from;
    @Value("${mail.username:}")
    private String username;
    @Value("${mail.password:}")
    private String password;

    private boolean enabled;

    private Properties mailProperties;

    public Properties getMailProperties() {
        return mailProperties;
    }

    public void setMailProperties(Properties mailProperties) {
        this.mailProperties = mailProperties;
    }

    /**
     * Implement a stub MimeMessage for the case where mail notifications are
     * not enabled for a specific deployment.
     */
    public static class NullMimeMessage extends MimeMessage {

        public NullMimeMessage() {
            super((Session) null);
        }
    }


    public String getFrom() {
        return from;
    }

    public boolean getEnabled() {
        return enabled;
    }

    @Bean
    public JavaMailSender javaMailSender() {
        if (host == null || host.isEmpty() || port == 0) {
            // disable mail notifications if no host or port was configured.
            enabled = false;
        }
        enabled = true;
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        Properties mailProps = new Properties();
        mailProps.put("mail.smtp.auth", auth);
        mailProps.put("mail.smtp.starttls.enable", starttls);
        mailProps.put("mail.smtp.host", host);
        mailProps.put("mail.smtp.ssl.trust", sslTrust);
        mailProps.put("mail.smtp.port", port);
        mailProps.put("mail.from", from);
        mailProps.put("mail.password", password);
        mailProps.put("mail.username", username);
        this.setMailProperties(mailProps);
        mailSender.setJavaMailProperties(mailProps);
//        mailSender.setHost(host);
//        mailSender.setPort(port);
//        mailSender.setProtocol(protocol);
        mailSender.setUsername(username);
        mailSender.setPassword(password);
        return mailSender;
    }

    @Bean
    public Session javaMailSession() {
        Properties mailProps = new Properties();
        mailProps.put("mail.smtp.auth", auth);
        mailProps.put("mail.smtp.starttls.enable", starttls);
        mailProps.put("mail.smtp.host", host);
        mailProps.put("mail.smtp.ssl.trust", sslTrust);
        mailProps.put("mail.smtp.port", port);
        mailProps.put("mail.from", from);
        mailProps.put("mail.password", password);
        mailProps.put("mail.username", username);
        this.setMailProperties(mailProps);
        return Session.getInstance(mailProps,
                new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        username, password);// Specify the Username and the PassWord
            }
        });
    }
}
