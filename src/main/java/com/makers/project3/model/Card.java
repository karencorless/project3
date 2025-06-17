package com.makers.project3.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Card {
    @Id
    private String name;
    private String parentDeck;
    private String flavourText;
    private String imageSource;
    private Integer strength;
    private Integer intelligence;
    private Integer defence;
    private Integer luck;
    private Integer customStat;
}