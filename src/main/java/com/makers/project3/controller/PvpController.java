package com.makers.project3.controller;

import com.makers.project3.Service.*;
import com.makers.project3.exception.NoSuchEntityExistsException;
import com.makers.project3.model.*;
import com.makers.project3.repository.DeckRepository;
import com.makers.project3.repository.PlayerRepository;
import com.makers.project3.repository.PvpGameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
public class PvpController {

    @Autowired
    AuthenticatedUserService authenticatedUserService;
    @Autowired
    PvpGameRepository pvpGameRepository;
    @Autowired
    LeaderboardService leaderboardService;
    @Autowired
    DeckRepository deckRepository;
    @Autowired
    PlayerRepository playerRepository;
    @Autowired
    UserService userService;
    @Autowired
    PvpService pvpService;
    @Autowired
    GameService gameService;


    @GetMapping("/playmodes")
    public ModelAndView choosePlayMode() {
        ModelAndView modelAndView = new ModelAndView("playmodes");
        User currentUser = authenticatedUserService.getAuthenticatedUser();
        modelAndView.addObject("currentUser", currentUser);
        return modelAndView;
    }

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

        mav.addObject("mode", "pvp");
        return mav;
    }

//      View set up page for a new game
    @GetMapping("/pvp/new")
    public String beginNewPvpGame(Model model){
        Iterable<Deck> decks = deckRepository.findAll();
        User currentUser = authenticatedUserService.getAuthenticatedUser();
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("decks", decks);
        model.addAttribute("mode", "pvp");
        return "newgame";
    }


//       Initialize a new game. Takes input for chosen decks and points to win
    @PostMapping("/pvp/new")
    public RedirectView setupGame(@RequestParam List<String> chosenDeckIds, @RequestParam Integer pointsToWin){
//        pvpService.cleanUp();
        PvpGame game = pvpService.newPvpGame(pointsToWin, chosenDeckIds);
        return new RedirectView("/pvp/waiting");
    }


