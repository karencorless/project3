package com.makers.project3.repository;

import com.makers.project3.model.Player;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlayerRepository extends CrudRepository<Player,Long> {
    void deleteAllById(Long id);

//    ------- Issues with incrementing player score, removing from use ------
//    @Modifying()
//    @Query("UPDATE Player u SET u.currentScore = u.currentScore + 1 WHERE u.id = :id")
//    void incrementCurrentScore(@Param("id") Long id);

    Player findByPlayerUserId(Long playerUserId);
    List<Player> findAllByPlayerUserId(Long playerUserId);
}
