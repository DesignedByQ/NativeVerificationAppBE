package com.nativeverificationappbe.api.services;

import com.nativeverificationappbe.api.models.DocumentDTO;
import com.nativeverificationappbe.api.models.DocumentEntity;
import com.nativeverificationappbe.api.repos.DocumentRepo;
import com.nativeverificationappbe.api.repos.UserCredRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OperationalServicesImp implements OperationalServices{

    private static final Logger logger =  LoggerFactory.getLogger(OperationalServicesImp.class);

    private final DocumentRepo documentRepo;

    private final UserCredRepo userCredRepo;

    @Override
    @Transactional
    public Boolean uploadDocument(DocumentDTO documentDTO, String user_id) {

        Long userId = Long.parseLong(user_id);

        //UserCredentialsEntity user = userCredRepo.getReferenceById(userId);

        DocumentEntity userDocs = documentRepo.findUserByAccountId(userId);

        if (userDocs == null) {

            userDocs = new DocumentEntity();
            userDocs.setUserCredentialsEntity(userCredRepo.getReferenceById(userId));

        }

        //Update IDs
        if (documentDTO.getDocumentType() == null) {
            throw new IllegalArgumentException("Document type cannot be null.");
        }

        // 2. Handle document types dynamically
        switch (documentDTO.getDocumentType()) {
            case "NATIONAL_ID" -> {
                validateExpiration(documentDTO.getDocumentExpiration(), userDocs.getNationalDocExpiry(), documentDTO.getDocumentType());
                userDocs.setNationalDocNumber(documentDTO.getDocumentNumber());
                userDocs.setNationalDocImage(documentDTO.getDocumentImage());
                userDocs.setNationalDocExpiry(documentDTO.getDocumentExpiration());
            }
            case "VOTERS_CARD" -> {
                validateExpiration(documentDTO.getDocumentExpiration(), userDocs.getVotersDocExpiry(), documentDTO.getDocumentType());
                userDocs.setVotersDocNumber(documentDTO.getDocumentNumber());
                userDocs.setVotersDocImage(documentDTO.getDocumentImage());
                userDocs.setVotersDocExpiry(documentDTO.getDocumentExpiration());
            }
            case "DRIVERS_LICENSE" -> {
                validateExpiration(documentDTO.getDocumentExpiration(), userDocs.getDriversDocExpiry(), documentDTO.getDocumentType());
                userDocs.setDriversDocNumber(documentDTO.getDocumentNumber());
                userDocs.setDriversDocImage(documentDTO.getDocumentImage());
                userDocs.setDriversDocExpiry(documentDTO.getDocumentExpiration());
            }
            case "PASSPORT" -> {
                validateExpiration(documentDTO.getDocumentExpiration(), userDocs.getPassportDocExpiry(), documentDTO.getDocumentType());
                userDocs.setPassportDocNumber(documentDTO.getDocumentNumber());
                userDocs.setPassportDocImage(documentDTO.getDocumentImage());
                userDocs.setPassportDocExpiry(documentDTO.getDocumentExpiration());
            }
            default -> throw new IllegalArgumentException("Unsupported document type: " + documentDTO.getDocumentType());
        }

        // 3. Single save call
        DocumentEntity savedEntity = documentRepo.save(userDocs);

        logger.info("Document saved successfully to user: {}", savedEntity.getUserCredentialsEntity().getAccountId());

        return savedEntity.getDocumentsId() != null;
    }


    // Safe helper method to compare date strings without NullPointerExceptions
    private void validateExpiration(String incomingExpiry, String existingExpiry, String docName) {
        if (existingExpiry != null && incomingExpiry != null) {
            // If existing expiration is LATER than incoming expiration, reject
            if (existingExpiry.compareTo(incomingExpiry) <= 0) {
                throw new IllegalStateException("A newer " + docName + " document already exists.");
            }
        }
    }

}
