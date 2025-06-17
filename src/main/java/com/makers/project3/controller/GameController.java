package com.makers.project3.controller;

import com.makers.project3.Service.GameService;
import com.makers.project3.model.Card;
import com.makers.project3.model.Deck;
import com.makers.project3.repository.CardRepository;
import com.makers.project3.repository.DeckRepository;
import com.makers.project3.repository.GameRepository;
import com.makers.project3.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;

@Controller
public class GameController {

    @Autowired
    GameService gameService;
    @Autowired
    PlayerRepository playerRepository;
    @Autowired
    DeckRepository deckRepository;


    @GetMapping("/newgame")
        public String newGame(Model model){
        List<Deck> chosenDecks = new ArrayList<Deck>();
        chosenDecks.add(deckRepository.findById(1L).orElse(null));
        HashMap<Long, List<Card>> cardsInPlay = gameService.getRandomHandsFromDeck(chosenDecks, 1, 1L, 2L);
        model.addAttribute("cardsInPlay", cardsInPlay);
        return "newgame.html";
    }

}
