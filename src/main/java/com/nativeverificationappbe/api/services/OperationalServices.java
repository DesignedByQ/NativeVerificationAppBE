package com.nativeverificationappbe.api.services;

import com.nativeverificationappbe.api.models.DocumentDTO;
import com.nativeverificationappbe.api.models.UserCredentialsEntity;

public interface OperationalServices {

    Boolean uploadDocument(DocumentDTO documentDTO, String user_id);
}
