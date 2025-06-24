package com.makers.project3.controller;

import com.makers.project3.Service.AuthenticatedUserService;
import com.makers.project3.Service.LeaderboardService;
import com.makers.project3.model.Deck;
import com.makers.project3.model.User;
import com.makers.project3.repository.DeckRepository;
import com.makers.project3.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
public class AfterLogin {

    @Autowired
    AuthenticatedUserService authenticatedUserService;

    @Autowired
    DeckRepository deckRepository;

    @Autowired
    LeaderboardService leaderboardService;

    @GetMapping("/users/after-login")
    public ModelAndView authenticateUserAndRedirect() {
        authenticatedUserService.getAuthenticatedUser();
        return new ModelAndView("redirect:/homepage");
    }

    @GetMapping("/homepage")
    public ModelAndView viewHomepage() {
        ModelAndView mav = new ModelAndView("homepage");

        User currentUser = authenticatedUserService.getAuthenticatedUser();
        mav.addObject("currentUser", currentUser);

        Iterable<Deck> decks = deckRepository.findAll();
        mav.addObject("decks", decks);

        List<User> playersMostWins = leaderboardService.getTopPlayersByWins(3);
        mav.addObject("playersMostWins", playersMostWins);

        return mav;
    }
}
