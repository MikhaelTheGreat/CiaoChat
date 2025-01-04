package org.ciaochat.controller;

import lombok.RequiredArgsConstructor;
import org.ciaochat.dto.UserDto;
import org.ciaochat.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class SignController {
    private final UserService userService;

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String status, Model model) {
        if (status != null) {
            if (status.equals("error")) {
                model.addAttribute("status", "Wrong username or password!");
            } else if (status.equals("success")) {
                model.addAttribute("status", "Login success!");
            }
        }
        return "login";
    }

    @GetMapping("/register")
    public String register(@RequestParam(required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", "Registration failed!");
        }
        return "registration";
    }

    @PostMapping("/perform_registration")
    public ResponseEntity<?> processRegistration(@RequestBody UserDto userDto) {
        userService.createUser(userDto);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/home")
    public String home(Model model) {
        return "home";
    }
}
