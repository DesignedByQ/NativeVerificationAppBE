package com.nativeverificationappbe.api.services;

import com.nativeverificationappbe.api.models.NINandSelfieDTO;
import com.nativeverificationappbe.api.models.UserCredentialsDTO;

public interface SignUpServices {

    Boolean livelinessCheck(NINandSelfieDTO ninAndSelfieDTO);

    UserCredentialsDTO ninAndFaceCheck(NINandSelfieDTO ninAndSelfieDTO);

    Boolean createAccount(UserCredentialsDTO userCredentialsDTO);

    Boolean persistOTP(String contact);

    Integer createOTP();

    String initiateEmailService(String userEmail, Integer otp);

}
