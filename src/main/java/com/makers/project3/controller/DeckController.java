package com.makers.project3.controller;


import com.makers.project3.exception.NoSuchEntityExistsException;
import com.makers.project3.Service.UserService;
import com.makers.project3.model.Card;
import com.makers.project3.model.Deck;
import com.makers.project3.model.User;
import com.makers.project3.repository.DeckRepository;
import com.makers.project3.repository.CardRepository;
import com.makers.project3.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class DeckController {

    @Autowired
    DeckRepository deckRepository;

    @Autowired
    CardRepository cardRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @GetMapping("/decks")
    public String viewDecks(Model model) {
        Iterable<Deck> decks = deckRepository.findAll();
        model.addAttribute("decks", decks);
        User currentUser = (userRepository.findById(userService.getCurrentUserId())).orElse(null);
        model.addAttribute("currentUser", currentUser);
        return "decks/alldecks";
    }

    @GetMapping("/decks/{name}")
    public ModelAndView viewCardsInDeck(@PathVariable("name") String name){
        ModelAndView modelAndView = new ModelAndView("decks/deck");

        Deck deck = deckRepository.findByName(name).orElseThrow(
                () -> new NoSuchEntityExistsException("Deck '" + name + "'"));
        List<Card> cards = cardRepository.findAllByParentDeckId(deck.getId());

        modelAndView.addObject("deck", deck);
        modelAndView.addObject("cards", cards);

        User currentUser = (userRepository.findById(userService.getCurrentUserId())).orElse(null);
        modelAndView.addObject("currentUser", currentUser);

        return modelAndView;


    }

    @GetMapping("/decks/compare")
    public String compareCards(Model model) {
        List<Card> allCards = new ArrayList<>();
        cardRepository.findAll().forEach(allCards::add);
        model.addAttribute("cards", allCards);
        User currentUser = (userRepository.findById(userService.getCurrentUserId())).orElse(null);
        model.addAttribute("currentUser", currentUser);
        return "decks/compare";
    }

    @PostMapping("/decks/compare")
    public String compareSelectedCards(@RequestParam("card1") Long card1Id, @RequestParam("card2") Long card2Id, Model model){
        List<Card> allCards = new ArrayList<>();
        cardRepository.findAll().forEach(allCards::add);
        model.addAttribute("cards", allCards);

        Card card1 = cardRepository.findById(card1Id).orElseThrow(
                () -> new NoSuchEntityExistsException("Card '" + card1Id + "'"));
        Card card2 = cardRepository.findById(card2Id).orElseThrow(
                () -> new NoSuchEntityExistsException("Card '" + card2Id + "'"));

        model.addAttribute("card1", card1);
        model.addAttribute("card2", card2);
        User currentUser = (userRepository.findById(userService.getCurrentUserId())).orElse(null);
        model.addAttribute("currentUser", currentUser);
        return "decks/compare-result";

    }

}
