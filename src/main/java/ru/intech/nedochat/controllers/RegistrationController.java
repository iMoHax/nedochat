package ru.intech.nedochat.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.intech.nedochat.entity.User;
import ru.intech.nedochat.service.UserExistAuthenticationException;
import ru.intech.nedochat.service.UsersService;
import ru.intech.nedochat.model.RegistrationForm;

import javax.validation.Valid;

@Controller
@RequestMapping("/registration")
public class RegistrationController {
    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

    private UsersService usersService;
    private PasswordEncoder passwordEncoder;

    public RegistrationController(UsersService usersService, PasswordEncoder passwordEncoder) {
        this.usersService = usersService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String registrationForm(Model model){
        model.addAttribute("registrationForm", new RegistrationForm());
        return "registration";
    }

    @PostMapping
    public String processRegistration(@Valid RegistrationForm form, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "registration";
        }
        if (!form.getPassword().equals(form.getConfirm())){
            bindingResult.addError(new FieldError("registrationForm", "confirm", "Пароли не совпадают"));
            return "registration";
        }
        User user;
        try {
            user = usersService.add(form.toUser(passwordEncoder));
        } catch (UserExistAuthenticationException e){
            bindingResult.addError(new FieldError("registrationForm", "username", e.getLocalizedMessage()));
            return "registration";
        }
        logger.info("Register new user: {}", user.getUsername());
        return "redirect:/login";
    }
}
