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
    @GetMapping("/pvp/join")
    public String joinPvpGame(@RequestParam Long pvpGameId, Model model) {
        User currentUser = authenticatedUserService.getAuthenticatedUser();
        pvpService.addPlayerTwoToPvpGame(pvpGameId, currentUser.getId());
        pvpService.dealPvpPlayerCards(currentUser.getId(), pvpGameId);
        PvpGame currentPvpGame = pvpGameRepository.findById(pvpGameId).orElse(null);
        if (currentPvpGame == null){
            throw new NoSuchEntityExistsException("PvP Game");
        }
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("mode", "pvp");
        return "/pvp/play";
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
        return "waiting";
        }


    @GetMapping("/pvp/play")
    public String playPvpGame(Model model) {
        User currentUser = authenticatedUserService.getAuthenticatedUser();
        PvpGame currentPvpGame = pvpService.findCurrentUserPvpGame();
        Player playerOne = playerRepository.findById(currentPvpGame.getPlayerOneId()).orElse(null);
        Player playerTwo = playerRepository.findById(currentPvpGame.getPlayerTwoId()).orElse(null);
        List<Card> playerHand = new ArrayList<>();
        if (playerOne == null || playerTwo == null) {
            throw new NoSuchEntityExistsException("Player");
        }
        boolean roundComplete = false;
        boolean isPlayerOne = (Objects.equals(currentUser.getId(), playerOne.getPlayerUserId()));
        boolean isPlayerTwo = (Objects.equals(currentUser.getId(), playerOne.getPlayerUserId()));


        // Fetches player scores.
        int player1Score = gameService.getScore(playerOne.getId());
        int player2Score = gameService.getScore(playerTwo.getId());

        if (isPlayerOne) {
            playerHand = gameService.showPlayerHand(playerOne.getId());
            model.addAttribute("cpuScore", player2Score);
        } else if (isPlayerTwo) {
            playerHand = gameService.showPlayerHand(playerTwo.getId());
            model.addAttribute("cpuScore", player1Score);
        }
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("playerHand", playerHand);
        model.addAttribute("player1Score", player1Score);
        model.addAttribute("roundComplete", roundComplete);
        model.addAttribute("p1IsAttackingNextRound", true);
        model.addAttribute("currentAttacker", "P1");
        model.addAttribute("gameOver", false);
        model.addAttribute("pointsToWin", currentPvpGame.getPointsToWin());
        model.addAttribute("mode", "game");
        return "playgame";
    }



}
