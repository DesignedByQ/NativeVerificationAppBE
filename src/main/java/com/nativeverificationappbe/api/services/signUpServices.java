package com.nativeverificationappbe.api.services;

import com.nativeverificationappbe.api.models.NINandSelfieDTO;
import com.nativeverificationappbe.api.models.UserCredentialsDTO;
import jakarta.servlet.http.HttpSession;

public interface SignUpServices {

    Boolean livelinessCheck(NINandSelfieDTO ninAndSelfieDTO);

    UserCredentialsDTO ninAndFaceCheck(NINandSelfieDTO ninAndSelfieDTO);

    Long createAccount(UserCredentialsDTO userCredentialsDTO, HttpSession session);

    Boolean persistOTP(String contact);

    Integer createOTP();

    long incrementAndGetCount(String sessionId);
}
