package com.nativeverificationappbe.api.services;

import com.nativeverificationappbe.api.models.*;
import com.nativeverificationappbe.api.repos.UserCredRepo;
import com.nativeverificationappbe.api.repos.VerificationRepo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.redis.core.RedisTemplate;

@Service
@RequiredArgsConstructor
public class SignUpServicesImp implements SignUpServices {

    private static final Logger logger = LoggerFactory.getLogger(SignUpServicesImp.class);

    private static final SecureRandom secureRandom = new SecureRandom();

    private final RedisTemplate<String, Object> redisTemplate;

    private final VerificationRepo verificationRepo;

    private final UserCredRepo userCredRepo;

    private final Mapper mapper;

    private final CommunicationServicesImp communicationServicesImp;

    private final OperationalServicesImp operationalServicesImp;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.prembly.com")
            .build();

    // Initialize environment variable
    @Value("${API_KEY}")
    private String getApiKey;

    /**
     * Generates a random 5-digit numeric string (leading zeros allowed).
     *
     * @return a string in the range "00000" to "99999"
     */
    public String randomFiveDigit() {
        int value = secureRandom.nextInt(100_000); // 0..99999
        return String.format("%05d", value);
    }

    @Override
    public Integer createOTP() {
        return secureRandom.nextInt(100_000, 999_999); // 100,000 (inclusive) to 999,000 (exclusive)
    }

