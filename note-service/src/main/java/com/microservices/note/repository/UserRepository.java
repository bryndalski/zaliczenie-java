package com.microservices.note.repository;

import com.microservices.note.model.User;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends Neo4jRepository<User, String> {
    
    @Query("""
        MERGE (u:User {userId: $userId})
        ON CREATE SET u.email = $email, u.username = $username
        ON MATCH SET u.email = $email, u.username = $username
        RETURN u
        """)
    User createOrUpdateUser(@Param("userId") String userId, 
                           @Param("email") String email,
                           @Param("username") String username);
}
