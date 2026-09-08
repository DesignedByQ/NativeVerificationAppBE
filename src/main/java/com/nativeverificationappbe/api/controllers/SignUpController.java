package com.nativeverificationappbe.api.controllers;

import com.nativeverificationappbe.api.models.NINandSelfieDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import org.springframework.http.HttpStatus;
import com.nativeverificationappbe.api.services.SignUpServices;

@RestController
@RequestMapping("/nativeverificationappbe/api")
public class SignUpController {

    @Autowired
    private SignUpServices signUpServices;

    private static final Logger logger = LoggerFactory.getLogger(SignUpController.class);

    @PostMapping(value = "/signup", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Map<String, Object>> signup(@RequestBody NINandSelfieDTO ninAndSelfieDTO) {

        Boolean selfieIsReal = signUpServices.livelinessCheck(ninAndSelfieDTO);

        if (!selfieIsReal) {
            logger.error("Liveliness check returned false.");
            //throw new VerificationFailedException("Selfie submitted failed realness check.");
        }



        return ResponseEntity.status(HttpStatus.OK).body(Map.of("IsReal", selfieIsReal));
    }

}
