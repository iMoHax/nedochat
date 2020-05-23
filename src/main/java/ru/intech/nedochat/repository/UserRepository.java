package ru.intech.nedochat.repository;

import org.springframework.data.repository.CrudRepository;
import ru.intech.nedochat.entity.User;

public interface UserRepository extends CrudRepository<User, Long> {

    User findByUsername(String login);
    boolean existsByUsername(String login);
}
