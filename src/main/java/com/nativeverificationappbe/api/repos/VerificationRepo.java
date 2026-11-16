package com.nativeverificationappbe.api.repos;

import com.nativeverificationappbe.api.models.VerificationRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationRepo extends JpaRepository<VerificationRequestEntity, Long> {

    @Query("SELECT v FROM VerificationRequestEntity v WHERE v.userCredentialsEntity.accountId = :accountId")
    Optional<VerificationRequestEntity> findUserByAccountId(@Param("accountId") Long accountId);

}
