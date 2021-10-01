package com.github.makewheels.videoshare.videoservice;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {
    @RequestMapping("/")
    public String root() {
        return "redirect:/login.html";
    }

    @RequestMapping("loginSuccess")
    public String onLoginSuccess() {
        return "redirect:/home.html";
    }
}