    @Override
    public Boolean livelinessCheck(NINandSelfieDTO ninAndSelfieDTO) {

        // Fetch the key
        String apiKey = Optional.ofNullable(getApiKey)
                .orElseThrow(() -> new IllegalStateException("API_KEY not found in environment variable."));

        try {
            PremblyLivelinessResponse response = webClient.post()
                    .uri("/verification/biometrics/face/liveliness_check")
                    .header("X-Api-Key", apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(Map.of("image", ninAndSelfieDTO.getImage()))
                    .retrieve()
                    .bodyToMono(PremblyLivelinessResponse.class)
                    .block();

            if (response == null) {
                logger.error("Prembly returned null response");
                return false;
            }

            logger.info("Prembly response: {}", response);

            if (Boolean.TRUE.equals(response.getStatus())) {
                logger.info("Liveliness check passed!");
                return true;
            } else {
                logger.warn("Liveliness check failed: {}", response);
                return false;
            }

        } catch (Exception e) {
            logger.error("Error during liveliness check: ", e);
            throw new RuntimeException(e);
        }

    }

    @Override
    public UserCredentialsDTO ninAndFaceCheck(NINandSelfieDTO ninAndSelfieDTO) {

        String apiKey = Optional.ofNullable(getApiKey).orElseThrow(() -> new IllegalStateException(("API_KEY not found in environment variable.")));

        try {

            PremblyLivelinessResponse response = webClient.post()
                    .uri("/verification/nin_w_face")
                    .header("X-Api-Key", apiKey)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(Map.of(
                                    "image", ninAndSelfieDTO.getImage(),
                                    "number_nin", String.valueOf(ninAndSelfieDTO.getNin()), // Ensure String format
                                    "date_of_birth", ninAndSelfieDTO.getBirthdate()
                            )
                    )
                    .retrieve()
                    .bodyToMono(PremblyLivelinessResponse.class)
                    .block();

            if (response == null) {
                logger.error("Prembly returned null response.");
                return null;
            }

            logger.info("NIN response received for NIN data: {}", response);

            if (response.getStatus() == true) {

                PremblyLivelinessResponseNinData ninData = response.getData().getNinData();
                PremblyLivelinessResponseFaceData faceData = response.getData().getFaceData();
                PremblyLivelinessResponseVerification verificationData = response.getVerification();

                if (ninData != null) {
                    logger.info("Phone: {}", ninData.getTelephoneno());
                    logger.info("Face Match Confidence: {}", faceData.getConfidence());
                    logger.info("Verification response: {}", verificationData);
                    return mapper.responseToUserCredentialsDTO(ninData);
                } else {
                    logger.warn("Prembly NIN data object is null. Errors: {}", response.getData());
                    return null;
                }
            } else {
                logger.warn("Verification failed or status is false. Errors: {}", response.getDetail());
                return null;
            }

        } catch (Exception e) {
            logger.error("Error during nin and selfie check: ", e);
            return null;
        }

    }

    @Override
    public Long createAccount(UserCredentialsDTO userCredentialsDTO, HttpSession session) {

        try {

            //Add user ID to logs
            // Store user ID in the session attributes (stored on server memory/Redis)

            // val session = request.getSession(false)
            // if (session != null) {

            UserCredentialsEntity user = userCredRepo.save(mapper.userCredentialsDTOtoEntity(userCredentialsDTO));

            VerificationRequestEntity associatedVerificationRequest = new VerificationRequestEntity();
            associatedVerificationRequest.setUserCredentialsEntity(user);
            associatedVerificationRequest.setConfidenceScore(50);

            verificationRepo.save(associatedVerificationRequest);

            session.setAttribute("USER_ID", user.getAccountId().toString());
            session.setAttribute("ROLE", "ROLE_USER");
            MDC.put("userId", user.getAccountId().toString());

            logger.info("Account created successfully.");



//            session.setAttribute("user_id", user.accountId.toString())
//            session.setAttribute("is_verified", true)
//            MDC.put("userId", user.accountId.toString())
//            logger.info("User ID added to Session")

            logger.info("Now persisting phone OTP.");
            persistOTP(user.getPhone());

//        } else {
//            logger.warn("No Session found, login to start again.")
//        }

            return user.getAccountId();

        } catch (Exception e) {

            logger.error("Error creating user account: ", e);
            return null;
        }

    }

    @Override
    public Boolean persistOTP(String contact) {

        Integer newOTP = createOTP();
        logger.info(contact, newOTP);
        if (contact.contains("@")) {

            try {

                Optional<UserCredentialsEntity> optionalUser = Optional.ofNullable(userCredRepo.findUserSignUpByEmail(contact));

                if (optionalUser.isEmpty()) {
                    logger.warn("User not found.");
                    return false;
                }

                UserCredentialsEntity user = optionalUser.get();

                VerificationRequestEntity getUser = verificationRepo.findUserByAccountId(user.getAccountId())
                        .orElseThrow(() -> new EntityNotFoundException("User not found."));

                getUser.setEmailOTP(newOTP);
                getUser.setOtpCreatedAt(LocalDateTime.now());
                verificationRepo.save(getUser);
                logger.info("Saved new email OTP to entity: {}", user.getEmail());

                communicationServicesImp.initiateEmailService(contact, newOTP);

            } catch (Exception e) {
                logger.error("Failed to add email OTP to entity: ", e);
                return false;
            }

        } else {

            try {

                Optional<UserCredentialsEntity> optionalUser = Optional.ofNullable(userCredRepo.findUserCredentialsByPhone(contact));

                if (optionalUser.isEmpty()) {
                    logger.warn("User not found.");
                    return false;
                }

                UserCredentialsEntity user = optionalUser.get();

                VerificationRequestEntity getUser = verificationRepo.findUserByAccountId(user.getAccountId())
                        .orElseThrow(() -> new EntityNotFoundException("User not found."));

                getUser.setPhoneOTP(newOTP);
                getUser.setOtpCreatedAt(LocalDateTime.now());
                verificationRepo.save(getUser);
                logger.info("Saved new phone OTP to entity: {}", user.getEmail());

                communicationServicesImp.initiatePhoneService(contact, newOTP);

            } catch (Exception e) {
                logger.error("Failed to add phone OTP to entity: ", e);
                return false;
            }

        }

        return false;
    }

    @Override
    @Transactional
    public Boolean persistOTP(String newContact, Long userId, Integer newOTP) {

        logger.info(newContact);
        logger.info(String.valueOf(userId));
        logger.info(String.valueOf(newOTP));
        if (newContact.contains("@")) {

            try {

                Optional<UserCredentialsEntity> optionalUser = Optional.of(userCredRepo.getReferenceById(userId));

                UserCredentialsEntity user = optionalUser.get();
                logger.info(String.valueOf(user.getAccountId()));
                VerificationRequestEntity getUser = verificationRepo.findUserByAccountId(user.getAccountId())
                        .orElseThrow(() -> new EntityNotFoundException("User not found."));
                logger.info(getUser.getRequestedCredentialUpdate());
                getUser.setEmailOTP(newOTP);
                getUser.setOtpCreatedAt(LocalDateTime.now());
                getUser.setEmailVerified(false);
                verificationRepo.save(getUser);
                logger.info(getUser.getRequestedCredentialUpdate());
                logger.info("Saved new email OTP to entity: {}", getUser.getRequestedCredentialUpdate());

                communicationServicesImp.initiateEmailService(newContact, newOTP);

                return true;

            } catch (Exception e) {
                logger.error("Failed to add email OTP to entity: ", e);
                return false;
            }

        } else {

            try {

                Optional<UserCredentialsEntity> optionalUser = Optional.of(userCredRepo.getReferenceById(userId));

                UserCredentialsEntity user = optionalUser.get();

                VerificationRequestEntity getUser = verificationRepo.findUserByAccountId(user.getAccountId())
                        .orElseThrow(() -> new EntityNotFoundException("User not found."));

                getUser.setPhoneOTP(newOTP);
                getUser.setOtpCreatedAt(LocalDateTime.now());
                getUser.setPhoneVerified(false);
                verificationRepo.save(getUser);
                logger.info("Saved new phone OTP to entity: {}", user.getEmail());

                communicationServicesImp.initiatePhoneService(newContact, newOTP);

                return true;

            } catch (Exception e) {
                logger.error("Failed to add phone OTP to entity: ", e);
                return false;
            }

        }

    }

    /**
     * Increment the login-attempts counter for the given session and return the new count.
     * If this is the first increment, set an expiry of 30 minutes on the key.
     *
     * @param sessionId session identifier
     * @return current attempt count
     */
    @Override
    public long incrementAndGetCount(String sessionId) {
        String key = "login_attempts:" + sessionId;

        Long count = redisTemplate.opsForValue().increment(key);
        if (count == null) {
            // defensive fallback: ensure a value exists
            count = 1L;
            redisTemplate.opsForValue().set(key, count);
        }

        if (count == 1L) {
            // set idle expiry for the attempts key
            redisTemplate.expire(key, Duration.ofMinutes(30));
        }

        return count;
    }


    @Override
    public UserCredentialsEntity authenticate(String email, String password) {

        UserCredentialsEntity user = userCredRepo.findUserSignUpByEmail(email);

        VerificationRequestEntity verifiedUser = verificationRepo.findUserByAccountId(user.getAccountId())
                .orElseThrow(() -> new EntityNotFoundException("Login credentials not matched."));

        if (!user.getPassword().equals(password)) {
            logger.warn("Password is not recognised.");
            return null;
        }

        operationalServicesImp.updateConfidenceScore(verifiedUser);

        return user;

    }

    @Override
    public Boolean checkPhoneOTPmatches(String otp_request, String user_id) {

        Long userId = Long.parseLong(user_id);
        int otpRequest = Integer.parseInt(otp_request);

        VerificationRequestEntity user = verificationRepo.findUserByAccountId(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found."));

        var currentOtp = user.getPhoneOTP();
        var createdAt = user.getOtpCreatedAt();

        // 1. Check if an OTP even exists
        if (currentOtp == null || createdAt == null) {
            logger.info("No active OTP found for user.");
            return false;
        }

        // 2. Check if the 10 minutes have expired
        var minutesElapsed = Duration.between(createdAt, LocalDateTime.now()).toMinutes();
        if (minutesElapsed >= 10) {
            logger.warn("Clearing OTP because it has expired, minutes elapsed: {}", minutesElapsed);

            // Clear it so they can't try again with an expired code
            user.setPhoneOTP(null);
            user.setOtpCreatedAt(null);
            verificationRepo.save(user);
            return false;
        }

        // 3. Match the OTP
        if (otpRequest == currentOtp) {
            logger.info("OTP matched successfully.");
            // Clear the OTP immediately on success
            user.setPhoneOTP(null);
            user.setOtpCreatedAt(null);
            user.setPhoneVerified(true);
            if (user.getConfidenceScore() < 53) {
                user.setConfidenceScore(53);
            }
            verificationRepo.save(user);

            logger.info("Now persisting Email OTP.");
            persistOTP(user.getUserCredentialsEntity().getEmail());

            return true;
        } else {
            logger.info("Incorrect OTP submitted.");
            return false;
        }
    }

    @Override
    public Boolean checkEmailOTPmatches(String otp_request, String user_id) {

        Long userId = Long.parseLong(user_id);
        int otpRequest = Integer.parseInt(otp_request);

        VerificationRequestEntity user = verificationRepo.findUserByAccountId(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found."));

        var currentOtp = user.getEmailOTP();
        var createdAt = user.getOtpCreatedAt();

        // 1. Check if an OTP even exists
        if (currentOtp == null || createdAt == null) {
            logger.info("No active OTP found for user.");
            return false;
        }

        // 2. Check if the 10 minutes have expired
        var minutesElapsed = Duration.between(createdAt, LocalDateTime.now()).toMinutes();
        if (minutesElapsed >= 10) {
            logger.warn("Clearing OTP because it has expired, minutes elapsed: {}", minutesElapsed);

            // Clear it so they can't try again with an expired code
            user.setEmailOTP(null);
            user.setOtpCreatedAt(null);
            verificationRepo.save(user);
            return false;
        }

        // 3. Match the OTP
        if (otpRequest == currentOtp) {
            logger.info("OTP matched successfully.");
            // Clear the OTP immediately on success
            user.setEmailOTP(null);
            user.setOtpCreatedAt(null);
            user.setEmailVerified(true);
            if (user.getConfidenceScore() < 55) {
                user.setConfidenceScore(55);
            }
            verificationRepo.save(user);

            return true;
        } else {
            logger.info("Incorrect OTP submitted.");
            return false;
        }
    }

    @Override
    public Map<String, Boolean> checkOTPverification(String user_id){

        Long userId = Long.parseLong(user_id);

        VerificationRequestEntity user = verificationRepo.findUserByAccountId(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found."));

        return Map.of(
                "emailVerification", user.getEmailVerified(),
                "phoneVerification", user.getPhoneVerified()
        );

    }

    @Override
    @Transactional
    public Boolean updateEmailNotification(String newEmail, String user_id) {

        Long userId = Long.parseLong(user_id);

        //Find current email and send notification
        VerificationRequestEntity user = verificationRepo.findUserByAccountId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Verification record not found."));

        user.setRequestedCredentialUpdate(newEmail);
        logger.info(user.getRequestedCredentialUpdate());
        verificationRepo.save(user);

        //communicationServicesImp.notifyOldEmailOfUpdate(newEmail, user.get().getEmail());

        Integer otp = createOTP();

        //Persist and send otp to users new email
        Boolean otpSent = persistOTP(newEmail, userId, otp);
        logger.info("OTP persisted and sent successfully? {}", otpSent);

        return otpSent != null;
    }

    @Override
    @Transactional
    public Boolean updateEmail(String user_id) {
        Long userId = Long.parseLong(user_id);

        VerificationRequestEntity verifiedUser = verificationRepo.findUserByAccountId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Verification record not found for user: " + userId));

        String newEmail = verifiedUser.getRequestedCredentialUpdate();
        logger.info(newEmail);
        if (newEmail == null || newEmail.isBlank()) {
            logger.warn("No pending email update found for user: {}", userId);
            return false;
        }

        UserCredentialsEntity user = userCredRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));
        logger.info(user.getEmail());
        user.setEmail(newEmail.trim());
        verifiedUser.setRequestedCredentialUpdate(null);

        logger.info("Updated email for user {} to {}", userId, user.getEmail());
        return true;
    }


    @Override
    @Transactional
    public Boolean updatePhoneNotification(String newNumber, String user_id) {

        Long userId = Long.parseLong(user_id);

        //Find current email and send notification
        VerificationRequestEntity user = verificationRepo.findUserByAccountId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Verification record not found."));

        user.setRequestedCredentialUpdate(newNumber);
        logger.info(user.getRequestedCredentialUpdate());
        verificationRepo.save(user);

        Integer otp = createOTP();

        //Persist and send otp to users new email
        Boolean otpSent = persistOTP(newNumber, userId, otp);
        logger.info("OTP persisted and sent successfully? {}", otpSent);

        return otpSent != null;
    }

    @Override
    @Transactional
    public Boolean updatePhone(String user_id) {
        Long userId = Long.parseLong(user_id);

        VerificationRequestEntity verifiedUser = verificationRepo.findUserByAccountId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Verification record not found for user: " + userId));

        String newNumber = verifiedUser.getRequestedCredentialUpdate();
        logger.info(newNumber);
        if (newNumber == null || newNumber.isBlank()) {
            logger.warn("No pending phone update found for user: {}", userId);
            return false;
        }

        UserCredentialsEntity user = userCredRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));
        logger.info(user.getPhone());
        user.setPhone(newNumber.trim());
        verifiedUser.setRequestedCredentialUpdate(null);

        logger.info("Updated phone for user {} to {}", userId, user.getPhone());
        return true;
    }

