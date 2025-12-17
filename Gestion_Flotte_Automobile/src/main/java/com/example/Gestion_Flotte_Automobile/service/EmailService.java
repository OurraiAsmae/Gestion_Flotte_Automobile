package com.example.Gestion_Flotte_Automobile.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendEmail(String to, String subject, String body) {
        // Convert newlines to HTML breaks for legacy text compatibility
        String htmlBody = body.replace("\n", "<br>");
        sendHtmlEmail(to, subject, htmlBody, null, null);
    }

    public void sendHtmlEmail(String to, String subject, String bodyContent, String actionLink, String actionText) {
        // Log to console used to be here, keeping it for info but integrating HTML
        // logic
        System.out.println("----- EMAIL SENDING -----");
        System.out.println("To: " + to);
        System.out.println("Subject: " + subject);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            Context context = new Context();
            context.setVariable("subject", subject);
            context.setVariable("bodyContent", bodyContent); // Pass raw string or formatted HTML if body is HTML
            context.setVariable("actionLink", actionLink);
            context.setVariable("actionText", actionText);

            String html = templateEngine.process("email/default-email", context);

            // Console preview of the HTML (optional, helpful for debugging without SMTP)
            // System.out.println("HTML Content Preview:\n" + html);

            helper.setTo(to);
            helper.setFrom(fromEmail);
            helper.setSubject(subject);
            helper.setText(html, true);

            mailSender.send(message);
            System.out.println("Email sent successfully.");
        } catch (MessagingException e) {
            System.err.println("Email sending failed: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error during email sending: " + e.getMessage());
        }
    }
}
