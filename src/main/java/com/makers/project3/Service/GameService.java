package com.makers.project3.Service;

import com.makers.project3.model.Player;
import com.makers.project3.model.PlayerCards;
import com.makers.project3.repository.CardRepository;
import com.makers.project3.repository.DeckRepository;
import com.makers.project3.repository.GameRepository;
import com.makers.project3.model.Card;
import com.makers.project3.model.Deck;
import com.makers.project3.repository.PlayerCardsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

@Service
public class GameService {
    @Autowired
    GameRepository gameRepository;
    @Autowired
    DeckRepository deckRepository;
    @Autowired
    CardRepository cardRepository;
    @Autowired
    PlayerCardsRepository playerCardsRepository;


    public HashMap<Long, List<Card>> getRandomHandsFromDeck(List<Deck> decksChosen, Integer pointsToWin, Long playerOneId, Long playerTwoId){
        List<Card> playerOneCards = new ArrayList<Card>();
        List<Card> playerTwoCards = new ArrayList<Card>();
        List<Card> allCardsInPlay = new ArrayList<Card>();
        HashMap<Long, List<Card>> newGameStartingHands = new HashMap<>();

//        list of all available cards
        for (Deck deck : decksChosen) {
            List<Card> deckCards = new ArrayList<>(cardRepository.findAllByParentDeckId(deck.getId()));
            allCardsInPlay.addAll(deckCards);
        };

//        random draw, alternating between players
        Boolean playerOneDraw = true;
        for (int i = 0; i <(pointsToWin * 2); i ++) {
            Random random = new Random();
            if (playerOneDraw) {
                playerOneCards.add(allCardsInPlay.remove(random.nextInt(allCardsInPlay.size())));
            }
            else {
                playerTwoCards.add(allCardsInPlay.remove(random.nextInt(allCardsInPlay.size())));
            }
            playerOneDraw = !playerOneDraw;
        }
//        update playerCards repo
        for (Card card : playerOneCards) {
            PlayerCards newCard = new PlayerCards(null, playerOneId, card.getId(), null);
            playerCardsRepository.save(newCard);
        }
        for (Card card : playerTwoCards) {
            PlayerCards newCard = new PlayerCards(null, playerTwoId, card.getId(), null);
            playerCardsRepository.save(newCard);
        }

        newGameStartingHands.put(playerOneId, playerOneCards);
        newGameStartingHands.put(playerTwoId, playerTwoCards);
        return newGameStartingHands;
    }


}
