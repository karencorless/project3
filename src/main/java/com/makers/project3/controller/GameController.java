package com.makers.project3.controller;

import com.makers.project3.Service.GameService;
import com.makers.project3.Service.UserService;
import com.makers.project3.model.Card;
import com.makers.project3.model.Deck;
import com.makers.project3.model.Game;
import com.makers.project3.repository.DeckRepository;
import com.makers.project3.repository.GameRepository;
import com.makers.project3.repository.PlayerCardRepository;
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
    @Autowired
    PlayerCardRepository playerCardRepository;


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
        Game game = gameService.newGame(pointsToWin, chosenDeckIds);
        return new RedirectView("/game/play/" + game.getId());
    }


//          For displaying gameplay.
@GetMapping("/game/play/{gameId}")
    public String playGame(Model model, @PathVariable Long gameId){
        Game currentGame = gameRepository.findById(gameId).orElse(null);
        if (currentGame == null){  // needs error mapping
            return "errorpage";
        }
        Long userId = userService.getCurrentUserId();
        Long currentPlayerId = playerRepository.findByPlayerUserId(userId).getId();

        if (currentPlayerId == null) {
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
    @PostMapping("/game/play/{gameId}")
    public String selectCard(@RequestParam("currentPlayerId") Long currentPlayerId, @RequestParam("cardId") Long cardId, @PathVariable Long gameId, Model model) {

        model.addAttribute("currentPlayerId", currentPlayerId);

        // Checks if the both player and card ids are valid, otherwise shoes the hand with erorr
        if (currentPlayerId == null || cardId == null) {
            model.addAttribute("errorMessage", "Player ID and Card ID are required to play a card.");
            List<Card> currentHand = gameService.showPlayerHand(currentPlayerId != null ? currentPlayerId : 1L);
            model.addAttribute("playerHand", currentHand);
            return "playgame";
        }

        try {
            Card playedCard = gameService.pickCardFromHand(currentPlayerId, cardId);

            if (playedCard != null) {
                model.addAttribute("playedCard", playedCard);
                model.addAttribute("successMessage", "You played: " + playedCard.getName() + "!");
            } else {
                model.addAttribute("errorMessage", "Card isn't available in your hand or could not be played.");
            }
        } catch (Exception e) {
            System.err.println("An unexpected error occurred while playing card: " + e.getMessage());
            e.printStackTrace(); // Print full stack trace for detailed debugging
            model.addAttribute("errorMessage", "An unexpected error occurred: " + e.getMessage());
        }

        Game game = gameService.getGameById(gameId);
        if (game == null) {
            model.addAttribute("errorMessage", "Game not found.");
            return "playgame";
        }

        List<Card> playerHand = gameService.showPlayerHand(currentPlayerId);
        model.addAttribute("playerHand", playerHand);
        model.addAttribute("gameId", gameId);

        return "playgame";
    }

    @PostMapping("/game/play/{gameId}/select-stat")
    public String selectPlayerStat(@PathVariable Long gameId, @RequestParam("currentPlayerId") Long currentPlayerId,
                                   @RequestParam("cardId") Long cardId, @RequestParam("chosenStat") String chosenStat, Model model) {

        Card playedCard = gameService.pickCardFromHand(currentPlayerId, cardId);
        if (playedCard == null) {
            model.addAttribute("errorMessage", "Selected card not found.");
            return "playgame";
        }

        int statValue = gameService.getStatValue(chosenStat, playedCard);

        Game game = gameService.getGameById(gameId);
        if (game == null) {
            model.addAttribute("errorMessage", "Game not found.");
            return "playgame";
        }

        Long cpuId = gameService.getOppIdForGame(game, currentPlayerId);
        if (cpuId == null) {
            model.addAttribute("errorMessage", "Opponent not found.");
            return "playgame";
        }

        Card cpuCard = gameService.getCpuCardByHighestStat(cpuId, chosenStat);
        if (cpuCard == null) {
            model.addAttribute("errorMessage", "Opponent has no cards.");
            return "playgame";
        }

        int cpuStatValue = gameService.getStatValue(chosenStat, cpuCard);

        List<Card> updatedPlayerHand = gameService.showPlayerHand(currentPlayerId);
        model.addAttribute("playerHand", updatedPlayerHand);

        model.addAttribute("chosenStat", chosenStat);
        model.addAttribute("statValue", statValue);
        model.addAttribute("playedCard", playedCard);
        model.addAttribute("cpuPlayedCard", cpuCard);
        model.addAttribute("cpuChosenStat", chosenStat);
        model.addAttribute("cpuStatValue", cpuStatValue);
        model.addAttribute("currentPlayerId", currentPlayerId);
        model.addAttribute("gameId", gameId);

        System.out.println("CPU card picked: " + cpuCard.getName());
        System.out.println("CPU chosen stat: " + chosenStat);
        System.out.println("Entered selectPlayerStat with gameId=" + gameId + ", currentPlayerId=" + currentPlayerId + ", cardId=" + cardId + ", chosenStat=" + chosenStat);
        System.out.println("Played card: " + (playedCard != null ? playedCard.getName() : "null"));
        System.out.println("CPU card: " + (cpuCard != null ? cpuCard.getName() : "null"));
        System.out.println("Sending to playgame: cpuCard=" + cpuCard.getName() + ", stat=" + chosenStat);


        return "playgame";
    }


    @PostMapping("/game/play/{gameId}/cpu-turn")
    public String selectCpuTurn(@RequestParam("currentPlayerId") Long currentPlayerId, @RequestParam("cardId") Long cardId, @RequestParam("cpuId") Long cpuId, Model model) {

        // ----- CPU ID REQUIRED, PARSE THAT IN THE FORM ------

        int highestStatValue = gameService.getHighestStatValue(cpuId);

        Card highestStatValueCard = gameService.getHighestStatValueCard(cpuId);

        String statName = gameService.getNameOfMaxStatOnCard(highestStatValueCard);

        // gets highest stat card CHARACTER NAME
        String cardCharacterName = highestStatValueCard.getName();

        model.addAttribute("cpuChosenCard", cardCharacterName);
        model.addAttribute("cpuChosenStat", statName);

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

