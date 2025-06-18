package com.makers.project3.controller;

import com.makers.project3.Service.GameService;
import com.makers.project3.model.Card;
import com.makers.project3.model.Deck;
import com.makers.project3.repository.DeckRepository;
import com.makers.project3.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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


    // show game setup page
    @GetMapping("/game/new")
    public String newGame(Model model) {
        Iterable<Deck> decks = deckRepository.findAll();
        model.addAttribute("decks", decks);
        return "newgame";
    }

    //   To start a new game. Will take input for chosen decks and points to win
    @PostMapping("/game/new")
    public RedirectView setupGame(@ModelAttribute Deck deck, @RequestParam List<String> chosenDeckIds, @RequestParam Integer pointsToWin){
        List<Long> chosenDecks = new ArrayList<>();
//                chosenDeckIds.forEach(Long.parseLong(String id));
        for (String deckId : chosenDeckIds) {
            Long id = Long.parseLong(deckId);
            chosenDecks.add(id);
        }
        //  user parse player 1 and player 2 user Id's
        Long playerOne;
        Long playerTwo;
        Long user = 1L;
        Long p2 = 2L;
        // decide who is playerOne for this game
        if (gameService.coinFlip()) {
            playerOne = user;
            playerTwo = p2;
        } else {
            playerOne = p2;
            playerTwo = user;
        }

        // need to create new game object

        if (chosenDecks == null || chosenDeckIds.isEmpty()){
            return new RedirectView("/game/start");
        }
        gameService.dealCards(chosenDecks, pointsToWin, playerOne, playerTwo);
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

    //    For displaying gameplay. More to add.
    @PostMapping("/game/play")
    public String selectCard(@RequestParam Long playerId, @RequestParam Long cardId, Model model){

        return "newgame";
    }
}

