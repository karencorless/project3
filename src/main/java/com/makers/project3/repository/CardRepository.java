package com.makers.project3.repository;

import com.makers.project3.model.Card;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CardRepository extends CrudRepository<Card, Long> {
    List<Card> findAllByParentDeckId(Long parentDeckId);

}
