package com.makers.project3.Service;

import com.makers.project3.model.PlayerCard;
import com.makers.project3.repository.CardRepository;
import com.makers.project3.model.Card;
import com.makers.project3.repository.PlayerCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class GameService {

    @Autowired
    CardRepository cardRepository;
    @Autowired
    PlayerCardRepository playerCardsRepository;


//        Randomly deal cards from the active decks
    public void dealCards(List<Long> decksChosen, Integer pointsToWin, Long playerOneId, Long playerTwoId){
        List<Card> allCardsInPlay = new ArrayList<>();
        //      list of all available cards
        for (Long deckId : decksChosen) {
            List<Card> deckCards = new ArrayList<>(cardRepository.findAllByParentDeckId(deckId));
            allCardsInPlay.addAll(deckCards);
        }
        //      Random draw, alternating between players
        boolean playerOneDraw = true;
        for (int i = 0; i <(pointsToWin * 4); i ++) {
            Collections.shuffle(allCardsInPlay);
            Card card = allCardsInPlay.removeFirst();
            PlayerCard newCard;

            if (playerOneDraw) {
                newCard = new PlayerCard(null, playerOneId, card.getId(), null);
            }
            else {
                newCard = new PlayerCard(null, playerTwoId, card.getId(), null);
            }
            playerCardsRepository.save(newCard);
            playerOneDraw = !playerOneDraw;
        }
    }


//    Return a list of cards currently in the player's hand
    public List<Card> showPlayerHand(Long playerUserId){
        List<PlayerCard> cardIds = playerCardsRepository.findByPlayerUserId(playerUserId);
        List<Card> playerHand = new ArrayList<>();
        for (PlayerCard crd : cardIds) {
            cardRepository.findById(crd.getCardId()).ifPresent(playerHand :: add);
        }
        return playerHand;
    }


//    Delete all playerCards currently in player's hand
    @Transactional
    public void clearHand(Long playerUserId){
        playerCardsRepository.deleteAllByPlayerUserId(playerUserId);
    }

    @Transactional
    public Card pickCardFromHand(Long playerUserId, Long selectedCardId) {
        if (playerUserId == null || selectedCardId == null) {
            System.out.println("Error: Player ID or Selected Card ID is null in pickCardFromHand.");
            return null;
        }

        List<PlayerCard> playerCardsActiveInHand = playerCardsRepository.findByPlayerUserId(playerUserId);

        // Find the card id from playercards and store it
        PlayerCard selectedCardInPlayerCards = null;
        for (PlayerCard playerCard : playerCardsActiveInHand) {
            if (playerCard.getCardId().equals(selectedCardId)) {
                selectedCardInPlayerCards = playerCard;
                break;
            }
        }

        // If card not found, prints warnign
        if (selectedCardInPlayerCards == null) {
            System.out.println("Warning: Card with ID " + selectedCardId + " not found in hand for player " + playerUserId);
            return null; // Indicate that the operation failed
        }

        Optional<Card> optionalCard = cardRepository.findById(selectedCardInPlayerCards.getCardId());

        Card playedCard = optionalCard.get();
        playerCardsRepository.delete(selectedCardInPlayerCards); // Remove the card from player's hand

        return playedCard;
    }

    public boolean coinFlip() {
        Random random = new Random();
        return random.nextBoolean();
    }
}
