package com.makersacademy.acebook.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    public void sendSignUpConfirmation(String to, String username) {
        String subject = "Sign Up Confirmation";
        String text = "Hey " + username + ",\n\nYour signup is confirmed. Welcome to Acebook!";
        sendSimpleMessage(to, subject, text);
    }
}
