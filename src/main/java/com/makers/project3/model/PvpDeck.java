package com.makers.project3.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="pvp_decks")
public class PvpDeck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "pvp_game_id")
    private Long pvpGameId;
    @Column(name="card_id")
    private Long cardId;

}
