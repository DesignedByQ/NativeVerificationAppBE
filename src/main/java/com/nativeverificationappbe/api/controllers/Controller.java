package com.nativeverificationappbe.api.controllers;

import com.nativeverificationappbe.api.models.DocumentDTO;
import com.nativeverificationappbe.api.models.UpdateUserDTO;
import com.nativeverificationappbe.api.models.UserCredentialsEntity;
import com.nativeverificationappbe.api.models.VerificationRequestEntity;
import com.nativeverificationappbe.api.repos.VerificationRepo;
import com.nativeverificationappbe.api.services.CommunicationServicesImp;
import com.nativeverificationappbe.api.services.OperationalServicesImp;
import com.nativeverificationappbe.api.services.SignUpServicesImp;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
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

    private final VerificationRepo verificationRepo;

    @GetMapping(value = "/getuserid", produces = "application/json")
    public ResponseEntity<String> getUserId(HttpServletRequest request){

        HttpSession session = request.getSession(false);

        String user_id = session.getAttribute("USER_ID").toString();

        return ResponseEntity.ok(user_id);

    }

    @GetMapping(value = "/getconfidencescore", produces = "application/json")
    public ResponseEntity<Integer> getConfidenceScore(HttpServletRequest request){

        HttpSession session = request.getSession(false);

        String user_id = session.getAttribute("USER_ID").toString();

        Long userId = Long.parseLong(user_id);

        VerificationRequestEntity user = verificationRepo.findUserByAccountId(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return ResponseEntity.status(HttpStatus.OK).body(user.getConfidenceScore());

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

    @PostMapping(value = "/documents", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Map<String, Boolean>> uploadDocuments(@RequestBody DocumentDTO documentDTO, HttpServletRequest request){

        HttpSession session = request.getSession(false);

        if (session == null) {
            throw new IllegalStateException("Session is null.");
            //return session timed out
        }

        var userId = session.getAttribute("USER_ID").toString();

        if (userId.isEmpty()) {
            throw new IllegalStateException("Session not found account doesn't exist.");
        }

        boolean documentUploaded = operationalServices.uploadDocument(documentDTO, userId);

        return ResponseEntity.ok(
                Map.of("documentUploaded", documentUploaded));
    }
    //upload docs - controller method the front end need a service that verifies the document before storing

    @GetMapping(value = "/userdata", produces = "application/json")
    public ResponseEntity<Map<String, String>> getUserData(HttpServletRequest request){
        HttpSession session = request.getSession(false);

        String userId = session.getAttribute("USER_ID").toString();

        UserCredentialsEntity user = operationalServices.getUserCredentials(userId);

        //logger.info(String.valueOf(user));

        return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                "title", user.getTitle(),
                "firstName", user.getFirstname(),
                "middleName", user.getMiddlename(),
                "lastName", user.getSurname(),
                "birthdate", user.getBirthdate(),
                "email", user.getEmail(),
                "phone", user.getPhone(),
                "gender", user.getGender(),
                "nin", user.getNin(),
                "livelinessPhoto", user.getLivelinessPhoto()
        ));
    }


    @PostMapping(value = "/update-email", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Boolean> updateEmail(@RequestBody UpdateUserDTO newEmail, HttpServletRequest request){

        HttpSession session = request.getSession(false);

        String userId = session.getAttribute("USER_ID").toString();

        logger.info(session.getId());
        logger.info(userId);

        return ResponseEntity.status(HttpStatus.OK).body(signUpServices.updateEmailNotification(newEmail.getEmail(), userId));

    }

    @PostMapping(value = "/update-phone", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Boolean> updatePhone(@RequestBody UpdateUserDTO newNumber, HttpServletRequest request){

        HttpSession session = request.getSession(false);

        String userId = session.getAttribute("USER_ID").toString();

        logger.info(session.getId());
        logger.info(userId);

        return ResponseEntity.status(HttpStatus.OK).body(signUpServices.updatePhoneNotification(newNumber.getPhone(), userId));

    }

    @PostMapping(value = "/update-password", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Boolean> updatePassword(@RequestBody UpdateUserDTO newPassword, HttpServletRequest request){

        HttpSession session = request.getSession(false);

        String userId = session.getAttribute("USER_ID").toString();

        logger.info(session.getId());
        logger.info(userId);

        return ResponseEntity.status(HttpStatus.OK).body(signUpServices.updatePasswordNotification(newPassword.getPassword(), userId));

    }
}
