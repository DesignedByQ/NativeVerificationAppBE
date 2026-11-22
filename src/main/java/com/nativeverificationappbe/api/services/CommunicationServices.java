package com.nativeverificationappbe.api.services;

public interface CommunicationServices {

    String initiateEmailService(String userEmail, Integer otp);

    String initiatePhoneService(String userPhoneNumber, Integer otp);

}
