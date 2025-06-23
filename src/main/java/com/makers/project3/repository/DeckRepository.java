package com.makers.project3.repository;

import com.makers.project3.model.Deck;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface DeckRepository extends CrudRepository<Deck, Long> {
    Optional<Deck> findByName(String name);
}
