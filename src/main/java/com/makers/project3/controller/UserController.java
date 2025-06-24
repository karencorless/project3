package com.makers.project3.controller;

import com.makers.project3.Service.UserService;
import com.makers.project3.model.User;
import com.makers.project3.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Date;

@Controller
public class UserController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;

    @GetMapping("/settings")
    public String viewSettingsPage(Model model) {
        User currentUser = (userRepository.findById(userService.getCurrentUserId())).orElse(null);
        model.addAttribute("currentUser", currentUser);
        return "settings";
    }

    @PostMapping("/settings")
    public RedirectView updateSettings(@RequestParam("username") String username, @RequestParam("birthday") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date birthday) {
        User currentUser = (userRepository.findById(userService.getCurrentUserId())).orElse(null);

        //        @RequestParam("imageFile") MultipartFile imageFile
        assert currentUser != null;
        currentUser.setUsername(username);
        currentUser.setBirthday(birthday);  //need to fix parsing issue


        userRepository.save(currentUser);
        return new RedirectView("/settings");
    }

    @GetMapping("/privacy")
    public String cookies(Model model) {
        User currentUser = (userRepository.findById(userService.getCurrentUserId())).orElse(null);
        model.addAttribute("currentUser", currentUser);
        return "cookies";
    }
}
