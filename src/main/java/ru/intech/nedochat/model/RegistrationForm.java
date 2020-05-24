package ru.intech.nedochat.model;

import org.springframework.security.crypto.password.PasswordEncoder;
import ru.intech.nedochat.entity.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class RegistrationForm {
    @Size(min=3, message = "Имя пользователя должно содержать не менее {min} знаков")
    private String username;
    @NotBlank
    private String name;
    @Size(min=5, message = "Пароль должен содержать не менее {min} знаков")
    private String password;
    @NotBlank

    private String confirm;

    public RegistrationForm() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirm() {
        return confirm;
    }

    public void setConfirm(String confirm) {
        this.confirm = confirm;
    }

    public User toUser(PasswordEncoder passwordEncoder) {
        return new User(username, name, passwordEncoder.encode(password));
    }
}
