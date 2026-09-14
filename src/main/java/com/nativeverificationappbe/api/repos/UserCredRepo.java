package com.nativeverificationappbe.api.repos;

import com.nativeverificationappbe.api.models.UserCredentialsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserCredRepo extends JpaRepository<UserCredentialsEntity, Long> {

    @Query("SELECT u FROM UserCredentialsEntity u WHERE u.phone = :num")
    UserCredentialsEntity findUserCredentialsByPhone(@Param("num") String num);

    @Query("SELECT u FROM UserCredentialsEntity u WHERE u.email = :eml")
    UserCredentialsEntity findUserSignUpByEmail(@Param("eml") String eml);

    Optional<UserCredentialsEntity> findByNin(String nin);

    boolean existsByEmail(String email);


}
