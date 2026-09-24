package com.nativeverificationappbe.api.services;

import com.nativeverificationappbe.api.models.NINandSelfieDTO;
import com.nativeverificationappbe.api.models.OTPrequest;
import com.nativeverificationappbe.api.models.UserCredentialsDTO;
import com.nativeverificationappbe.api.models.UserCredentialsEntity;
import jakarta.servlet.http.HttpSession;

public interface SignUpServices {

    Boolean livelinessCheck(NINandSelfieDTO ninAndSelfieDTO);

    UserCredentialsDTO ninAndFaceCheck(NINandSelfieDTO ninAndSelfieDTO);

    Long createAccount(UserCredentialsDTO userCredentialsDTO, HttpSession session);

    Boolean persistOTP(String contact);

    Integer createOTP();

    long incrementAndGetCount(String sessionId);

    UserCredentialsEntity authenticate(String email, String password);

    Boolean checkPhoneOTPmatches(OTPrequest otpRequest, String user_id);
}
