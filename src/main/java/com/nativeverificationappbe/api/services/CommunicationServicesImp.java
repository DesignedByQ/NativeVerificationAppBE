package com.nativeverificationappbe.api.services;

import brevo.ApiClient;
import brevo.ApiException;
import brevo.Configuration;
import brevo.auth.ApiKeyAuth;
import brevoApi.TransactionalEmailsApi;
import brevoApi.TransactionalSmsApi;
import brevoModel.SendSmtpEmail;
import brevoModel.SendSmtpEmailSender;
import brevoModel.SendSmtpEmailTo;
import brevoModel.SendTransacSms;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommunicationServicesImp implements CommunicationServices {

    private static final Logger logger = LoggerFactory.getLogger(CommunicationServicesImp.class);

    // Initialize environment variable
    @Value("${BREVO_API_KEY}")
    private String getBrevoKey;

    @Override
    public String initiateEmailService(String userEmail, Integer otp) {

        // 1. Fetch the key
        String brevoApiKey = Optional.ofNullable(getBrevoKey)
                .orElseThrow(() -> new IllegalStateException("BREVO_API_KEY not found in environment variable."));

        // 2. Setup Client
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        ApiKeyAuth apiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("api-key");
        apiKeyAuth.setApiKey(brevoApiKey);

        TransactionalEmailsApi apiInstance = new TransactionalEmailsApi();

        // 3. Build Email Components
        SendSmtpEmailSender sender = new SendSmtpEmailSender()
                .name("O & O Verification App")
                .email("oando.applications@gmail.com"); // Must be verified in Brevo

        SendSmtpEmailTo to = new SendSmtpEmailTo()
                .email(userEmail);

        SendSmtpEmail emailContent = new SendSmtpEmail()
                .sender(sender)
                .to(List.of(to))
                .subject("Hello from O & O!")
                .htmlContent("<html><body><h1>Your new OTP is: " + otp + "</h1></body></html>");

        // 4. Send Email
        try {
            var result = apiInstance.sendTransacEmail(emailContent);
            logger.info("Email sent! ID: {}", result.getMessageId());
            return "Email sent successfully!";
        } catch (Exception e) {
            logger.info("Error sending email: {}", e.getMessage());
            return "Error sending email";
        }

    }

    @Override
    public String initiatePhoneService(String userPhoneNumber, Integer otp) {

        // 1. Load API Key

        String brevoApiKey = Optional.ofNullable(getBrevoKey)
                .orElseThrow(() -> new IllegalStateException("Brevo phone API Key missing."));


        // 2. Configure Client

        ApiClient defaultClient = Configuration.getDefaultApiClient();
        ApiKeyAuth apiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("api-key");
        apiKeyAuth.setApiKey(brevoApiKey);

        TransactionalSmsApi apiInstance = new TransactionalSmsApi();

        String last10Digits = userPhoneNumber.substring(userPhoneNumber.length() - 10);
        String phoneNumber = "234" + last10Digits;

        // 3. Create SMS Object
        SendTransacSms messageContent = new SendTransacSms()
                .sender("CVTApp")// Max 11 alphanumeric characters
                .recipient(phoneNumber) // Must include country code (e.g., "447123456789")
                .content("Your new OTP is: " + otp)
                .type(SendTransacSms.TypeEnum.TRANSACTIONAL);

        // 4. Send
        try {
            var result = apiInstance.sendTransacSms(messageContent);
            logger.info("SMS sent successfully! ID: {}", result.getMessageId());
            logger.info("SMS sent successfully: {}", result);
            return "SMS sent successfully! ID: " + result.getMessageId();
        } catch (ApiException e) {
            // This will print the exact reason Brevo rejected the request
            logger.error("Brevo API Error Code: {}", e.getCode());
            logger.error("Brevo API Error Response: {}", e.getResponseBody());
        } catch (Exception e) {
            logger.error("Error sending SMS: ", e);
            return "FAILED to send SMS: " + e.getMessage();
        }
        return "Try catch failed.";
    }


}
