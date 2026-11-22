package com.nativeverificationappbe.api.services;

import com.nativeverificationappbe.api.models.DocumentDTO;
import com.nativeverificationappbe.api.models.UserCredentialsEntity;
import com.nativeverificationappbe.api.models.VerificationRequestEntity;

public interface OperationalServices {

    Boolean uploadDocument(DocumentDTO documentDTO, String user_id);

    UserCredentialsEntity getUserCredentials(String user_id);

    void updateConfidenceScore(VerificationRequestEntity verifiedUser);

}
