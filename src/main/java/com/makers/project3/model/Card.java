package com.makers.project3.model;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cards")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "deck_id")
    private Long parentDeckId;
    private String name;
    @Column(name = "flavour_text")
    private String flavourText;
    @Column(name="image")
    private String imageSource;
    private Integer strength;
    private Integer wisdom;
    private Integer defence;
    private Integer luck;
    @Column(name = "unique_stat")
    private Integer customStat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deck_id", insertable = false, updatable = false)
    private Deck deck;

}
