package ru.intech.nedochat.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.intech.nedochat.entity.Role;
import ru.intech.nedochat.entity.User;
import ru.intech.nedochat.repository.UserRepository;

import javax.annotation.security.RolesAllowed;
import java.util.Optional;

@RestController
@RequestMapping(path = "/api/user")
public class UserController {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @RolesAllowed(Role.ADMIN)
    public void deleteUser(@PathVariable("userId") Integer userId) {
        Optional<User> user = userRepository.findById(userId);
        user.ifPresent((u) -> {
            u.setDisabled(true);
            userRepository.save(u);
        });
    }

    @PatchMapping(path = "/{userId}", consumes="application/json")
    @RolesAllowed(Role.ADMIN)
    public ResponseEntity patchUser(@PathVariable("userId") Integer userId, @RequestBody User patch) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()){
            User u = user.get();
            if (patch.getUsername() != null){
                u.setUsername(patch.getUsername());
            }
            if (patch.getName() != null){
                u.setName(patch.getName());
            }
            if (patch.getPassword() != null){
                u.setPassword(passwordEncoder.encode(patch.getPassword()));
            }
            userRepository.save(u);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
