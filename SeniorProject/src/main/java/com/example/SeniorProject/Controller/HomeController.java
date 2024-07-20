package com.example.SeniorProject.Controller;

import org.springframework.http.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

@Controller
public class HomeController
{
    @GetMapping("/")
    public String home()
    {
        return "index";
    }

    @GetMapping("/login")
    public String login()
    {
        return "Login";
    }

    @GetMapping("/signup")
    public String signup()
    {
        return "signup";
    }

    @GetMapping("/about")
    public String about()
    {
        return "about";
    }

    @GetMapping("/faq")
    public String faq()
    {
        return "faq";
    }

    @GetMapping("/gallery")
    public String gallery()
    {
        return "gallery";
    }

    @GetMapping("/healthcheck")
    public ResponseEntity<?> performHealthCheck()
    {
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
