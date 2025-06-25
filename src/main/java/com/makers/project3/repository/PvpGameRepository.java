package com.makers.project3.repository;


import com.makers.project3.model.PvpGame;
import org.springframework.data.repository.CrudRepository;

public interface PvpGameRepository extends CrudRepository<PvpGame, Long> {
    void deleteAllById(Long id);
    PvpGame findByPlayerOneIdOrPlayerTwoId(Long playerOneId, Long playerTwoId);
}
