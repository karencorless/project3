package com.makers.project3.controller;

import com.makers.project3.model.Card;
import com.makers.project3.model.Deck;
import com.makers.project3.repository.DeckRepository;
import com.makers.project3.repository.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class DeckController {

    @Autowired
    DeckRepository deckRepository;

    @Autowired
    CardRepository cardRepository;

    @GetMapping("/decks")
    public String viewDecks(Model model) {
        Iterable<Deck> decks = deckRepository.findAll();
        model.addAttribute("decks", decks);
        return "decks/alldecks";
    }

    @GetMapping("/decks/{name}")
    public ModelAndView viewCardsInDeck(@PathVariable("name") String name){
        ModelAndView modelAndView = new ModelAndView("decks/deck");

        Deck deck = deckRepository.findByName(name);
        List<Card> cards = cardRepository.findAllByParentDeckId(deck.getId());

        modelAndView.addObject("deck", deck);
        modelAndView.addObject("cards", cards);

        return modelAndView;


    }
}
