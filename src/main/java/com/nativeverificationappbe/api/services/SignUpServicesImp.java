package com.nativeverificationappbe.api.services;

import com.nativeverificationappbe.api.models.*;
import com.nativeverificationappbe.api.repos.UserCredRepo;
import com.nativeverificationappbe.api.repos.VerificationRepo;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;


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

            if (!response.getStatus()) {
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

            verificationRepo.save(associatedVerificationRequest);

            logger.info("Account created successfully.");

            session.setAttribute("USER_ID", user.getAccountId().toString());
            MDC.put("userId", user.getAccountId().toString());

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

        if (contact.contains("@")) {

            try {

                Optional<UserCredentialsEntity> optionalUser = Optional.ofNullable(userCredRepo.findUserSignUpByEmail(contact));

                if (optionalUser.isEmpty()) {
                    logger.warn("User not found.");
                    return false;
                }

                UserCredentialsEntity user = optionalUser.get();

                VerificationRequestEntity getUser = verificationRepo.findUserByAccountId(user.getAccountId());

                if (getUser != null) {
                    getUser.setEmailOTP(newOTP);
                    getUser.setOtpCreatedAt(LocalDateTime.now());
                    verificationRepo.save(getUser);
                    logger.info("Saved new email OTP to entity: {}", user.getEmail());

                }

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

                VerificationRequestEntity getUser = verificationRepo.findUserByAccountId(user.getAccountId());

                if (getUser != null) {
                    getUser.setPhoneOTP(newOTP);
                    getUser.setOtpCreatedAt(LocalDateTime.now());
                    verificationRepo.save(getUser);
                    logger.info("Saved new phone OTP to entity: {}", user.getEmail());

                }

                communicationServicesImp.initiatePhoneService(contact, newOTP);

            } catch (Exception e) {
                logger.error("Failed to add phone OTP to entity: ", e);
                return false;
            }

        }

        return false;
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
            redisTemplate.expire(key, 30, TimeUnit.MINUTES);
        }

        return count;
    }



}
