package com.nativeverificationappbe.api.models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class Mapper {

    private static final Logger logger = LoggerFactory.getLogger(Mapper.class);

    public UserCredentialsDTO responseToUserCredentialsDTO(PremblyLivelinessResponseNinData premblyResponseData) {
        try {
            return new UserCredentialsDTO(
                    premblyResponseData.getTitle(),
                    premblyResponseData.getFirstname(),
                    premblyResponseData.getMiddlename(),
                    premblyResponseData.getSurname(),
                    premblyResponseData.getBirthdate(),
                    premblyResponseData.getGender(),
                    premblyResponseData.getNin(),
                    premblyResponseData.getTelephoneno(),
                    premblyResponseData.getEmail(),
                    null
            );
        } catch (Exception e) {
            logger.info("Failed to create user credentials DTO: {}", String.valueOf(e));
            throw new IllegalStateException("Creating user credentials DTO failed. " + e);
        }
    }

    public UserCredentialsEntity userCredentialsDTOtoEntity(UserCredentialsDTO userCredentialsDTO){

        try {

            return new UserCredentialsEntity(
                    null,
                    userCredentialsDTO.getTitle(),
                    userCredentialsDTO.getFirstname(),
                    userCredentialsDTO.getMiddlename(),
                    userCredentialsDTO.getSurname(),
                    userCredentialsDTO.getBirthdate(),
                    userCredentialsDTO.getGender(),
                    userCredentialsDTO.getNin(),
                    userCredentialsDTO.getPhone(),
                    userCredentialsDTO.getEmail(),
                    userCredentialsDTO.getPassword()
            );

        } catch (Exception e) {
            logger.error("Failed to map UserCredentialsDTO to entity. DTO: {}", userCredentialsDTO.getEmail(), e);
            throw new IllegalStateException("Failed to map UserCredentialsDTO to entity", e);
        }

    }

}
