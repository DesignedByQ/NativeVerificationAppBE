package com.nativeverificationappbe.api.repos;

import com.nativeverificationappbe.api.models.DocumentEntity;
import com.nativeverificationappbe.api.models.VerificationRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepo extends JpaRepository<DocumentEntity, Long> {

    @Query("SELECT v FROM DocumentEntity v WHERE v.userCredentialsEntity.accountId = :accountId")
    DocumentEntity findUserByAccountId(@Param("accountId") Long accountId);

}
