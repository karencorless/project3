package com.makers.project3.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long parentDeckId;
    private String name;
    private String flavourText;
    private String imageSource;
    private Integer strength;
    private Integer intelligence;
    private Integer defence;
    private Integer luck;
    private Integer customStat;

}
