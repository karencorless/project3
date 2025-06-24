package com.makers.project3.controller;

import com.makers.project3.Service.AuthenticatedUserService;
import com.makers.project3.Service.UserService;
import com.makers.project3.model.User;
import com.makers.project3.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Date;

@RestController
public class AfterLogin {

    @Autowired
    AuthenticatedUserService authenticatedUserService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;


    @GetMapping("/users/after-login")
    public ModelAndView authenticateUserAndRedirect() {
        authenticatedUserService.getAuthenticatedUser();
        return new ModelAndView("redirect:/homepage");
    }

    @GetMapping("")
    public ModelAndView redirectEmpty() {
        authenticatedUserService.getAuthenticatedUser();
        return new ModelAndView("redirect:/homepage");
    }

    @GetMapping("/")
    public ModelAndView redirectSlash() {
        authenticatedUserService.getAuthenticatedUser();
        return new ModelAndView("redirect:/homepage");
    }


    @GetMapping("/homepage")
    public ModelAndView viewHomepage() {
        User currentUser = authenticatedUserService.getAuthenticatedUser();
        ModelAndView mav = new ModelAndView("homepage");
        mav.addObject("currentUser", currentUser);
        return mav;
    }
}
