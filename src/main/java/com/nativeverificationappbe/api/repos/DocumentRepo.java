package com.nativeverificationappbe.api.repos;

import com.nativeverificationappbe.api.models.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepo extends JpaRepository<DocumentEntity, Long> {
}
