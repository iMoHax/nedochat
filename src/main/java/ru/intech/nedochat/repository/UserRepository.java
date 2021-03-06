package ru.intech.nedochat.repository;

import org.springframework.data.repository.CrudRepository;
import ru.intech.nedochat.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Integer> {

    Optional<User> findByUsername(String login);
    Optional<User> findByUsernameAndDisabled(String login, boolean disabled);
    boolean existsByUsername(String login);
    boolean existsByUsernameAndDisabled(String login, boolean disabled);
    List<User> findAllByDisabledOrderByName(boolean disabled);
}
