package com.makers.project3.Service;

import com.makers.project3.model.PlayerCard;
import com.makers.project3.model.Player;
import com.makers.project3.model.Game;
import com.makers.project3.model.Card;
import com.makers.project3.model.User;
import com.makers.project3.repository.PlayerCardRepository;
import com.makers.project3.repository.PlayerRepository;
import com.makers.project3.repository.GameRepository;
import com.makers.project3.repository.UserRepository;
import com.makers.project3.repository.CardRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class GameService {

    @Autowired
    CardRepository cardRepository;
    @Autowired
    PlayerCardRepository playerCardRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PlayerRepository playerRepository;
    @Autowired
    GameRepository gameRepository;
    @Autowired
    UserService userService;


                        //   Security related methods:

//          Verify that currentUser is in the game
    public boolean checkCurrentUserIsPlayerInGame(Long gameId) {
        Long currentUserId = userService.getCurrentUserId();
        Player[] players = getPlayersFromGame(gameId);
        return Objects.equals(currentUserId, players[0].getPlayerUserId()) || Objects.equals(currentUserId, players[1].getPlayerUserId());
    }
            //  should update or add a method to compare with robo user,
            //  so we can add it to winPoint and getScore.

//          Verify that currentUser is player
    public boolean checkCurrentUserIsPlayer(Long playerId){
        Long currentUserId = userService.getCurrentUserId();
        Player player = playerRepository.findById(playerId).orElse(null);
        assert player != null;
        return Objects.equals(currentUserId, player.getPlayerUserId());
    }


                    //  Creation/game start related methods:

//       Creates new Game
    public Game newGame(int pointsToWin, List<String> chosenDeckIds){
        Long currentUserId = userService.getCurrentUserId();
        User opp = userRepository.findUserByUsername("robouser1");
        Long oppId = opp.getId();
        Game game;

        // creates player objects for the user and opponent
        Player player = newPlayer(currentUserId);
        Player opponent = newPlayer(oppId);

        // parses the list of strings from newgame.html, uses the long ID's to deal the hands
        List<Long> chosenDecks = parseLongIdsFromString(chosenDeckIds);
        dealCards(chosenDecks, player.getId(), opponent.getId(), pointsToWin);

        // determines who goes first and creates the Game object
        if (coinFlip()) {
            game = new Game(null, player.getId(), opponent.getId(), pointsToWin);
        } else {
            game = new Game(null, opponent.getId(), player.getId(), pointsToWin);
        }
        gameRepository.save(game);
        return game;
    }


//          Creates new Player object
    public Player newPlayer(Long userId){
        Player player = new Player(userId);
        playerRepository.save(player);
        return player;
    }

////                ----------- KAREN CODE --------------
////        Randomly deal cards from the active decks
//public void dealCards(List<Long> decksChosen, Integer pointsToWin, Long playerOneId, Long playerTwoId){
//    List<Card> allCardsInPlay = new ArrayList<>();
//    //      list of all available cards
//    for (Long deckId : decksChosen) {
//        List<Card> deckCards = new ArrayList<>(cardRepository.findAllByParentDeckId(deckId));
//        allCardsInPlay.addAll(deckCards);
//    }
//    //      Random draw, alternating between players
//    boolean playerOneDraw = true;
//    for (int i = 0; i <(pointsToWin * 4); i ++) {
//        Collections.shuffle(allCardsInPlay);
//        Card card = allCardsInPlay.removeFirst();
//        PlayerCard newCard;
//
//        if (playerOneDraw) {
//            newCard = new PlayerCard(null, playerOneId, card.getId(), null);
//        }
//        else {
//            newCard = new PlayerCard(null, playerTwoId, card.getId(), null);
//        }
//        playerCardsRepository.save(newCard);
//        playerOneDraw = !playerOneDraw;
//    }
//}

////                    --AVIAN--
//        Randomly deal cards from the active decks
    public void dealCards(List<Long> decksChosen, Long playerOneId, Long playerTwoId, Integer pointsToWin){
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
            playerCardRepository.save(newCard);
            playerOneDraw = !playerOneDraw;
        }
    }


