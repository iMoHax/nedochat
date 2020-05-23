package ru.intech.nedochat.controllers;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.intech.nedochat.service.UsersService;
import ru.intech.nedochat.view.RegistrationForm;

import javax.validation.Valid;

@Controller
@RequestMapping("/registration")
public class RegistrationController {

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
        usersService.add(form.toUser(passwordEncoder));
        return "redirect:/login";
    }
}
