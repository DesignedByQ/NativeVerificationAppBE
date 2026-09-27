package com.nativeverificationappbe.api.controllers;

import com.nativeverificationappbe.api.services.SignUpServicesImp;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/nativeverificationappbe/api/controller")
@RequiredArgsConstructor
public class Controller {

    private final static Logger logger = LoggerFactory.getLogger(Controller.class);

    private final SignUpServicesImp signUpServices;

    @GetMapping(value = "/getuserid", produces = "application/json")
    public ResponseEntity<String> getUserId(HttpServletRequest request){

        HttpSession session = request.getSession(false);

        String userId = session.getAttribute("USER_ID").toString();

        return ResponseEntity.ok(userId);

    }

    @GetMapping(value = "/verify-otps", produces = "application/json")
    public ResponseEntity<Map<String, Boolean>> checkOTPsAreVerified(HttpServletRequest request){

        HttpSession session = request.getSession(false);
        logger.info(session.getId());

        var userId = session.getAttribute("USER_ID").toString();
        logger.info(userId);

        if (userId.isEmpty()) {
            throw new IllegalStateException("Session not found account doesn't exist.");
        }

        return ResponseEntity.ok(signUpServices.checkOTPverification(userId));
    }
}
