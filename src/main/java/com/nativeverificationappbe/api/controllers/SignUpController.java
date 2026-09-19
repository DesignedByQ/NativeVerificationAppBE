package com.nativeverificationappbe.api.controllers;

import com.nativeverificationappbe.api.models.NINandSelfieDTO;
import com.nativeverificationappbe.api.models.UserCredentialsDTO;
import com.nativeverificationappbe.api.services.SignUpServicesImp;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
//import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/nativeverificationappbe/api")
@RequiredArgsConstructor
public class SignUpController {

    private final SignUpServicesImp signUpServices;

    private static final Logger logger = LoggerFactory.getLogger(SignUpController.class);

    @PostMapping(value = "/signup", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Map<String, Object>> signup(@RequestBody NINandSelfieDTO ninAndSelfieDTO, HttpServletRequest request) {

        // 1. Create HTTP session
        HttpSession session = request.getSession(true);
        logger.info(session.getId());

        // 2. Dynamically set session attributes from DB record Update MDC dynamically
        String id = signUpServices.randomFiveDigit();

        session.setAttribute("ROLE", "ROLE_NEW_USER");
        session.setAttribute("USER_ID", "user_"+id);
        MDC.put("userId", "user_"+id);
        MDC.put("sessionId", session.getId());

        // Simple log statement - MDC automatically injects Req ID, Session ID, and User ID!
        logger.info("User signup process initiated.");

        var count = signUpServices.incrementAndGetCount(session.getId());

        logger.info("Number of attempts has been made by this user to sign up: {}", count);

        if (count > 3) {
            logger.error("User has tried to many times to verify.");

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                            "status", false,
                            "message", "User has initiated too many verification attempts!",
                            "sessionId", session.getId()
                    )
            );
        }

        logger.info(String.valueOf(ninAndSelfieDTO));

        Boolean selfieIsReal = signUpServices.livelinessCheck(ninAndSelfieDTO);

        if (!selfieIsReal) {
            logger.error("Liveliness check returned false.");
            //create global exception class
            //throw new VerificationFailedException("Selfie submitted failed realness check.");

        }

        //If liveliness check is successful it goes to the nin with face endpoint check
        UserCredentialsDTO ninMatch = signUpServices.ninAndFaceCheck(ninAndSelfieDTO);

        if (ninMatch == null) {

            logger.error("NIN and Selfie check returned null.");
            //throw new VerificationFailedException("NIN and Selfie submitted has failed to find a match.");

        } else {

            //User confirms details at FE
            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                    "status", true,
                            "ninResponse", ninMatch,
                            "message", "NIN and Selfie verified successfully.",
                            "sessionId", session.getId()
                        )
                    );

        }

        // tests
        return null;

    }

    @PostMapping(value = "/createaccount", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Map<String, String>> createaccount(@RequestBody UserCredentialsDTO userCredentialsDTO, HttpServletRequest request) {

        HttpSession session = request.getSession(false);

        Long accountCreated = signUpServices.createAccount(userCredentialsDTO, session);

        if (accountCreated != null) {

            session.setAttribute("USER_ID", accountCreated);
            session.setAttribute("ROLE", "ROLE_USER");

        }
        return null;
    }

}
