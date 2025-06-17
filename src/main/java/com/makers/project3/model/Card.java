package com.makers.project3.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String parentDeck;
    private String name;
    private String flavourText;
    private String imageSource;
    private Integer strength;
    private Integer intelligence;
    private Integer defence;
    private Integer luck;
    private Integer customStat;
}
