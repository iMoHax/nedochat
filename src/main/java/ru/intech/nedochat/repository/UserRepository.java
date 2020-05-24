package ru.intech.nedochat.repository;

import org.springframework.data.repository.CrudRepository;
import ru.intech.nedochat.entity.User;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByUsername(String login);
    boolean existsByUsername(String login);
}
