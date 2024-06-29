package com.utkarsh.stack.book.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine springTemplateEngine;
    @Async
    public void sendEmail(
            String to,
            String username,
            EmailTemplateName emailTemplateName,
            String confirmationUrl,
            String activationCode,
            String subject
    ) throws MessagingException {
        //For constructing content of email
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                mimeMessage,
                MimeMessageHelper.MULTIPART_MODE_MIXED,
                StandardCharsets.UTF_8.name()
        );
        helper.setFrom("utkarsh.sri.srivastav@gmail.com");
        helper.setSubject(subject);
        helper.setTo(to);

        //For storing replacable values from static template
        Map<String, Object> properties = new HashMap<>();
        properties.put("username", username);
        properties.put("confirmationUrl", confirmationUrl);
        properties.put("activation_code",activationCode);
        Context context = new Context();
        context.setVariables(properties);

        //Constructing final template by fetching "templateName.html" data and replacing with context data
        String templateName;
        if(emailTemplateName == null)
            templateName = "confirm-email";
        else
            templateName = emailTemplateName.name();
        String template = springTemplateEngine.process(templateName, context);

        // Populating text of our mimemessage
        helper.setText(template, true);

        //Finally sending mimemessage
        log.info("Sending mail to: {}", to);
        javaMailSender.send(mimeMessage);
    }
}
