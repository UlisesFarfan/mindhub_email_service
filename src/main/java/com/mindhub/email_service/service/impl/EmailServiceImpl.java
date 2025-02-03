package com.mindhub.email_service.service.impl;

import com.mindhub.email_service.service.EmailService;
import jakarta.activation.DataSource;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender emailSender;

    public void sendSimpleMessage(String to, String subject, String text,  byte[] pdfBytes, String fileName) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("testgrupo001@gmail.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);
            if(pdfBytes != null || fileName != null) {
                DataSource dataSource = new ByteArrayDataSource(pdfBytes, "application/pdf");
                helper.addAttachment(fileName, dataSource);
            }
            emailSender.send(message);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}