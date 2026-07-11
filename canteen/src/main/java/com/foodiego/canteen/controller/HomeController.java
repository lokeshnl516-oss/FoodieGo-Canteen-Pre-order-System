package com.foodiego.canteen.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@CrossOrigin(origins = "https://lokeshnl516-oss.github.io", allowCredentials = "true")
public class HomeController {

    @GetMapping("/")
    public String showHome() {
        return "index";
    }

    @GetMapping("/home")
    public String home() {
        return "redirect:/";
    }
}
