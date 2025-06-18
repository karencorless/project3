package com.makers.project3.controller;

import com.makers.project3.Service.GameService;
import com.makers.project3.Service.UserService;
import com.makers.project3.model.Card;
import com.makers.project3.model.Deck;
import com.makers.project3.model.Game;
import com.makers.project3.repository.DeckRepository;
import com.makers.project3.repository.GameRepository;
import com.makers.project3.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.util.*;

@Controller
public class GameController {

    @Autowired
    GameService gameService;
    @Autowired
    UserService userService;
    @Autowired
    PlayerRepository playerRepository;
    @Autowired
    DeckRepository deckRepository;
    @Autowired
    GameRepository gameRepository;


    // show game setup page
    @GetMapping("/game/new")
    public String newGame(Model model) {
        Iterable<Deck> decks = deckRepository.findAll();
        model.addAttribute("decks", decks);
        return "newgame";
    }


    //   To start a new game. Will take input for chosen decks and points to win
    @PostMapping("/game/new")
    public RedirectView setupGame(@RequestParam List<String> chosenDeckIds, @RequestParam Integer pointsToWin){
        Game game = gameService.newGame(pointsToWin, chosenDeckIds);
        return new RedirectView("/game/play/" + game.getId());
    }


//    For displaying gameplay.
    @GetMapping("/game/play/{gameId}")
    public String playGame(Model model, @PathVariable Long gameId){
        Game currentGame = gameRepository.findById(gameId).orElse(null);
        if (currentGame == null){  // needs error mapping
            return "errorpage";
        }
        Long userId = userService.getCurrentUserId();
        Long userPlayerId = playerRepository.findByPlayerUserId(userId).getId();
        List<Card> userHand;
        userHand = gameService.showPlayerHand(userPlayerId);
        gameService.winPoint(currentGame.getPlayerOneId()); // temporary for testing
        model.addAttribute("gameId", gameId);
        model.addAttribute("userHand", userHand);
        return "playgame";
    }


//    For the end of the game. Returns bool of if the current user is the winner, and clears the game from the db.
    @GetMapping("/game/reset/{gameId}")
    public RedirectView clearGame(@PathVariable Long gameId) {

        boolean userWinner = gameService.endGame(gameId);
        gameService.deleteGame(gameId);
        return new RedirectView("/game/new");
    }

}