//    MAY NEED TO CHECK/UPDATE WHEN WE FIGURE OUT HOW PLAYERS JOIN IN, but works so far.
    @PostMapping("/pvp/join")
    public String joinPvpGame(@RequestParam Long pvpGameId, Model model) {
        User currentUser = authenticatedUserService.getAuthenticatedUser();
        Long playerTwoId = pvpService.addPlayerTwoToPvpGame(pvpGameId, currentUser.getId());
        pvpService.dealPvpPlayerCards(playerTwoId, pvpGameId);
        PvpGame currentPvpGame = pvpGameRepository.findById(pvpGameId).orElse(null);
        if (currentPvpGame == null){
            throw new NoSuchEntityExistsException("PvP Game");
        }

        Player opponent = playerRepository.findById(currentPvpGame.getPlayerOneId()).orElse(null);
        Player currentPlayer = playerRepository.findById(currentPvpGame.getPlayerTwoId()).orElse(null);
        if (opponent == null || currentPlayer == null) {
            throw new NoSuchEntityExistsException("Player");
        }
        List<Card> playerHand = new ArrayList<>();
        boolean roundComplete = false;

        playerHand = gameService.showPlayerHand(currentPlayer.getId());

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("playerHand", playerHand);
        model.addAttribute("currentPlayer", currentPlayer);
        model.addAttribute("opponent", opponent);
        model.addAttribute("game", currentPvpGame);
        model.addAttribute("roundComplete", roundComplete);
        model.addAttribute("p1IsAttackingNextRound", true);
        model.addAttribute("currentAttacker", "P1");
        model.addAttribute("gameOver", false);
        model.addAttribute("mode", "pvp");
        return "play";
    }


    @GetMapping("pvp/waiting")
    public String  continuePvpGame(Model model){
        User currentUser = authenticatedUserService.getAuthenticatedUser();
        Iterable<Deck> decks = deckRepository.findAll();
        List<User> playersMostWins = leaderboardService.getTopPlayersByWins(3);
        Iterable<PvpGame> pvpGames = pvpGameRepository.findAll();
        model.addAttribute("decks", decks);
        model.addAttribute("playersMostWins", playersMostWins);
        model.addAttribute(pvpGames);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("mode", "pvp");
        return "waiting";
        }


    @GetMapping("/pvp/play")
    public String playPvpGame(Model model) {
//        pvpService.prepModel(model);
//        Player currentPlayer = (Player) model.getAttribute("currentPlayer");
////        User currentUser = authenticatedUserService.getAuthenticatedUser();
////        PvpGame currentPvpGame = pvpService.findCurrentUserPvpGame();
////
////        Player playerOne = playerRepository.findById(currentPvpGame.getPlayerOneId()).orElse(null);
////        Player playerTwo = playerRepository.findById(currentPvpGame.getPlayerTwoId()).orElse(null);
//
////        if (playerOne == null || playerTwo == null) {
////            throw new NoSuchEntityExistsException("Player");
////        }
//
////        Player currentPlayer = null;
////        Player opponent = null;
//
////        boolean isPlayerOne = (Objects.equals(currentUser.getId(), playerOne.getPlayerUserId()));
////        boolean isPlayerTwo = (Objects.equals(currentUser.getId(), playerTwo.getPlayerUserId()));
////
////
////        if (isPlayerOne) {
////            currentPlayer = playerOne;
////            opponent = playerTwo;
////        } else if (isPlayerTwo) {
////            currentPlayer = playerTwo;
////            opponent = playerOne;
////        } else{
////            throw new RuntimeException("Current user not found in Game.");
////        }
//        List<Card> playerHand = gameService.showPlayerHand(currentPlayer.getId());
//        boolean roundComplete = false;
//
////        model.addAttribute("currentUser", currentUser);
//        model.addAttribute("playerHand", playerHand);
////        model.addAttribute("currentPlayer", currentPlayer);
////        model.addAttribute("opponent", opponent);
////        model.addAttribute("game", currentPvpGame);
//        model.addAttribute("roundComplete", roundComplete);
//        model.addAttribute("p1IsAttackingNextRound", true);
//        model.addAttribute("currentAttacker", "P1");
//        model.addAttribute("gameOver", false);
////        model.addAttribute("mode", "pvp");

        User currentUser = authenticatedUserService.getAuthenticatedUser();
        PvpGame currentPvpGame = pvpService.findCurrentUserPvpGame();
        Player playerOne = playerRepository.findById(currentPvpGame.getPlayerOneId()).orElse(null);
        Player playerTwo = playerRepository.findById(currentPvpGame.getPlayerTwoId()).orElse(null);
        List<Card> playerHand = new ArrayList<>();
        if (playerOne == null || playerTwo == null) {
            throw new NoSuchEntityExistsException("Player");
        }
        Player currentPlayer = null;
        Player opponent = null;
        boolean roundComplete = false;
        boolean isPlayerOne = (Objects.equals(currentUser.getId(), playerOne.getPlayerUserId()));
        boolean isPlayerTwo = (Objects.equals(currentUser.getId(), playerTwo.getPlayerUserId()));

        if (isPlayerOne) {
            currentPlayer = playerOne;
            opponent = playerTwo;
        } else if (isPlayerTwo) {
            currentPlayer = playerTwo;
            opponent = playerOne;
        } else{
            throw new RuntimeException("Current user not found in Game.");
        }

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("playerHand", playerHand);
        model.addAttribute("currentPlayer", currentPlayer);
        model.addAttribute("opponent", opponent);
        model.addAttribute("game", currentPvpGame);
        model.addAttribute("roundComplete", roundComplete);
        model.addAttribute("p1IsAttackingNextRound", true);
        model.addAttribute("currentAttacker", "P1");
        model.addAttribute("gameOver", false);
        model.addAttribute("mode", "pvp");
        return "play";
    }

    // P1 SUBMITTING THEIR SELECTED CARD.
    @PostMapping("/pvp/play/p1-attack-1")
    public String p1PvpSelectCard(@RequestParam("cardId") Long cardId, Model model) {
//        pvpService.prepModel(model);
//        Player currentPlayer = (Player) model.getAttribute("currentPlayer");
            User currentUser = authenticatedUserService.getAuthenticatedUser();
            PvpGame currentPvpGame = pvpService.findCurrentUserPvpGame();

            Player playerOne = playerRepository.findById(currentPvpGame.getPlayerOneId()).orElse(null);
            Player playerTwo = playerRepository.findById(currentPvpGame.getPlayerTwoId()).orElse(null);
            if (playerOne == null || playerTwo == null) {
                throw new NoSuchEntityExistsException("Player");
            }

            Player currentPlayer = null;
            Player opponent = null;

            boolean isPlayerOne = (Objects.equals(currentUser.getId(), playerOne.getPlayerUserId()));
            boolean isPlayerTwo = (Objects.equals(currentUser.getId(), playerTwo.getPlayerUserId()));

            if (isPlayerOne) {
                currentPlayer = playerOne;
                opponent = playerTwo;
            } else if (isPlayerTwo) {
                currentPlayer = playerTwo;
                opponent = playerOne;
            } else{
                throw new RuntimeException("Current user not found in Game.");
            }


        if (cardId == null) {
            model.addAttribute("errorMessage", "Player ID and Card ID are required to play a card.");
        }

        // Selects P1's card from their hand using P1 and Card Id, AND REMOVES CARD FROM HAND, discardedbool=true.
        Card playedCard = pvpService.pickCardFromHand(currentPlayer, cardId);

        // Displays the selected
        if (playedCard != null) {
            model.addAttribute("playedCard", playedCard);
        }
        else {
            model.addAttribute("errorMessage", "Card isn't available in your hand or could not be played.");
        }

        // Show player's updated hand
        List<Card> playerHand = gameService.showPlayerHand(currentPlayer.getId());
        boolean roundComplete = false;

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("currentPlayer", currentPlayer);
        model.addAttribute("opponent", opponent);
        model.addAttribute("game", currentPvpGame);
        model.addAttribute("mode", "pvp");

        model.addAttribute("playerHand", playerHand);
        model.addAttribute("p1IsAttackingNextRound", true);
        model.addAttribute("currentAttacker", "P1");
        model.addAttribute("gameOver", false);
        model.addAttribute("roundComplete", roundComplete);
        return "playgame";
    }

