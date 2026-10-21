package com.nativeverificationappbe.api.services;

import com.nativeverificationappbe.api.models.DocumentDTO;
import com.nativeverificationappbe.api.models.DocumentEntity;
import com.nativeverificationappbe.api.models.Mapper;
import com.nativeverificationappbe.api.models.UserCredentialsEntity;
import com.nativeverificationappbe.api.repos.DocumentRepo;
import com.nativeverificationappbe.api.repos.UserCredRepo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OperationalServicesImp implements OperationalServices{

    private static final Logger logger =  LoggerFactory.getLogger(OperationalServicesImp.class);

    private final DocumentRepo documentRepo;

    private final Mapper mapper;

    private final UserCredRepo userCredRepo;

    @Override
    public Boolean uploadDocument(DocumentDTO documentDTO, String user_id) {

        Long userId = Long.parseLong(user_id);

        UserCredentialsEntity userCredentialsEntity = userCredRepo.getReferenceById(userId);

        DocumentEntity result = documentRepo.save(mapper.documentDTOtoEntity(documentDTO, userCredentialsEntity));

        return result.getId() != null && userCredentialsEntity.getAccountId() != null;
    }

}
