package com.makers.project3.repository;

import com.makers.project3.model.Game;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface GameRepository extends CrudRepository<Game, Long> {
    void deleteAllById(Long id);
    void deleteAllByPlayerOneIdOrPlayerTwoId(Long playerOneId, Long playerTwoId);
    Optional<Game> findByPlayerOneIdOrPlayerTwoId(Long playerOneId, Long playerTwoId);
}
