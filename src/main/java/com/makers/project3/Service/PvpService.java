package com.makers.project3.Service;

import com.makers.project3.exception.NoSuchEntityExistsException;
import com.makers.project3.model.*;
import com.makers.project3.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public void addPlayerTwoToPvpGame(Long pvpGameId, Long currentUserId) {
        PvpGame game = pvpGameRepository.findById(pvpGameId).orElse(null);
        if (game == null) {
            throw new NoSuchEntityExistsException("PvP Game");
        }
        game.setPlayerTwoId(currentUserId);
        pvpGameRepository.save(game);
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
        boolean playerOneDraw = gameService.coinFlip();
        Collections.shuffle(allCardsInPlay);
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





}
