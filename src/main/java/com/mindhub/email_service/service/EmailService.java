package com.mindhub.email_service.service;

public interface EmailService {
    public void sendSimpleMessage(String to, String subject, String text,  byte[] pdfBytes, String fileName);
}
