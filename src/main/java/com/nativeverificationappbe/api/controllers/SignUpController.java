package com.nativeverificationappbe.api.controllers;

import com.nativeverificationappbe.api.models.NINandSelfieDTO;
import com.nativeverificationappbe.api.models.UserCredentialsDTO;
import com.nativeverificationappbe.api.services.SignUpServicesImp;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
//import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import org.springframework.http.HttpStatus;
import com.nativeverificationappbe.api.services.SignUpServices;

@RestController
@RequestMapping("/nativeverificationappbe/api")
@RequiredArgsConstructor
public class SignUpController {

    private final SignUpServicesImp signUpServices;

    private static final Logger logger = LoggerFactory.getLogger(SignUpController.class);

    @PostMapping(value = "/signup", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Map<String, Object>> signup(@RequestBody NINandSelfieDTO ninAndSelfieDTO) {

        logger.info(String.valueOf(ninAndSelfieDTO));

        Boolean selfieIsReal = signUpServices.livelinessCheck(ninAndSelfieDTO);

        if (!selfieIsReal) {
            logger.error("Liveliness check returned false.");
            //create global exception class
            //throw new VerificationFailedException("Selfie submitted failed realness check.");

        }

        //if liveliness check is successful it goes to the nin with face endpoint check
        UserCredentialsDTO ninMatch = signUpServices.ninAndFaceCheck(ninAndSelfieDTO);

        if (ninMatch == null) {

            logger.error("NIN and Selfie check returned null.");
            //throw new VerificationFailedException("NIN and Selfie submitted has failed to find a match.");

        } else {

            //User confirms details at FE
            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                    "status", true,
                            "ninResponse",ninMatch,
                            "message", "NIN and Selfie verified successfully.",
                            "sessionId", "12345" //session.id
                        )
                    );

        }

        // tests
        return null;

    }

    @PostMapping(value = "/createaccount", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Map<String, String>> createaccount(@RequestBody UserCredentialsDTO userCredentialsDTO) {


        Boolean accountCreated = signUpServices.createAccount(userCredentialsDTO, request)
    }

}
