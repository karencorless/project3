package com.makers.project3.repository;

import com.makers.project3.model.PlayerCard;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PlayerCardRepository extends CrudRepository<PlayerCard, Long> {
    List<PlayerCard> findByPlayerId(Long playerUserId);
    void deleteAllByPlayerId(Long playerUserId);
    PlayerCard findByCardIdAndPlayerId(Long cardId, Long playerId);
}
