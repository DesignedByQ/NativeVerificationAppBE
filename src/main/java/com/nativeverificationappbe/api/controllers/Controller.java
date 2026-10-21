package com.nativeverificationappbe.api.controllers;

import com.nativeverificationappbe.api.models.DocumentDTO;
import com.nativeverificationappbe.api.services.OperationalServicesImp;
import com.nativeverificationappbe.api.services.SignUpServicesImp;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/nativeverificationappbe/api/controller")
@RequiredArgsConstructor
public class Controller {

    private final static Logger logger = LoggerFactory.getLogger(Controller.class);

    private final SignUpServicesImp signUpServices;

    private final OperationalServicesImp operationalServices;

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

    @PostMapping(value = "documents", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Map<String, Boolean>> uploadDocuments(@RequestBody DocumentDTO documentDTO, HttpServletRequest request){

        HttpSession session = request.getSession(false);

        if (session == null) {
            throw new IllegalStateException("Session is null.");
            //return session timedout
        }

        var userId = session.getAttribute("USER_ID").toString();

        if (userId.isEmpty()) {
            throw new IllegalStateException("Session not found account doesn't exist.");
        }

        boolean documentUploaded = operationalServices.uploadDocument(documentDTO, userId);

        return ResponseEntity.ok(
                Map.of("documentUploaded", documentUploaded));
    }
    //upload docs - controller method the front end need a service that verifies the document beofre storing
}