//    // P1 SUBMITTING THEIR SELECTED STAT.
//    @PostMapping("/game/play/p1-attack-2")
//    public String selectP1Stat(@RequestParam("cardId") Long cardId, @RequestParam("chosenStat") String chosenStat,
//        Model model) {
//
//
//
//    }
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//    // P1 SUBMITTING THEIR SELECTED CARD.
//    @PostMapping("/game/play/p1-attack-1")
//    public String p1SelectCard(@RequestParam("cardId") Long cardId, Model model) {
//
//        // Player 1, CPU, and game info.
//        User currentUser = authenticatedUserService.getAuthenticatedUser();
//        Game currentGame = gameService.findCurrentUserGame();
//        Long userId = userService.getCurrentUserId();
////        Long gameId = currentGame.getId();
////        Long currentPlayerId = playerRepository.findByPlayerUserId(userId).getId();
////        Long cpuId = gameService.getOppIdForGame(currentGame, currentPlayerId);
//
//        // more safely finds Players by the Game id and determines who is user and who is cpu
//        Long currentPlayerId = null;
//        Long cpuId = null;
//        Player playerOne = playerRepository.findById(currentGame.getPlayerOneId()).orElse(null);
//        Player playerTwo = playerRepository.findById(currentGame.getPlayerTwoId()).orElse(null);
//        if (playerOne == null || playerTwo == null) {
//            throw new NoSuchEntityExistsException("Player");
//        }
//        List<Card> currentHand = new ArrayList<>();
//        boolean isPlayerOne = (Objects.equals(currentUser.getId(), playerOne.getPlayerUserId()));
//        boolean isPlayerTwo = (Objects.equals(currentUser.getId(), playerTwo.getPlayerUserId()));
//        if (isPlayerOne) {
//            currentPlayerId = playerOne.getId();
//            cpuId = playerTwo.getId();
//            currentHand = gameService.showPlayerHand(playerOne.getId());
//            model.addAttribute("playerHand", currentHand);
//        } else if (isPlayerTwo) {
//            currentPlayerId = playerTwo.getId();
//            cpuId = playerOne.getId();
//            currentHand = gameService.showPlayerHand(playerTwo.getId());
//            model.addAttribute("playerHand", currentHand);
//        } else {
//            throw new RuntimeException("Current user not found in game.");
//        }
//
//
//        if (currentPlayerId == null || cardId == null) {
//            model.addAttribute("errorMessage", "Player ID and Card ID are required to play a card.");
//            currentHand = gameService.showPlayerHand(currentPlayerId != null ? currentPlayerId : 1L);
//            model.addAttribute("playerHand", currentHand);
//            return "playgame";
//        }
//
//
//
//        // Sends P1's cardId out
//        model.addAttribute("cardId", cardId);
//
//        // Selects P1's card from their hand using P1 and Card Id, AND REMOVES CARD FROM HAND, discardedbool=true.
//        Card playedCard = gameService.pickCardFromHand(currentPlayerId, cardId);
//
//        // Displays the selected
//        if (playedCard != null) {
//            model.addAttribute("playedCard", playedCard);
//            model.addAttribute("successMessage", "You played: " + playedCard.getName() + "!");
//            model.addAttribute("customStatName", gameService.getCardCustomStatName(playedCard));
//        }
//        else {
//            model.addAttribute("errorMessage", "Card isn't available in your hand or could not be played.");
//        }
//
//        // Show player's updated hand
//        List<Card> playerHand = gameService.showPlayerHand(currentPlayerId);
//
//        // Show updated player scores.
//        int player1Score = gameService.getScore(currentPlayerId);
//        int cpuScore = gameService.getScore(cpuId);
//
//        model.addAttribute("currentUser", currentUser);
//        model.addAttribute("playerHand", playerHand);
//        model.addAttribute("player1Score", player1Score);
//        model.addAttribute("cpuScore", cpuScore);
//        model.addAttribute("roundComplete", false);
//        model.addAttribute("p1IsAttackingNextRound", true);
//        model.addAttribute("currentAttacker", "P1");
//        model.addAttribute("gameOver", false);
//        model.addAttribute("pointsToWin", currentGame.getPointsToWin());
//        model.addAttribute("mode", "game");
//        return "playgame";
//    }
//
//
//    // P1 SUBMITTING THEIR SELECTED STAT.
//    @PostMapping("/game/play/p1-attack-2")
//    public String selectP1Stat(@RequestParam("cardId") Long cardId, @RequestParam("chosenStat") String chosenStat,
//                               Model model) {
//        System.out.println("Received chosenStat: " + chosenStat);
//
//
//        //Get current game object
//        Boolean roundComplete = false;
//        User currentUser = authenticatedUserService.getAuthenticatedUser();
//        Game currentGame = gameService.findCurrentUserGame();
//        Long userId = userService.getCurrentUserId();
//
//        // more safely finds Players by the Game id and determines who is user and who is cpu
//        Long currentPlayerId = null;
//        Long cpuId = null;
//        Player playerOne = playerRepository.findById(currentGame.getPlayerOneId()).orElse(null);
//        Player playerTwo = playerRepository.findById(currentGame.getPlayerTwoId()).orElse(null);
//        if (playerOne == null || playerTwo == null) {
//            throw new NoSuchEntityExistsException("Player");
//        }
//        List<Card> playerHand = new ArrayList<>();
//        boolean isPlayerOne = (Objects.equals(currentUser.getId(), playerOne.getPlayerUserId()));
//        boolean isPlayerTwo = (Objects.equals(currentUser.getId(), playerTwo.getPlayerUserId()));
//        if (isPlayerOne) {
//            currentPlayerId = playerOne.getId();
//            cpuId = playerTwo.getId();
//            playerHand = gameService.showPlayerHand(playerOne.getId());
//        } else if (isPlayerTwo) {
//            currentPlayerId = playerTwo.getId();
//            cpuId = playerOne.getId();
//            playerHand = gameService.showPlayerHand(playerTwo.getId());
//        } else {
//            throw new RuntimeException("Current user not found in game.");
//        }
//
//        if (cpuId == null) {
//            model.addAttribute("errorMessage", "Opponent not found.");
//            return "playgame";
//        }
//
//        // Selects P1's card, AND REMOVES IT FROM THEIR HAND.
//        Card playedCard = gameService.pickCardFromHand(currentPlayerId, cardId);
//        if (playedCard == null) {
//            model.addAttribute("errorMessage", "Selected card not found.");
//            return "playgame";
//        }
//
//        // Gets P1's selected card ID and stat value
//        Long playedCardId = playedCard.getId();
//        int statValue = gameService.getStatValue(chosenStat, playedCard);
//
//        // Gets CPU's chosen card and stat value
//        Card cpuCard = gameService.getCpuCardWithP1sStat(cpuId, chosenStat);
//        if (cpuCard == null) {
//            model.addAttribute("errorMessage", "Opponent has no cards.");
//            return "playgame";
//        }
//        int cpuStatValue = gameService.getStatValue(chosenStat, cpuCard);
//
//        // Pass through the CPU's card to view
//        model.addAttribute("cpuPlayedCard", cpuCard);
//
//        // Removes CPU's card from their hand so they cant use it again
//        gameService.discardCpuChosenCardFromHand(cpuCard.getId(), cpuId);
//
//        // Compare Player 1 and CPU's stat values:
//        Boolean compareStatsResult = gameService.compareStats(statValue, cpuStatValue);
//
//        // Apply points:
//        if (compareStatsResult == null) {
//            System.out.println("Stat values are equal, round result is a draw. No points awarded.");
//        }
//        else if (compareStatsResult) {
//            gameService.winPoint(currentPlayerId);
//        }
//        else {
//            gameService.winPoint(cpuId);
//        }
//
//        // Access both player's points:
//        int player1Score = gameService.getScore(currentPlayerId);
//        int cpuScore = gameService.getScore(cpuId);
//
//        // Return updated hand
//        List<Card> updatedPlayerHand = gameService.showPlayerHand(currentPlayerId);
//
//        // Check if game has been won
//        int pointsToWinGame = currentGame.getPointsToWin();
//        if (pointsToWinGame == player1Score || pointsToWinGame == cpuScore) {
//            model.addAttribute("gameOver", true);
//            gameService.endGameTally(currentGame.getId());
//        }
//        else {
//            model.addAttribute("gameOver", false);
//        }
//
//        // Put in necessary values.
//        model.addAttribute("currentUser", currentUser);
//        model.addAttribute("playerHand", updatedPlayerHand);
//
//        // Player's round moves
//        model.addAttribute("playedCard", playedCard);
//        model.addAttribute("chosenStat", chosenStat);
//        model.addAttribute("statValue", statValue);
//
//        // CPU's round moves
//        model.addAttribute("cpuChosenStat", gameService.getCardCustomStatName(cpuCard));
//        model.addAttribute("cpuStatValue", cpuStatValue);
//        // Pass through the CPU's card to view
//        model.addAttribute("cpuPlayedCard", cpuCard);
//
//        // Display points and mark round complete
//        model.addAttribute("player1Score", player1Score);
//        model.addAttribute("cpuScore", cpuScore);
//        model.addAttribute("roundComplete", true);
//        model.addAttribute("p1IsAttackingNextRound", false);
//        model.addAttribute("currentAttacker", "P1");
//        model.addAttribute("cpuCustomStatName", gameService.getCardCustomStatName(cpuCard));
//        model.addAttribute("customStatName", gameService.getCardCustomStatName(playedCard));
//        model.addAttribute("pointsToWin", currentGame.getPointsToWin());
//
////        // Game logs for easier reading and debugs
////        System.out.println("CPU card picked: " + cpuCard.getName());
////        System.out.println("CPU chosen stat: " + chosenStat);
////        System.out.println("Entered selectPlayerStat with gameId=" + gameId + ", currentPlayerId=" + currentPlayerId + ", cardId=" + cardId + ", chosenStat=" + chosenStat);
////        System.out.println("Played card: " + (playedCard != null ? playedCard.getName() : "null"));
////        System.out.println("CPU card: " + (cpuCard != null ? cpuCard.getName() : "null"));
////        System.out.println("Sending to playgame: cpuCard=" + cpuCard.getName() + ", stat=" + chosenStat);
////        System.out.println("Player 1 Score: " + player1Score);
////        System.out.println("CPU Score: " + cpuScore);
////        System.out.println("Current player Id: " + currentPlayerId);
////        System.out.println("Incrementing score for playerId: " + currentPlayerId);
////        System.out.println("Game points to win: " + game.getPointsToWin());
//        model.addAttribute("mode", "game");
//        return "playgame";
//    }
//
//
//    @GetMapping("/game/play/p2-attack")
//    public String selectP2Stat(Model model) {
//
//        // THIS ROUND, CPU HAS CHOSEN THE STAT.
//        // PLAYER ONLY NEEDS TO PICK A CARD.
//
//        // Player 1, CPU, and game info.
//        Long currentPlayerId = null;
//        Long cpuId = null;
//        User currentUser = authenticatedUserService.getAuthenticatedUser();
//        Game currentGame = gameService.findCurrentUserGame();
//        Long userId = currentUser.getId();
//
//        // more safely finds Players by the Game id and determines who is user and who is cpu
//        Player playerOne = playerRepository.findById(currentGame.getPlayerOneId()).orElse(null);
//        Player playerTwo = playerRepository.findById(currentGame.getPlayerTwoId()).orElse(null);
//        if (playerOne == null || playerTwo == null) {
//            throw new NoSuchEntityExistsException("Player");
//        }
//        List<Card> playerHand = new ArrayList<>();
//        boolean isPlayerOne = (Objects.equals(currentUser.getId(), playerOne.getPlayerUserId()));
//        boolean isPlayerTwo = (Objects.equals(currentUser.getId(), playerTwo.getPlayerUserId()));
//        if (isPlayerOne) {
//            currentPlayerId = playerOne.getId();
//            cpuId = playerTwo.getId();
//            playerHand = gameService.showPlayerHand(playerOne.getId());
//        } else if (isPlayerTwo) {
//            currentPlayerId = playerTwo.getId();
//            cpuId = playerOne.getId();
//            playerHand = gameService.showPlayerHand(playerTwo.getId());
//        } else {
//            throw new RuntimeException("Current user not found in game.");
//        }
//
//        // Chooses the CPU's stat
//        Card cpuCard = gameService.getCpuCard(cpuId);
//        String cpuStat = gameService.getNameOfMaxStatOnCard(cpuCard);
//
//        // Displays player scores
//        int player1Score = gameService.getScore(currentPlayerId);
//        int cpuScore = gameService.getScore(cpuId);
//
//        // Add to model
//        model.addAttribute("currentUser", currentUser);
//        model.addAttribute("playerHand", playerHand);
//        model.addAttribute("player1Score", player1Score);
//        model.addAttribute("cpuScore", cpuScore);
//        model.addAttribute("roundComplete", false);
//        model.addAttribute("cpuStat", cpuStat);
//        model.addAttribute("playerStat", null);
//        model.addAttribute("p1IsAttackingNextRound", true);
//        model.addAttribute("currentAttacker", "P2");
//        model.addAttribute("gameOver", false);
//        model.addAttribute("cpuCustomStatName", gameService.getCardCustomStatName(cpuCard));
//        model.addAttribute("pointsToWin", currentGame.getPointsToWin());
//        model.addAttribute("cpuPlayedCard", cpuCard);
//        model.addAttribute("mode", "game");
//        return "playgame";
//    }
//
//    @PostMapping("/game/play/p1-defend")
//    public String selectP1Card(@RequestParam("cardId") Long cardId, Model model) {
//
//        // Player 1, CPU, and game info.
//        Long currentPlayerId = null;
//        Long cpuId = null;
//        User currentUser = authenticatedUserService.getAuthenticatedUser();
//        Game game = gameService.findCurrentUserGame();
//        Long userId = currentUser.getId();
//        Long gameId = game.getId();
//
//        // more safely finds Players by the Game id and determines who is user and who is cpu
//        Player playerOne = playerRepository.findById(game.getPlayerOneId()).orElse(null);
//        Player playerTwo = playerRepository.findById(game.getPlayerTwoId()).orElse(null);
//        if (playerOne == null || playerTwo == null) {
//            throw new NoSuchEntityExistsException("Player");
//        }
//        List<Card> playerHand = new ArrayList<>();
//        boolean isPlayerOne = (Objects.equals(currentUser.getId(), playerOne.getPlayerUserId()));
//        boolean isPlayerTwo = (Objects.equals(currentUser.getId(), playerTwo.getPlayerUserId()));
//        if (isPlayerOne) {
//            currentPlayerId = playerOne.getId();
//            cpuId = playerTwo.getId();
//            playerHand = gameService.showPlayerHand(playerOne.getId());
//        } else if (isPlayerTwo) {
//            currentPlayerId = playerTwo.getId();
//            cpuId = playerOne.getId();
//            playerHand = gameService.showPlayerHand(playerTwo.getId());
//        } else {
//            throw new RuntimeException("Current user not found in game.");
//        }
//
//        // Chooses the CPU's stat
//        Card cpuCard = gameService.getCpuCard(cpuId);
//        String cpuStat = gameService.getNameOfMaxStatOnCard(cpuCard);
//
//        System.out.println("This is the cpu's chosen stat: " + cpuStat);
//
//        // Get the cpu's stat value.
//        int cpuStatValue = gameService.getMaxStatValueOnACard(cpuCard);
//
//        // Removes CPU's card from their hand so they cant use it again
//        gameService.discardCpuChosenCardFromHand(cpuCard.getId(), cpuId);
//
//        // Selects P1's card, AND REMOVES IT FROM THEIR HAND.
//        Card playedCard = gameService.pickCardFromHand(currentPlayerId, cardId);
//        if (playedCard == null) {
//            model.addAttribute("errorMessage", "Selected card not found.");
//            return "playgame";
//        }
//
//        // Gets P1's selected card ID and stat value
//        int statValue = gameService.getStatValue(cpuStat, playedCard);
//
//        // Compare Player 1 and CPU's stat values:
//        Boolean compareStatsResult = gameService.compareStats(statValue, cpuStatValue);
//
//        // Apply points:
//        if (compareStatsResult == null) {
//            System.out.println("Stat values are equal, round result is a draw. No points awarded.");
//        }
//        else if (compareStatsResult) {
//            gameService.winPoint(currentPlayerId);
//        }
//        else {
//            gameService.winPoint(cpuId);
//        }
//
//        // Access both player's points:
//        int player1Score = gameService.getScore(currentPlayerId);
//        int cpuScore = gameService.getScore(cpuId);
//
//        // Return updated hand
//        List<Card> updatedPlayerHand = gameService.showPlayerHand(currentPlayerId);
//
//        // Check if game has been won, and end it if so
//        int pointsToWinGame = game.getPointsToWin();
//        if (pointsToWinGame == player1Score || pointsToWinGame == cpuScore) {
//            model.addAttribute("gameOver", true);
//            gameService.endGameTally(game.getId());
//        }
//        else {
//            model.addAttribute("gameOver", false);
//        }
//
//        // Put in necessary values.
//        model.addAttribute("currentUser", currentUser);
//        model.addAttribute("playerHand", updatedPlayerHand);
//
//        // Player's round moves
//        model.addAttribute("playedCard", playedCard);
//        model.addAttribute("chosenStat", cpuStat);
//        model.addAttribute("statValue", statValue);
//
//        // CPU's round moves
//        model.addAttribute("cpuPlayedCard", cpuCard);
//        model.addAttribute("cpuChosenStat", cpuStat);
//        model.addAttribute("cpuStatValue", cpuStatValue);
//
//        // Display points and mark round complete
//        model.addAttribute("player1Score", player1Score);
//        model.addAttribute("cpuScore", cpuScore);
//        model.addAttribute("roundComplete", true);
//        model.addAttribute("p1IsAttackingNextRound", true);
//        model.addAttribute("currentAttacker", "P2");
//        model.addAttribute("pointsToWin", game.getPointsToWin());
//
//        model.addAttribute("cpuCustomStatName", gameService.getCardCustomStatName(cpuCard));
//        model.addAttribute("customStatName", gameService.getCardCustomStatName(playedCard));
//
////        // Game logs for easier reading and debugs
////        System.out.println("CPU card picked: " + cpuCard.getName());
////        System.out.println("CPU chosen stat: " + cpuStat);
////        System.out.println("Entered selectPlayerStat with gameId=" + gameId + ", currentPlayerId=" + currentPlayerId + ", cardId=" + cardId + ", chosenStat=" + cpuStat);
////        System.out.println("Played card: " + (playedCard != null ? playedCard.getName() : "null"));
////        System.out.println("CPU card: " + (cpuCard != null ? cpuCard.getName() : "null"));
////        System.out.println("Sending to playgame: cpuCard=" + cpuCard.getName() + ", stat=" + cpuStat);
////        System.out.println("Player 1 Score: " + player1Score);
////        System.out.println("CPU Score: " + cpuScore);
////        System.out.println("Current player Id: " + currentPlayerId);
////        System.out.println("Incrementing score for playerId: " + currentPlayerId);
////        System.out.println("Game points to win: " + game.getPointsToWin());
//
//        model.addAttribute("mode", "game");
//        return "playgame";
//    }
//
//    //    GET -- End of the game. Clears the game, players, and player cards from the db and redirects to new game page.
//    @GetMapping("/game/reset")
//    public RedirectView clearGame() {
//        gameRepository.delete(gameService.findCurrentUserGame());
////        gameService.cleanUp();
//        return new RedirectView("/game/new");
//    }
}
