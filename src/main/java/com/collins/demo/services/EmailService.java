package com.collins.demo.services;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.BodyPart;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


import java.io.File;
import java.util.Map;

import static com.collins.demo.utils.EmailConstants.*;
import static com.collins.demo.utils.EmailUtils.*;

@Service
@RequiredArgsConstructor
public class EmailService {


    @Value("${spring.mail.verify.host}")
    private String host;
    @Value("${spring.mail.username}")
    private String fromEmail;

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Async
    public void sendSimpleMailMessage(String name, String to, String token){
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setSubject(NEW_USER_ACCOUNT_VERIFICATION);
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setText(getEmailMessage(name, host, token));
            mailSender.send(message);
        } catch (Exception e){
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Async
    public void sendMimeMessageWithAttachments(String name, String to, String token){
        try {
            MimeMessage message = getMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8_ENCODING);
            helper.setPriority(1);
            helper.setSubject(NEW_USER_ACCOUNT_VERIFICATION);
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setText(getEmailMessage(name, host, token));
            //add attachment here
            FileSystemResource fort = new FileSystemResource(new File(System.getProperty("user.home") + "/Downloads/images/fort.jpg"));
            //FileSystemResource fort = new FileSystemResource(new File(System.getProperty("user.This PC") + "/Downloads/images/fort.jpg"));
            FileSystemResource dog = new FileSystemResource(new File(System.getProperty("user.home") + "/Downloads/images/dog.jpg"));
            FileSystemResource homework = new FileSystemResource(new File(System.getProperty("user.home") + "/Downloads/images/homework.docx"));
            
            helper.addAttachment(fort.getFilename(), fort);
            helper.addAttachment(dog.getFilename(), dog);
            helper.addAttachment(homework.getFilename(), homework);
            mailSender.send(message);
        } catch (Exception e){
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Async
    public void sendMimeMessageWithEmbeddedImages(String name, String to, String token){
        try {
            MimeMessage message = getMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8_ENCODING);
            helper.setPriority(1);
            helper.setSubject(NEW_USER_ACCOUNT_VERIFICATION);
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setText(getEmailMessage(name, host, token));
            //add attachment here
            FileSystemResource fort = new FileSystemResource(new File(System.getProperty("user.home") + "/Downloads/images/fort.jpg"));
            //FileSystemResource fort = new FileSystemResource(new File(System.getProperty("user.This PC") + "/Downloads/images/fort.jpg"));
            FileSystemResource dog = new FileSystemResource(new File(System.getProperty("user.home") + "/Downloads/images/dog.jpg"));
            FileSystemResource homework = new FileSystemResource(new File(System.getProperty("user.home") + "/Downloads/images/homework.docx"));

//            helper.addInline("<" + fort.getFilename() + ">", fort);
            helper.addInline(getContentId(fort.getFilename()), fort);
            helper.addInline(getContentId(dog.getFilename()), dog);
            helper.addInline(getContentId(homework.getFilename()), homework);
            mailSender.send(message);
        } catch (Exception e){
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Async
    public void sendHtmlEmail(String name, String to, String token){
        try {
            Context context = new Context();
            context.setVariables(Map.of("name", name, "url", getVerificationUrl(host, token)));
            String text = templateEngine.process(EMAIL_TEMPLATE, context);
            MimeMessage message = getMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8_ENCODING);
            helper.setPriority(1);
            helper.setSubject(NEW_USER_ACCOUNT_VERIFICATION);
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setText(text, true);
            mailSender.send(message);
        } catch (Exception e){
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Async
    public void sendHtmlEmailWithEmbeddedFiles(String name, String to, String token) {
        try {
            MimeMessage message = getMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8_ENCODING);
            helper.setPriority(1);
            helper.setSubject(NEW_USER_ACCOUNT_VERIFICATION);
            helper.setFrom(fromEmail);
            helper.setTo(to);
            //helper.setText(text, true);
            Context context = new Context();
            context.setVariables(Map.of("name", name, "url", getVerificationUrl(host, token)));
            String text = templateEngine.process(EMAIL_TEMPLATE, context);

            // Add HTML email body
            MimeMultipart mimeMultipart = new MimeMultipart("related");
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(text, TEXT_HTML_ENCODING);
            mimeMultipart.addBodyPart(messageBodyPart);

            // Add images to the email body
            BodyPart imageBodyPart = new MimeBodyPart();
            DataSource dataSource = new FileDataSource(System.getProperty("user.home") + "/Downloads/images/dog.jpg");
            imageBodyPart.setDataHandler(new DataHandler(dataSource));
            imageBodyPart.setHeader("Content-ID", "image");
            mimeMultipart.addBodyPart(imageBodyPart);

            mailSender.send(message);
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            throw new RuntimeException(exception.getMessage());
        }
    }

    private MimeMessage getMimeMessage() {
        return mailSender.createMimeMessage();
    }

    private String getContentId(String filename) {
        return "<" + filename + ">";
    }
}