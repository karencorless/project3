package com.makers.project3.controller;

import com.makers.project3.Service.GameService;
import com.makers.project3.Service.UserService;
import com.makers.project3.model.Card;
import com.makers.project3.model.Deck;
import com.makers.project3.model.Game;
import com.makers.project3.model.User;
import com.makers.project3.repository.DeckRepository;
import com.makers.project3.repository.GameRepository;
import com.makers.project3.repository.PlayerCardRepository;
import com.makers.project3.repository.PlayerRepository;
import com.makers.project3.repository.UserRepository;
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
    @Autowired
    UserRepository userRepository;

//          GET -- New game setup page
    @GetMapping("/game/new")
    public String newGame(Model model) {
        Iterable<Deck> decks = deckRepository.findAll();
        User currentUser = (userRepository.findById(userService.getCurrentUserId())).orElse(null);
        model.addAttribute("currentUser", currentUser);
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
        User currentUser = (userRepository.findById(userService.getCurrentUserId())).orElse(null);
        Game currentGame = gameService.findCurrentUserGame();
        Long gameId = currentGame.getId();

        if (currentGame == null){  // needs error mapping
            return "errorpage";
        }
        Long userId = userService.getCurrentUserId();
        Long currentPlayerId = playerRepository.findByPlayerUserId(userId).getId();
        Long cpuId = gameService.getOppIdForGame(currentGame, currentPlayerId);

        if (currentPlayerId == null) {
            currentPlayerId = 1L; // Default to Player 1 if no ID is found (adjust as needed)
            System.out.println("Warning: currentPlayerId was null on GET /game/play, defaulting to " + currentPlayerId + " for display.");
        }

        int player1Score = gameService.getScore(currentPlayerId);
        int cpuScore = gameService.getScore(cpuId);

        List<Card> playerHand = gameService.showPlayerHand(currentPlayerId);
        model.addAttribute("currentPlayerId", currentPlayerId);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("playerHand", playerHand);
        model.addAttribute("gameId", gameId);
        model.addAttribute("player1Score", player1Score);
        model.addAttribute("cpuScore", cpuScore);
        model.addAttribute("cpuId", cpuId);

        return "playgame";
    }


    // Allows user to select a card from their hand and updates the hand
    @PostMapping("/game/play")
    public String selectCard(@RequestParam("currentPlayerId") Long currentPlayerId, @RequestParam("cardId") Long cardId, @RequestParam("gameId") Long gameId, Model model) {
        User currentUser = (userRepository.findById(userService.getCurrentUserId())).orElse(null);

        // Checks if the both player and card ids are valid, otherwise shoes the hand with error
        if (currentPlayerId == null || cardId == null) {
            model.addAttribute("errorMessage", "Player ID and Card ID are required to play a card.");
            List<Card> currentHand = gameService.showPlayerHand(currentPlayerId != null ? currentPlayerId : 1L);
            model.addAttribute("playerHand", currentHand);
            return "playgame";
        }

        Card playedCard = gameService.pickCardFromHand(currentPlayerId, cardId);

        try {

            if (playedCard != null) {
                model.addAttribute("playedCard", playedCard);
                model.addAttribute("successMessage", "You played: " + playedCard.getName() + "!");
            } else {
                model.addAttribute("errorMessage", "Card isn't available in your hand or could not be played.");
            }
        } catch (Exception e) {
            System.err.println("An unexpected error occurred while playing card: " + e.getMessage());
            e.printStackTrace(); // Print full stack trace for detailed debugging bc more info good
            model.addAttribute("errorMessage", "An unexpected error occurred: " + e.getMessage());
        }

        Game game = gameService.getGameById(gameId);
        if (game == null) {
            model.addAttribute("errorMessage", "Game not found.");
            return "playgame";
        }

        List<Card> playerHand = gameService.showPlayerHand(currentPlayerId);
        Long cpuId = gameService.getOppIdForGame(game, currentPlayerId);

        int player1Score = gameService.getScore(currentPlayerId);
        int cpuScore = gameService.getScore(cpuId);

        model.addAttribute("playerHand", playerHand);
        model.addAttribute("gameId", gameId);
        model.addAttribute("player1Score", player1Score);
        model.addAttribute("cpuScore", cpuScore);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("cpuId", cpuId);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("currentPlayerId", currentPlayerId);
        model.addAttribute("gameId", gameId);

        return "playgame";
    }

    @PostMapping("/game/play/player-1-stat-select")
    public String selectP1Stat(@RequestParam("cardId") Long cardId, @RequestParam("chosenStat") String chosenStat,
                                   @RequestParam("gameId") Long gameId, Model model) {

        // Get current user object
        User currentUser = (userRepository.findById(userService.getCurrentUserId())).orElse(null);

        Long userId = userService.getCurrentUserId();
        Long currentPlayerId = playerRepository.findByPlayerUserId(userId).getId();

        // Checks card is in active hand
        Card playedCard = gameService.pickCardFromHand(currentPlayerId, cardId);
        if (playedCard == null) {
            model.addAttribute("errorMessage", "Selected card not found.");
            return "playgame";
        }

        // Gets card id
        // Checks card is in active hand
        Long playedCardId = playedCard.getId();

        // Gets value of chosen stat
        int statValue = gameService.getStatValue(chosenStat, playedCard);

        //Get current game object
        Game game = gameService.getGameById(gameId);
        if (game == null) {
            model.addAttribute("errorMessage", "Game not found.");
            return "playgame";
        }

        // Get Cpu id
        Long cpuId = gameService.getOppIdForGame(game, currentPlayerId);
        if (cpuId == null) {
            model.addAttribute("errorMessage", "Opponent not found.");
            return "playgame";
        }

        // Automatically generate Cpu's chosen card
        Card cpuCard = gameService.getCpuCardByHighestStat(cpuId, chosenStat);
        if (cpuCard == null) {
            model.addAttribute("errorMessage", "Opponent has no cards.");
            return "playgame";
        }

        // Get cpu's stat value
        int cpuStatValue = gameService.getStatValue(chosenStat, cpuCard);

        // Compare Player 1 and CPU's stat values:
        Boolean compareStatsResult = gameService.compareStats(statValue, cpuStatValue);

        // Apply points:
        if (compareStatsResult == null) {
            System.out.println("Stat values are equal, round result is a draw. No points awarded.");
        }
        else if (compareStatsResult) {
            gameService.winPoint(currentPlayerId);
        }
        else {
            gameService.winPoint(cpuId);
        }

        // Access both player's points:
        int player1Score = gameService.getScore(currentPlayerId);
        int cpuScore = gameService.getScore(cpuId);

        System.out.println("Current player Id: " + currentPlayerId);

        System.out.println("Incrementing score for playerId: " + currentPlayerId);
        gameService.winPoint(currentPlayerId);

        // Return updated hand
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
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("player1Score", player1Score);
        model.addAttribute("cpuScore", cpuScore);
        model.addAttribute("cardId", playedCardId);
        model.addAttribute("cpuId", cpuId);

        // Game logs for easier reading and debugs
        System.out.println("CPU card picked: " + cpuCard.getName());
        System.out.println("CPU chosen stat: " + chosenStat);
        System.out.println("Entered selectPlayerStat with gameId=" + gameId + ", currentPlayerId=" + currentPlayerId + ", cardId=" + cardId + ", chosenStat=" + chosenStat);
        System.out.println("Played card: " + (playedCard != null ? playedCard.getName() : "null"));
        System.out.println("CPU card: " + (cpuCard != null ? cpuCard.getName() : "null"));
        System.out.println("Sending to playgame: cpuCard=" + cpuCard.getName() + ", stat=" + chosenStat);
        System.out.println("Player 1 Score: " + player1Score);
        System.out.println("CPU Score: " + cpuScore);

        return "playgame";
    }


    @PostMapping("/game/play/player-2-stat-select")
    public String selectP2Stat(@RequestParam("cardId") Long cardId, @RequestParam("cpuId") Long cpuId, @RequestParam(value = "chosenStat", required = false) String chosenStat, Model model) {

        // Get current user object, user id and current player id
        User currentUser = (userRepository.findById(userService.getCurrentUserId())).orElse(null);
        Long userId = userService.getCurrentUserId();
        Long currentPlayerId = playerRepository.findByPlayerUserId(userId).getId();

        if (chosenStat == null) {
            chosenStat = "strength"; // default stat if none chosen yet
        }

        // Get played card for current player
        Card playedCard = gameService.pickCardFromHand(currentPlayerId, cardId);
        if (playedCard == null) {
            // error handlin
            model.addAttribute("error", "Invalid card selected.");
            return "error"; // or your error page
        }

        // Player's stat value on chosen stat
        int playerStatValue = gameService.getStatValue(chosenStat, playedCard);

        // CPU's best card for that stat
        Card cpuPlayedCard = gameService.getCpuCardByHighestStat(cpuId, chosenStat);
        int cpuStatValue = 0;
        if (cpuPlayedCard != null) {
            cpuStatValue = gameService.getStatValue(chosenStat, cpuPlayedCard);
        }

        // Player hand
        List<Card> playerHand = gameService.showPlayerHand(currentPlayerId);

        // Scores
        int player1Score = gameService.getScore(currentPlayerId);
        int cpuScore = gameService.getScore(cpuId);

        // Add to model
        model.addAttribute("player1Score", player1Score);
        model.addAttribute("cpuScore", cpuScore);
        model.addAttribute("playedCard", playedCard);
        model.addAttribute("chosenStat", chosenStat);
        model.addAttribute("statValue", playerStatValue);
        model.addAttribute("cpuPlayedCard", cpuPlayedCard);
        model.addAttribute("cpuChosenStat", chosenStat);
        model.addAttribute("cpuStatValue", cpuStatValue);
        model.addAttribute("playerHand", playerHand);
        model.addAttribute("currentUser", currentUser);

        Game game = gameService.findCurrentUserGame();
        Long gameId = game.getId();
        model.addAttribute("gameId", gameId);
        model.addAttribute("currentPlayerId", currentPlayerId);
        model.addAttribute("cardId", cardId);
        model.addAttribute("cpuId", cpuId);

        return "playgame"; // your Thymeleaf template
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

