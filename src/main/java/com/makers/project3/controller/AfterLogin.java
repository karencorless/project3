package com.makers.project3.controller;

import com.makers.project3.Service.AuthenticatedUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class AfterLogin {

    @Autowired
    AuthenticatedUserService authenticatedUserService;

    @GetMapping("/users/after-login")
    public ModelAndView authenticateUserAndRedirect() {
        authenticatedUserService.getAuthenticatedUser();
        return new ModelAndView("redirect:/homepage");
    }
}
