package com.nativeverificationappbe.api.services;

import com.nativeverificationappbe.api.models.NINandSelfieDTO;
import com.nativeverificationappbe.api.models.OTPrequest;
import com.nativeverificationappbe.api.models.UserCredentialsDTO;
import com.nativeverificationappbe.api.models.UserCredentialsEntity;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

import java.util.Map;

public interface SignUpServices {

    Boolean livelinessCheck(NINandSelfieDTO ninAndSelfieDTO);

    UserCredentialsDTO ninAndFaceCheck(NINandSelfieDTO ninAndSelfieDTO);

    Long createAccount(UserCredentialsDTO userCredentialsDTO, HttpSession session);

    Boolean persistOTP(String contact);

    Integer createOTP();

    Boolean persistOTP(String contact, Long userId, Integer newOTP);

    long incrementAndGetCount(String sessionId);

    UserCredentialsEntity authenticate(String email, String password);

    Boolean checkPhoneOTPmatches(String otpRequest, String user_id);

    Boolean checkEmailOTPmatches(String otPrequest, String user_id);

    Boolean checkPasswordOTPmatches(String otp_request, String user_id);

    Map<String, Boolean> checkOTPverification(String user_id);

    Boolean updateEmailNotification(String newEmail, String user_id);

    Boolean updateEmail(String user_id);

    @Transactional
    Boolean updatePhoneNotification(String newNumber, String user_id);

    @Transactional
    Boolean updatePhone(String user_id);

    @Transactional
    Boolean updatePasswordNotification(String newNumber, String user_id);

    @Transactional
    Boolean updatePassword(String user_id);

    @Transactional
    Boolean persistPasswordOTP(Long userId, Integer newOTP);
}
