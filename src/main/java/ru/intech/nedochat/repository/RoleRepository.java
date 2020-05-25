package ru.intech.nedochat.repository;


import org.springframework.data.repository.CrudRepository;
import ru.intech.nedochat.entity.Role;

import java.util.Optional;

public interface RoleRepository extends CrudRepository<Role, Integer> {
    Optional<Role> findByName(String login);
}
