package ru.intech.nedochat.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    @GetMapping(value = {"/admin/users"})
    public String index() {
        return "admin";
    }
}
