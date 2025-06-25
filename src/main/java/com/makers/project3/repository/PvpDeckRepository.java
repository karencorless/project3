package com.makers.project3.repository;


import com.makers.project3.model.PvpDeck;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PvpDeckRepository extends CrudRepository<PvpDeck, Long> {
    List<PvpDeck> findAllByPvpGameId(Long pvpGameId);

}
