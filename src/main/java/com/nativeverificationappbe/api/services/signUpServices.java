package com.nativeverificationappbe.api.services;

import com.nativeverificationappbe.api.models.NINandSelfieDTO;
import com.nativeverificationappbe.api.models.PremblyLivelinessResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.Optional;

@Service
public class SignUpServices {

    private static final Logger logger = LoggerFactory.getLogger(SignUpServices.class);

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.prembly.com")
            .build();


    // Initialize dotenv
    @Value("${X-Api-Key}")
    private String getApiKey;



    public Boolean livelinessCheck(NINandSelfieDTO ninAndSelfieDTO) {

        // Fetch the key
        String apiKey = Optional.ofNullable(getApiKey)
                .orElseThrow(() -> new IllegalStateException("X-Api-Key not found in .env file"));

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

}
