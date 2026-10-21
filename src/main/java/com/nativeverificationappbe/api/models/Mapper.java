package com.nativeverificationappbe.api.models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

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
                    userCredentialsDTO.getPassword(),
                        null
            );

        } catch (Exception e) {
            logger.error("Failed to map UserCredentialsDTO to entity. DTO: {}", userCredentialsDTO.getEmail(), e);
            throw new IllegalStateException("Failed to map UserCredentialsDTO to entity", e);
        }

    }


    public AddressEntity addressDTOtoEntity(AddressDetails addressDTO) {

        try {

            return new AddressEntity(
                    null,
                    addressDTO.getUserAddress().getNumber(),
                    addressDTO.getUserAddress().getBuildingName(),
                    addressDTO.getUserAddress().getStreet(),
                    addressDTO.getUserAddress().getCity(),
                    addressDTO.getUserAddress().getState(),
                    addressDTO.getUserAddress().getPostalCode(),
                    addressDTO.getUserAddress().getDescription(),
                    addressDTO.getUserAddress().getLatitude(),
                    addressDTO.getUserAddress().getLongitude()
            );
        } catch (Exception e) {
            logger.error("Failed to map addressDTO to Entity.");
            throw new IllegalStateException("Mapping address entity failed. ", e);
        }
    }

    public DocumentEntity documentDTOtoEntity(DocumentDTO documentDTO, UserCredentialsEntity userCredentialsEntity) {
        try {
            return new DocumentEntity(
                    null,
                    documentDTO.getNationalId(),
                    documentDTO.getVotersCard(),
                    documentDTO.getDriversLicense(),
                    documentDTO.getPassport(),
                    userCredentialsEntity
            );
        } catch (Exception e) {
            logger.error("Failed to map documentDTO to Entity.", e);
            throw new RuntimeException("Mapping failed", e);
        }
    }

}
