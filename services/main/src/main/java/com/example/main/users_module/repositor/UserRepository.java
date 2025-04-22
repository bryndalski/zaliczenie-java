package com.example.main.users_module.repositor;

import com.example.main.users_module.entities.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends MongoRepository<User, UUID> {

    Optional<User> findOneById(UUID id);

    Optional<User> findOneByEmail(String email);

    default User create(User user) {
        return save(user);
    }

    default User update(User user) {
        return save(user);
    }

    default void remove(User user) {
        delete(user);
    }
}