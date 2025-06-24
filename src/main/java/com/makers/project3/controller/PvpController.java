package com.makers.project3.controller;

import com.makers.project3.Service.AuthenticatedUserService;
import com.makers.project3.Service.LeaderboardService;
import com.makers.project3.model.Deck;
import com.makers.project3.model.PvpGame;
import com.makers.project3.model.User;
import com.makers.project3.repository.DeckRepository;
import com.makers.project3.repository.PvpGameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Iterator;

@Controller
public class PvpController {

    @Autowired
    AuthenticatedUserService authenticatedUserService;
    @Autowired
    DeckRepository deckRepository;
    @Autowired
    LeaderboardService leaderboardService;
    @Autowired
    PvpGameRepository pvpGameRepository;


    @GetMapping("/pvp")
    public ModelAndView viewPvpGamesList() {
        ModelAndView mav = new ModelAndView("pvpgames");

        User currentUser = authenticatedUserService.getAuthenticatedUser();
        mav.addObject("currentUser", currentUser);

        Iterable<Deck> decks = deckRepository.findAll();
        mav.addObject("decks", decks);

        List<User> playersMostWins = leaderboardService.getTopPlayersByWins(3);
        mav.addObject("playersMostWins", playersMostWins);

        Iterable<PvpGame> pvpGames = pvpGameRepository.findAll();
        mav.addObject("pvpGames", pvpGames);
        return mav;
    }




}
