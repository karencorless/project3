package com.makers.project3.controller;

import com.makers.project3.Service.GameService;
import com.makers.project3.model.Card;
import com.makers.project3.repository.DeckRepository;
import com.makers.project3.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

import java.util.*;

@Controller
public class GameController {

    @Autowired
    GameService gameService;
    @Autowired
    PlayerRepository playerRepository;
    @Autowired
    DeckRepository deckRepository;

//   To start a new game. Will take input for chosen decks and points to win
    @GetMapping("/game/start")
    public RedirectView newGame() {
        List<Long> chosenDeckIds = new ArrayList<>();
        chosenDeckIds.add(1L);  // will replace with parsed deckIds
        gameService.dealCards(chosenDeckIds, 1, 2L, 1L);
        return new RedirectView("/game/play");
    }

//    For displaying gameplay. More to add.
    @GetMapping("/game/play")
    public String playGame(Model model){
        List<Card> playerHand = gameService.showPlayerHand(2L);
        model.addAttribute("playerHand", playerHand);
        return "newgame";
    }

//    For the end of the game. Currently just being used to reset the hand.
    @GetMapping("/game/reset")
    public RedirectView clearHand() {
        gameService.clearHand(2L);  // will replace with parsed currentUserId
        gameService.clearHand(1L);
        return new RedirectView("/game/start");
    }
}

