package com.epam.rd.autocode.spring.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorPageController {

    @GetMapping("/errors/403")
    public String accessDenied() {
        return "errors/403";
    }

    @GetMapping("/errors/401")
    public String unAuthorized() {
        return "errors/401";
    }
}
