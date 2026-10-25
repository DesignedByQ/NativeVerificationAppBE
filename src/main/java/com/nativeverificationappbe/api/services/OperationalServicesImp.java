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
                validateExpiration(documentDTO, userDocs, documentDTO.getDocumentType());
                userDocs.setNationalDocNumber(documentDTO.getDocumentNumber());
                userDocs.setNationalDocImage(documentDTO.getDocumentImage());
            }
            case "VOTERS_CARD" -> {
                validateExpiration(documentDTO, userDocs, documentDTO.getDocumentType());
                userDocs.setVotersDocNumber(documentDTO.getDocumentNumber());
                userDocs.setVotersDocImage(documentDTO.getDocumentImage());
            }
            case "DRIVERS_LICENSE" -> {
                validateExpiration(documentDTO, userDocs, documentDTO.getDocumentType());
                userDocs.setDriversDocNumber(documentDTO.getDocumentNumber());
                userDocs.setDriversDocImage(documentDTO.getDocumentImage());
                userDocs.setDriversDocExpiry(documentDTO.getDocumentExpiration());
            }
            case "PASSPORT" -> {
                validateExpiration(documentDTO, userDocs, documentDTO.getDocumentType());
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
    private void validateExpiration(DocumentDTO incoming, DocumentEntity existing, String docName) {
        if (incoming != null && existing != null) {
            // If existing expiration is LATER than incoming expiration, reject

            switch (docName) {
                case "NATIONAL_ID" -> {
                    if (existing.getNationalDocNumber() != null) {
                        throw new IllegalStateException("User's " + docName + " document already exists on the system.");
                    }
                }
                case "VOTERS_CARD" -> {
                    if (existing.getVotersDocNumber() != null) {
                        throw new IllegalStateException("User's " + docName + " document already exists on the system.");
                    }
                }
                case "DRIVERS_LICENSE" -> {
                    if (existing.getDriversDocNumber() != null) {
                        if (incoming.getDocumentNumber().equals(existing.getDriversDocNumber()) || incoming.getDocumentExpiration().compareTo(existing.getDriversDocExpiry()) <= 0) {
                            throw new IllegalStateException("The same or newer " + docName + " document already exists on the system.");
                        }
                    }
                }
                case "PASSPORT" -> {
                    if (existing.getPassportDocNumber() != null) {
                        if (incoming.getDocumentNumber().equals(existing.getPassportDocNumber()) || incoming.getDocumentExpiration().compareTo(existing.getPassportDocExpiry()) <= 0) {
                            throw new IllegalStateException("The same or newer " + docName + " document already exists on the system.");
                        }
                    }
                }
                default -> throw new IllegalArgumentException("Unsupported document type: " + docName);
            }


        }
    }

}
