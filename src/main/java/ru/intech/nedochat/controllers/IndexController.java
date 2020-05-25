package ru.intech.nedochat.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class IndexController {

    @GetMapping(value = {"/", "/index"})
    public String index(Principal principal) {
        if (principal == null) return "redirect:/login";
        else {
            return "redirect:/chat";
        }
    }
}