//      Takes the String deckIds from newgame.html, parses the usable Long values, and returns that list
    public List<Long> parseLongIdsFromString(List<String>chosenDeckIds){
        List<Long> chosenDecks = new ArrayList<>();
        for (String deckId : chosenDeckIds) {
            Long id = Long.parseLong(deckId);
            chosenDecks.add(id);
        }
        return chosenDecks;
    }


//      Random Boolean Generator
    public boolean coinFlip() {
        Random random = new Random();
        return random.nextBoolean();
    }


                                 //    Game Play related methods:
////            --------  KAREN ----------
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






///         ---- AVIAN ----
//          Returns the player's current score
    public Integer getScore(Long playerId) {
        return (Objects.requireNonNull(playerRepository.findById(playerId).orElse(null))).getCurrentScore();
    }


//          Updates player's currentScore
    @Transactional
    public void winPoint(Long playerId) {
        playerRepository.incrementCurrentScore(playerId);
    }


//          Compare scores
    public Long compareCurrentScores(Long playerOneId, Long playerTwoId){
        assert checkCurrentUserIsPlayer(playerOneId) || checkCurrentUserIsPlayer(playerTwoId);

        Integer playerOneScore = getScore(playerOneId);
        Integer playerTwoScore = getScore(playerTwoId);
        if (playerOneScore > playerTwoScore) {
            return playerOneId;
        } else if (playerTwoScore > playerOneScore) {
            return playerTwoId;
        } else {
            return null; // need error handling
        }
    }


//          Return list of the cards currently in the player's hand
    public List<Card> showPlayerHand(Long playerId){
        assert checkCurrentUserIsPlayer(playerId);

        List<PlayerCard> cardIds = playerCardRepository.findByPlayerId(playerId);
        List<Card> playerHand = new ArrayList<>();
        for (PlayerCard card : cardIds) {
            cardRepository.findById(card.getCardId()).ifPresent(playerHand :: add);
        }
        return playerHand;
    }


//      Return list of the players, derived from the Game id
    public Player[] getPlayersFromGame(Long gameId){
        assert checkCurrentUserIsPlayerInGame(gameId);

        Game game = gameRepository.findById(gameId).orElse(null);
        assert game != null;
        Player playerOne = playerRepository.findById(game.getPlayerOneId()).orElse(null);
        Player playerTwo = playerRepository.findById(game.getPlayerTwoId()).orElse(null);
        return new Player[]{playerOne, playerTwo};
    }


                    //  Deletion/ end game related methods:

//        End Game, determine winner
    public boolean endGame(Long gameId) {
        assert checkCurrentUserIsPlayerInGame(gameId);

        Long currentUserId = userService.getCurrentUserId();
        Player[] players = getPlayersFromGame(gameId);
        Long winnerPlayerId = compareCurrentScores(players[0].getId(), players[1].getId());
        if (winnerPlayerId == null){
            return false;
        }
        Long winnerUserId = playerRepository.findById(winnerPlayerId).get().getPlayerUserId();
        return Objects.equals(winnerUserId, currentUserId);
    }


//          Clears the game from the DB, deleting the playerCard, player, and game objects.
    @Transactional
    public void deleteGame(Long gameId){
        assert checkCurrentUserIsPlayerInGame(gameId);

        Game game = gameRepository.findById(gameId).orElse(null);
        assert game != null;

        Long playerOneId = game.getPlayerOneId();
        Long playerTwoId = game.getPlayerTwoId();

        playerCardRepository.deleteAllByPlayerId(playerOneId);
        playerCardRepository.deleteAllByPlayerId(playerTwoId);
        playerRepository.deleteAllById(playerOneId);
        playerRepository.deleteAllById(playerTwoId);
        gameRepository.deleteAllById(gameId);
    }


    //    Delete all playerCards currently in player's hand
    @Transactional
    public void clearHand(Long playerId){
        playerCardRepository.deleteAllByPlayerId(playerId);
    }

}
