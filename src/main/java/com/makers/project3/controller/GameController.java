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
    public RedirectView setupGame(@RequestParam List<String> chosenDeckIds, @RequestParam Integer pointsToWin, Model model){
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
            return new RedirectView("/game/new");
        }
        gameService.dealCards(chosenDecks, pointsToWin, playerOne, playerTwo);
        model.addAttribute("currentPlayerId", user);
        return new RedirectView("/game/play");
    }

//    For displaying gameplay. More to add.
    @GetMapping("/game/play")
    public String playGame(Model model){
        // Attempt to retrieve currentPlayerId from the model (passed from previous method, e.g., setupGame POST)
        Long currentPlayerId = (Long) model.getAttribute("currentPlayerId");

        // --- FIX 4: Provide a fallback for currentPlayerId if accessed directly or not set ---
        // This is crucial if the user directly navigates to /game/play without going through /game/new
        if (currentPlayerId == null) {
            // IMPORTANT: Replace '1L' with actual user identification logic (e.g., from Spring Security, session)
            currentPlayerId = 1L; // Default to Player 1 if no ID is found (adjust as needed)
            System.out.println("Warning: currentPlayerId was null on GET /game/play, defaulting to " + currentPlayerId + " for display.");
        }

        // --- FIX 5: Ensure currentPlayerId IS ALWAYS IN THE MODEL for the HTML view ---
        model.addAttribute("currentPlayerId", currentPlayerId);

        List<Card> playerHand = gameService.showPlayerHand(2L);
        model.addAttribute("playerHand", playerHand);
        return "newgame";
    }


//    For the end of the game. Currently just being used to reset the hand.
    @GetMapping("/game/reset")
    public RedirectView clearHand() {
        gameService.clearHand(2L);  // will replace with parsed currentUserId
        gameService.clearHand(1L);

        return new RedirectView("/game/new");
    }

// Allows user to select a card from their hand and updates the hand
    @PostMapping("/game/play")
    public String selectCard(@RequestParam("playerId") Long playerId, @RequestParam("cardId") Long cardId, Model model) {
        Card selectedCard = gameService.pickCardFromHand(playerId, cardId);

        model.addAttribute("currentPlayerId", playerId);

        // Checks if the both player and card ids are valid, otherwise shoes the hand with erorr
        if (playerId == null || cardId == null) {
            model.addAttribute("errorMessage", "Player ID and Card ID are required to play a card.");
            List<Card> currentHand = gameService.showPlayerHand(playerId != null ? playerId : 1L);
            model.addAttribute("playerHand", currentHand);
            return "playgame";
        }

        try {
            Card playedCard = gameService.pickCardFromHand(playerId, cardId);

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

        List<Card> updatedPlayerHand = gameService.showPlayerHand(playerId);
        model.addAttribute("playerHand", updatedPlayerHand);

        return "playgame";
    }
}

