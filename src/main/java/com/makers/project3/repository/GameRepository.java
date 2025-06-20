package com.makers.project3.repository;

import com.makers.project3.model.Game;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GameRepository extends CrudRepository<Game, Long> {
    void deleteAllById(Long id);
    void deleteAllByPlayerOneIdOrPlayerTwoId(Long playerOneId, Long playerTwoId);
    Game findByPlayerOneIdOrPlayerTwoId(Long playerOneId, Long playerTwoId);
}
