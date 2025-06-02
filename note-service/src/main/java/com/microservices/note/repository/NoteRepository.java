package com.microservices.note.repository;

import com.microservices.note.model.AccessRole;
import com.microservices.note.model.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends Neo4jRepository<Note, String> {
    
    @Query(value = """
        MATCH (u:User {userId: $userId})-[r:AUTHOR|EDITOR|SPECTATOR]->(n:Note)
        RETURN n
        ORDER BY n.updatedAt DESC
        """,
        countQuery = """
        MATCH (u:User {userId: $userId})-[r:AUTHOR|EDITOR|SPECTATOR]->(n:Note)
        RETURN count(n)
        """)
    Page<Note> findAllByUserId(@Param("userId") String userId, Pageable pageable);
    
    @Query("""
        MATCH (u:User {userId: $userId})-[r]->(n:Note {id: $noteId})
        WHERE type(r) IN $roles
        RETURN count(n) > 0
        """)
    boolean hasAccess(@Param("noteId") String noteId, @Param("userId") String userId, @Param("roles") List<AccessRole> roles);
    
    @Query("""
        MATCH (u:User {userId: $userId}), (n:Note {id: $noteId})
        CREATE (u)-[r:$role {grantedAt: datetime()}]->(n)
        """)
    void createUserAccess(@Param("userId") String userId, @Param("noteId") String noteId, @Param("role") AccessRole role);
}
