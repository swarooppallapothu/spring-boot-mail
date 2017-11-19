package com.poc;

import com.poc.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication
public class MapiPocApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(MapiPocApplication.class, args);
    }

    @Bean(name = "workExecutor")
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setMaxPoolSize(10);
//        taskExecutor.setQueueCapacity(200);
        taskExecutor.afterPropertiesSet();
        return taskExecutor;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Application Started");
        String content = "";
        content = "Test message from payqin sendgrid";
        mailService.sendMessage("Test", content, "testmailid@domail.com");
        mailService.sendMimeMessage("From Payqin Sendgrid", content, "text/plain", "testmailid@domail.com", null);
        System.exit(0);
    }

    @Autowired
    private MailService mailService;

}
