package com.makers.project3.controller;

import com.makers.project3.Service.GameService;
import com.makers.project3.Service.UserService;
import com.makers.project3.model.Card;
import com.makers.project3.model.Deck;
import com.makers.project3.model.Game;
import com.makers.project3.repository.DeckRepository;
import com.makers.project3.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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


//          GET -- New game setup page
    @GetMapping("/game/new")
    public String newGame(Model model) {
        Iterable<Deck> decks = deckRepository.findAll();
        model.addAttribute("decks", decks);
        return "newgame";
    }


//       POST -- Initialize a new game. Takes input for chosen decks and points to win
    @PostMapping("/game/new")
    public RedirectView setupGame(@RequestParam List<String> chosenDeckIds, @RequestParam Integer pointsToWin){
        gameService.cleanUp();
        Game game = gameService.newGame(pointsToWin, chosenDeckIds);
        return new RedirectView("/game/play");
    }


//          GET -- Display gameplay.
    @GetMapping("/game/play")
    public String playGame(Model model){
        Game currentGame = gameService.findCurrentUserGame();
        Long gameId = currentGame.getId();

        if (currentGame == null){  // needs error mapping
            return "errorpage";
        }
        Long userId = userService.getCurrentUserId();
        Long currentPlayerId = playerRepository.findByPlayerUserId(userId).getId();

        // --- FIX 4: Provide a fallback for currentPlayerId if accessed directly or not set ---
        // This is crucial if the user directly navigates to /game/play without going through /game/new
        if (currentPlayerId == null) {
            // IMPORTANT: Replace '1L' with actual user identification logic (e.g., from Spring Security, session)
            currentPlayerId = 1L; // Default to Player 1 if no ID is found (adjust as needed)
            System.out.println("Warning: currentPlayerId was null on GET /game/play, defaulting to " + currentPlayerId + " for display.");
        }

        List<Card> playerHand = gameService.showPlayerHand(currentPlayerId);
        model.addAttribute("currentPlayerId", currentPlayerId);
        model.addAttribute("playerHand", playerHand);
        model.addAttribute("gameId", gameId);
        return "playgame";
    }


    // Allows user to select a card from their hand and updates the hand
    @PostMapping("/game/play")
    public String selectCard(@RequestParam("currentPlayerId") Long currentPlayerId, @RequestParam("cardId") Long cardId, @RequestParam("gameId") Long gameId, Model model) {
        model.addAttribute("currentPlayerId", currentPlayerId);
        model.addAttribute("gameId", gameId);

        // Checks if the both player and card ids are valid, otherwise shoes the hand with error
        if (currentPlayerId == null || cardId == null) {
            model.addAttribute("errorMessage", "Player ID and Card ID are required to play a card.");
            List<Card> currentHand = gameService.showPlayerHand(currentPlayerId != null ? currentPlayerId : 1L);
            model.addAttribute("playerHand", currentHand);
            return "playgame";
        }

        try {
            Card playedCard = gameService.pickCardFromHand(currentPlayerId, cardId);

            if (playedCard != null) {
                model.addAttribute("successMessage", "You have selected: " + playedCard.getName() + "!");
            } else {
                model.addAttribute("errorMessage", "Card isn't available in your hand or could not be played.");
            }
        } catch (Exception e) {
            System.err.println("An unexpected error occurred while playing card: " + e.getMessage());
            e.printStackTrace(); // Print full stack trace for detailed debugging
            model.addAttribute("errorMessage", "An unexpected error occurred: " + e.getMessage());
        }

        List<Card> playerHand = gameService.showPlayerHand(currentPlayerId);
        model.addAttribute("playerHand", playerHand);
        return "playgame";
    }


//    GET -- End of the game. Returns bool of if the current user is the winner, and clears the game from the db.
    @GetMapping("/game/reset")
    public RedirectView clearGame() {
        Game game = gameService.findCurrentUserGame();
        boolean userWinner = gameService.endGame(game.getId());
        gameService.cleanUp();
        return new RedirectView("/game/new");
    }
}

