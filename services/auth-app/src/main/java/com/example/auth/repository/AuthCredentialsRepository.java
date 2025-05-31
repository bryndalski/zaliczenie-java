package com.example.auth.repository;

import com.example.auth.model.AuthCredentials;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AuthCredentialsRepository extends MongoRepository<AuthCredentials, String> {
    Optional<AuthCredentials> findByUserEmail(String userEmail);
    Optional<AuthCredentials> findByUserId(String userId);
}
