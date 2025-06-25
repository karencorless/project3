package com.makers.project3.Service;

import com.makers.project3.exception.NoSuchEntityExistsException;
import com.makers.project3.model.*;
import com.makers.project3.repository.*;
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
        User roboUser = userRepository.findById(1L).orElse(null);
        assert roboUser != null;
        Long roboUserId = roboUser.getId();
        Game game;

        // creates player objects for the user and roboPlayer
        Player player = newPlayer(currentUserId);
        Player roboPlayer = newPlayer(roboUserId);

        // parses the list of strings from newgame.html, uses the long ID's to deal the hands
        List<Long> chosenDecks = parseLongIdsFromString(chosenDeckIds);
        dealCards(chosenDecks, player.getId(), roboPlayer.getId(), pointsToWin);

        // determines who goes first and creates the Game object
        if (coinFlip()) {
            game = new Game(null, player.getId(), roboPlayer.getId(), pointsToWin);
        } else {
            game = new Game(null, roboPlayer.getId(), player.getId(), pointsToWin);
        }

        gameRepository.save(game);
        player.setGameId(game.getId());
        roboPlayer.setGameId(game.getId());
        playerRepository.save(player);
        playerRepository.save(roboPlayer);

        return game;
    }


    //          Creates new Player object
    public Player newPlayer(Long userId){
        Player player = new Player(userId);
        playerRepository.save(player);
        return player;
    }


    //        Randomly deal cards from the active decks
    public void dealCards(List<Long> decksChosen, Long playerOneId, Long playerTwoId, Integer pointsToWin){
        List<Card> allCardsInPlay = new ArrayList<>();
        //      list of all available cards
        for (Long deckId : decksChosen) {
            if (deckId == null) {
                throw new NoSuchEntityExistsException("Deck");
            }
            List<Card> deckCards = new ArrayList<>(cardRepository.findAllByParentDeckId(deckId));
            allCardsInPlay.addAll(deckCards);
        }
        //      Random draw, alternating between players
        boolean playerOneDraw = true;
        Collections.shuffle(allCardsInPlay);

        for (int i = 0; i <(pointsToWin * 4); i ++) {
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

    //          Allows player to select card from hand, updates playerCards accordingly.
    @Transactional
    public Card pickCardFromHand(Long playerId, Long selectedCardId) {
        // Makes sure player id and card id are valid
        if (playerId == null || selectedCardId == null) {
            System.out.println("Error: Player ID or Selected Card ID is null in pickCardFromHand.");
            return null;
        }

        List<PlayerCard> playerCardsActiveInHand = playerCardRepository.findByPlayerId(playerId);

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
            System.out.println("Warning: Card with ID " + selectedCardId + " not found in hand for player " + playerId);
            return null;
        }

        Optional<Card> optionalCard = cardRepository.findById(selectedCardInPlayerCards.getCardId());

        Card playedCard = optionalCard.get();
        Player currentPlayer = playerRepository.findById(playerId).orElse(null);

        currentPlayer.setCurrentCardId(selectedCardId);
        selectedCardInPlayerCards.setDiscarded(true);

        System.out.println("This is P1's current hand: " + playerCardsActiveInHand);

        return playedCard;
    }


    //          Returns the player's current score
    public Integer getScore(Long playerId) {
        return (Objects.requireNonNull(playerRepository.findById(playerId).orElse(null))).getCurrentScore();
    }


    @Transactional
    public void winPoint(Long playerId) {
        // Updated as previous wasn't updating for some reason
        Player player = playerRepository.findById(playerId).orElse(null);
        if (player != null) {
            player.setCurrentScore(player.getCurrentScore() + 1);
            playerRepository.save(player);
            System.out.println("Manually incremented score for playerId: " + playerId + ", new score: " + player.getCurrentScore());
        } else {
            System.out.println("Player not found with id: " + playerId);
        }
    }


    //          Compare players' scores
    public Player compareCurrentScores(Long playerOneId, Long playerTwoId){
        assert checkCurrentUserIsPlayer(playerOneId) || checkCurrentUserIsPlayer(playerTwoId);

        Integer playerOneScore = getScore(playerOneId);
        Integer playerTwoScore = getScore(playerTwoId);
        if (playerOneScore > playerTwoScore) {
            return playerRepository.findById(playerOneId).orElse(null);
        } else if (playerTwoScore > playerOneScore) {
            return playerRepository.findById(playerTwoId).orElse(null);
        } else {
            return null;
        }
    }

    //          Compare players' stat values
    public Boolean compareStats(int p1StatValue, int cpuStatValue){
        if (p1StatValue > cpuStatValue) {
            return true;
        } else if (cpuStatValue > p1StatValue) {
            return false;
        } else {
            return null;
        }
    }

    //          Return list of the cards currently in the player's hand
    public List<Card> showPlayerHand(Long playerId){
        assert checkCurrentUserIsPlayer(playerId);

        List<PlayerCard> playerCardsInHand = playerCardRepository.findByPlayerId(playerId);
        List<Card> playerHand = new ArrayList<>();
        for (PlayerCard card : playerCardsInHand) {
            if (!card.getDiscarded()) {
                cardRepository.findById(card.getCardId()).ifPresent(playerHand::add);
            }
        }
        return playerHand;
    }


    //      Return list of the player objects, derived from the Game id
    public Player[] getPlayersFromGame(Long gameId){
        assert checkCurrentUserIsPlayerInGame(gameId);

        Game game = gameRepository.findById(gameId).orElse(null);
        assert game != null;
        Player playerOne = playerRepository.findById(game.getPlayerOneId()).orElse(null);
        Player playerTwo = playerRepository.findById(game.getPlayerTwoId()).orElse(null);
        return new Player[]{playerOne, playerTwo};
    }

    //          Find CurrentUser's Game
    public Game findCurrentUserGame(){
        Long userId = userService.getCurrentUserId();
        List<Player> associatedPlayers = playerRepository.findAllByPlayerUserId(userId);
        for (Player player : associatedPlayers){
            if (Objects.equals(player.getPlayerUserId(), userId)) {
                return gameRepository.findByPlayerOneIdOrPlayerTwoId(player.getId(), player.getId());
            }
        }
        return null;
    }

//          Gets the card's stat value based on chosen attribute
    public int getStatValue(String stat, Card card){
        String normalizedStat = stat.toLowerCase();

        if ("strength".equals(normalizedStat)) {
            return card.getStrength();
        } else if ("wisdom".equals(normalizedStat)) {
            return card.getWisdom();
        } else if ("defence".equals(normalizedStat)) {
            return card.getDefence();
        } else if ("luck".equals(normalizedStat)) {
            return card.getLuck();
        }
        else {
            return card.getCustomStat();
        }
    }


    //      Finds the highest value stat on one card for CPU
    public int getMaxStatValueOnACard(Card card) {
        return Collections.max(List.of(
                card.getStrength(),
                card.getWisdom(),
                card.getDefence(),
                card.getLuck(),
                card.getCustomStat()
        ));
    }

//      Finds the name of the attribute with the highest value on a card:
    public String getNameOfMaxStatOnCard(Card card) {
        int maxValue = getMaxStatValueOnACard(card);  // get highest value

        if (card.getStrength() == maxValue) return "strength";
        if (card.getWisdom() == maxValue) return "wisdom";
        if (card.getDefence() == maxValue) return "defence";
        if (card.getLuck() == maxValue) return "luck";
        if (card.getCustomStat() == maxValue) return card.getDeck().getUniqueStatName();

        return "unknown";
    }

    //      Finds the ID of the opponent...
    public Long getOppIdForGame(Game game, Long currentPlayerId) {
        if (game.getPlayerOneId().equals(currentPlayerId)) {
            return game.getPlayerTwoId();
        } else if (game.getPlayerTwoId().equals(currentPlayerId)) {
            return game.getPlayerOneId();
        } else {
            return null; // or throw exception if player not in game
        }
    }

    //      Finds the card with the highest stat value in the CpU's hand
    public Card getCpuCard(Long cpuId){
        List<PlayerCard> cpuCardsInHand = playerCardRepository.findByPlayerId(cpuId);

        List<Card> cpuHand = new ArrayList<>();
        for (PlayerCard card : cpuCardsInHand) {
            if (!card.getDiscarded()) {
                cardRepository.findById(card.getCardId()).ifPresent(cpuHand::add);
            }
        }
        cpuHand.sort(Comparator.comparingInt(this::getMaxStatValueOnACard).reversed());

        // Return the highest stat value
        return cpuHand.isEmpty() ? null : cpuHand.get(0);
    }

    //      Get cpu's highest card with MY Chosen attribute.
    public Card getCpuCardWithP1sStat(Long cpuId, String chosenStat) {
        List<PlayerCard> cpuCardsInHand = playerCardRepository.findByPlayerId(cpuId);

        List<Card> cpuHand = new ArrayList<>();
        for (PlayerCard pc : cpuCardsInHand) {
            if (!pc.getDiscarded()) {
                cardRepository.findById(pc.getCardId()).ifPresent(cpuHand::add);
            }
        }
        cpuHand.sort(Comparator.comparingInt((Card card) -> getStatValue(chosenStat, card)).reversed());

        return cpuHand.isEmpty() ? null : cpuHand.get(0);
    }


    public Game getGameById(Long gameId) {
        return gameRepository.findById(gameId).orElse(null);
    }


    // Mark the CPU's chosen card as discarded
    public void discardCpuChosenCardFromHand(Long cardId, Long cpuId) {

        // Get the cpu's hand
        List<PlayerCard> cpuCardsInHand = playerCardRepository.findByPlayerId(cpuId);

        // Find the card id from playerCards and store it
        PlayerCard selectedCardInCpuCards = null;
        for (PlayerCard playerCard : cpuCardsInHand) {
            if (playerCard.getCardId().equals(cardId)) {
                selectedCardInCpuCards = playerCard;
                selectedCardInCpuCards.setDiscarded(true);
                break;
            }
            else {
                System.out.println("Error removing card with ID " + cardId + "from CPU hand.");
            }
        }

        // Stack trace debugs:
        List<Card> cpuHand = new ArrayList<>();
        for (PlayerCard card : cpuCardsInHand) {
            if (!card.getDiscarded()) {
                cardRepository.findById(card.getCardId()).ifPresent(cpuHand::add);
            }
        }

        System.out.println("This is the CPU's current hand :" + cpuHand);
    }

    // Get P1's selected cards custom stat name
    public String getCardCustomStatName(Card card) {
        return card.getDeck().getUniqueStatName();
    }

    //  Deletion and end game related methods:

    public void endGameTally(Long gameId) {
        assert checkCurrentUserIsPlayerInGame(gameId);

        Game game = gameRepository.findById(gameId).orElse(null);
        if (game == null) {
            throw new NoSuchEntityExistsException("Game");}

        Player playerOne = playerRepository.findById(game.getPlayerOneId()).orElse(null);
        Player playerTwo = playerRepository.findById(game.getPlayerTwoId()).orElse(null);
        if (playerOne == null || playerTwo == null) {
            throw new NoSuchEntityExistsException("Player");
        }

        User userOne = userRepository.findById(playerOne.getPlayerUserId()).orElse(null);
        User userTwo = userRepository.findById(playerTwo.getPlayerUserId()).orElse(null);
        assert userOne != null;
        assert userTwo != null;
        userOne.setGamesPlayed(userOne.getGamesPlayed()+1);
        userTwo.setGamesPlayed(userTwo.getGamesPlayed()+1);

        Player winnerPlayer = compareCurrentScores(playerOne.getId(), playerTwo.getId());
        if (winnerPlayer == null) {return;}
        User winner =  userRepository.findById(winnerPlayer.getPlayerUserId()).orElse(null);
        if (winner == null) {
            throw new NoSuchEntityExistsException("User");
        } else if (winner == userOne) {
            userOne.setGamesWon(userOne.getGamesWon()+1);
        } else if (winner == userTwo) {
            userTwo.setGamesWon(userTwo.getGamesWon()+1);
        }
        userRepository.save(userOne);
        userRepository.save(userTwo);
    }


    @Transactional
    public void cleanUp(){
        Long userId = userService.getCurrentUserId();
        List<Player> playersForCurrentUser = playerRepository.findAllByPlayerUserId(userId);
        for (Player player : playersForCurrentUser){
            gameRepository.deleteAllById(player.getGameId());
        }
    }
}