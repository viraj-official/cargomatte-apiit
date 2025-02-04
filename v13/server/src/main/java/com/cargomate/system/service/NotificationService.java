package com.cargomate.system.service;


import com.cargomate.system.util.ValidationService;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class NotificationService {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.phone.number}")
    private String twilioPhoneNumber;

    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;

    @Autowired
    private ValidationService validationService;

    public void sendEmail(String to, String subject, String body) {
        if (!validationService.isValidEmail(to)) {
            log.error("Invalid email address: {}", to);
            return;
        }

        try {
            Email from = new Email("virajsameerawork@gmail.com");
            Email toEmail = new Email(to);
            Content content = new Content("text/html", body);
            Mail mail = new Mail(from, subject, toEmail, content);

            SendGrid sendGrid = new SendGrid(sendGridApiKey);
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sendGrid.api(request);
            log.info("Email sent to {}: Status Code - {}", to, response.getStatusCode());
        } catch (IOException e) {
            log.error("Error sending email to {}: {}", to, e.getMessage(), e);
        }
    }

    public void sendSMS(String to, String messageContent) {
        if (!validationService.isValidPhoneNumber(to)) {
            log.error("Invalid phone number: {}", to);
            return;
        }

        try {
            Twilio.init(accountSid, authToken);
            Message message = Message.creator(
                            new com.twilio.type.PhoneNumber(to),
                            new com.twilio.type.PhoneNumber(twilioPhoneNumber),
                            messageContent)
                    .create();

            log.info("SMS sent to {}: SID - {}", to, message.getSid());
        } catch (Exception e) {
            log.error("Error sending SMS to {}: {}", to, e.getMessage(), e);
        }
    }


}
