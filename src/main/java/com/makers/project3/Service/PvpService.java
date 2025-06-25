package com.makers.project3.Service;

import com.makers.project3.exception.NoSuchEntityExistsException;
import com.makers.project3.model.*;
import com.makers.project3.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class PvpService {
//    needs:
//    be able to initialize a new PvpGame (lobbyInit)
//    able to init a new Game object

    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;
    @Autowired
    PlayerRepository playerRepository;
    @Autowired
    GameService gameService;
    @Autowired
    PvpGameRepository pvpGameRepository;
    @Autowired
    PvpDeckRepository pvpDeckRepository;
    @Autowired
    CardRepository cardRepository;
    @Autowired
    PlayerCardRepository playerCardRepository;
    @Autowired
    AuthenticatedUserService authenticatedUserService;


//          Create new PvpGame
    public PvpGame newPvpGame(int pointsToWin, List<String> chosenDeckIds){

        Long currentUserId = userService.getCurrentUserId();
        Player player = gameService.newPlayer(currentUserId);
        PvpGame game = new PvpGame(player.getId(), pointsToWin);
        pvpGameRepository.save(game);

        // parses the list of strings from newgame.html, assigns them to the pvpGame
        List<Long> chosenDecks = gameService.parseLongIdsFromString(chosenDeckIds);
        for (Long deckId : chosenDecks) {
            PvpDeck newDeck = new PvpDeck(null, game.getId(), deckId);
            pvpDeckRepository.save(newDeck);
        }
        player.setPvpGameId(game.getId());
        playerRepository.save(player);
        return game;
    }

//          Find CurrentUser's Game
    public PvpGame findCurrentUserPvpGame(){
        Long userId = userService.getCurrentUserId();
        List<Player> associatedPlayers = playerRepository.findAllByPlayerUserId(userId);
        for (Player player : associatedPlayers){
            if (Objects.equals(player.getPlayerUserId(), userId)) {
                return pvpGameRepository.findByPlayerOneIdOrPlayerTwoId(player.getId(), player.getId());
            }
        }
        return null;
    }

//          Clear Pvp Game from DB completely -- player Card, PvP Deck, Player, and Game objects
    @Transactional
    public void cleanUp(){
        Long userId = userService.getCurrentUserId();
        List<Player> playersForCurrentUser = playerRepository.findAllByPlayerUserId(userId);
        for (Player player : playersForCurrentUser){
        pvpGameRepository.deleteAllById(player.getPvpGameId());
        }
    }

    public Long addPlayerTwoToPvpGame(Long pvpGameId, Long currentUserId) {
        PvpGame game = pvpGameRepository.findById(pvpGameId).orElse(null);
        if (game == null) {
            throw new NoSuchEntityExistsException("PvP Game");
        }
        Player playerTwo = new Player(currentUserId);
        playerRepository.save(playerTwo);
        game.setPlayerTwoId(playerTwo.getId());
        pvpGameRepository.save(game);
        return playerTwo.getId();
    }


    public void dealPvpPlayerCards(Long playerTwoId, Long pvpGameId) {
        List<Card> allCardsInPlay = new ArrayList<>();
        List<PvpDeck> decksChosen = pvpDeckRepository.findAllByPvpGameId(pvpGameId);
        PvpGame pvpGame = pvpGameRepository.findById(pvpGameId).orElse(null);
        assert pvpGame != null;
        Long playerOneId = pvpGame.getPlayerOneId();

        for (PvpDeck deck : decksChosen) {
            if (deck == null) {
                throw new NoSuchEntityExistsException("Deck");
            }
            List<Card> deckCards = new ArrayList<>(cardRepository.findAllByParentDeckId(deck.getDeckId()));
            allCardsInPlay.addAll(deckCards);
        }
        Collections.shuffle(allCardsInPlay);
        boolean playerOneDraw = gameService.coinFlip();
        for (int i = 0; i <(pvpGame.getPointsToWin() * 4); i ++) {
            Card card = allCardsInPlay.removeFirst();
            PlayerCard newCard;

            if (playerOneDraw) {
                newCard = new PlayerCard(null, playerOneId, card.getId(), false);
            }
            else {
                newCard = new PlayerCard(null, playerTwoId, card.getId(), false);
            }
            playerCardRepository.save(newCard);
            playerOneDraw = !playerOneDraw;
        }
        pvpGame.setStatus("IN_GAME");
        pvpGameRepository.save(pvpGame);
    }

    //          Allows player to select card from hand, updates playerCards accordingly.
    @Transactional
    public Card pickCardFromHand(Player player, Long selectedCardId) {
        // Makes sure player id and card id are valid
        if (player == null || selectedCardId == null) {
            System.out.println("Error: Player ID or Selected Card ID is null in pickCardFromHand.");
            return null;
        }

        List<PlayerCard> playerCardsActiveInHand = playerCardRepository.findByPlayerId(player.getId());

        // Find the card id from playerCards and store it
        PlayerCard selectedCardInPlayerCards = null;
        for (PlayerCard playerCard : playerCardsActiveInHand) {
            if (playerCard.getCardId().equals(selectedCardId)) {
                selectedCardInPlayerCards = playerCard;
                break;
            }
        }

        // If card not found, prints warning
        if (selectedCardInPlayerCards == null) {
            System.out.println("Warning: Card with ID " + selectedCardId + " not found in hand for player " + player.getId());
            return null;
        }

        Card playedCard = cardRepository.findById(selectedCardInPlayerCards.getCardId()).orElse(null);
        if (playedCard == null) {throw new NoSuchEntityExistsException("Card");}

        player.setCurrentCardId(selectedCardId);
        selectedCardInPlayerCards.setDiscarded(true);

        System.out.println("This is P1's current hand: " + playerCardsActiveInHand);

        return playedCard;
    }

    public Model prepModel(Model model){
        User currentUser = authenticatedUserService.getAuthenticatedUser();
        PvpGame currentPvpGame = findCurrentUserPvpGame();

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
//        List<Card> playerHand = gameService.showPlayerHand(currentPlayer.getId());

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("currentPlayer", currentPlayer);
        model.addAttribute("opponent", opponent);
        model.addAttribute("game", currentPvpGame);
        model.addAttribute("mode", "pvp");
        return model;
    }

}
