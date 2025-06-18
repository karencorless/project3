package com.makers.project3.repository;

import com.makers.project3.model.Deck;
import org.springframework.data.repository.CrudRepository;

public interface DeckRepository extends CrudRepository<Deck, Long> {
    Deck findByName(String name);
}