    @Override
    @Transactional
    public Boolean updatePasswordNotification(String newPassword, String user_id) {

        Long userId = Long.parseLong(user_id);

        //Find current email and send notification
        VerificationRequestEntity user = verificationRepo.findUserByAccountId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Verification record not found."));

        user.setRequestedCredentialUpdate(newPassword);
        logger.info(user.getRequestedCredentialUpdate());
        verificationRepo.save(user);

        Integer otp = createOTP();

        //Persist and send otp to users new email
        Boolean otpSent = persistPasswordOTP(userId, otp);
        logger.info("OTP persisted and sent successfully? {}", otpSent);

        return otpSent != null;
    }

    @Override
    @Transactional
    public Boolean updatePassword(String user_id) {
        Long userId = Long.parseLong(user_id);

        VerificationRequestEntity verifiedUser = verificationRepo.findUserByAccountId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Verification record not found for user: " + userId));

        String newPassword = verifiedUser.getRequestedCredentialUpdate();
        logger.info(newPassword);
        if (newPassword == null || newPassword.isBlank()) {
            logger.warn("No pending password update found for user: {}", userId);
            return false;
        }

        UserCredentialsEntity user = userCredRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));
        logger.info(user.getPassword());
        user.setPassword(newPassword.trim());
        verifiedUser.setRequestedCredentialUpdate(null);

        logger.info("Updated password for user {}", userId);
        return true;
    }

    @Override
    @Transactional
    public Boolean persistPasswordOTP(Long userId, Integer newOTP) {

        logger.info(String.valueOf(userId));
        logger.info(String.valueOf(newOTP));

        try {

            Optional<UserCredentialsEntity> optionalUser = Optional.of(userCredRepo.getReferenceById(userId));

            UserCredentialsEntity user = optionalUser.get();

            VerificationRequestEntity getUser = verificationRepo.findUserByAccountId(user.getAccountId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found."));

            getUser.setPasswordOTP(newOTP);
            getUser.setOtpCreatedAt(LocalDateTime.now());
            getUser.setPasswordVerified(false);
            verificationRepo.save(getUser);

            logger.info("Saved new password OTP to entity: {}", getUser.getEmailOTP());

            communicationServicesImp.initiateEmailService(user.getEmail(), newOTP);

            return true;

        } catch (Exception e) {
            logger.error("Failed to add password OTP to entity: ", e);
            return false;
        }

    }

}
