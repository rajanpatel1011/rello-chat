package com.rajan.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

import java.security.Principal;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/")
    public String index(Principal principal, Model model) {
        // If user is authenticated, expose their username to the template so the client JS
        // can auto-skip the username page.
        if (principal != null) {
            model.addAttribute("loginUsername", principal.getName());
        }
        return "index";
    }

}