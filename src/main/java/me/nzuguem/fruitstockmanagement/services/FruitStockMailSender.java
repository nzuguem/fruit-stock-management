package me.nzuguem.fruitstockmanagement.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.Charset;

@Component
public class FruitStockMailSender {


    private final ResourceLoader resourceLoader;

    private final JavaMailSender mailSender;

    public FruitStockMailSender(ResourceLoader resourceLoader, JavaMailSender javaMailSender) {
        this.resourceLoader = resourceLoader;
        this.mailSender = javaMailSender;
    }

    public void sendEmail(String subject, String template) {

        try {
            var message = mailSender.createMimeMessage();

            message.setFrom(new InternetAddress("fruit.stock@nzuguem.me"));
            message.setRecipients(MimeMessage.RecipientType.TO, "inventory@nzuguem.me");
            message.setSubject(subject);

            var htmlTemplate = this.resourceLoader.getResource(STR."classpath:email/\{template}.html");

            message.setContent(htmlTemplate.getContentAsString(Charset.defaultCharset()), "text/html; charset=utf-8");

            this.mailSender.send(message);

        } catch (MessagingException | IOException e) {
            throw new RuntimeException(e);
        }

    }
}
