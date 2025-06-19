package com.makers.project3.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="player_cards")
public class PlayerCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "player_id")
    private Long playerId;
    @Column(name="card_id")
    private Long cardId;
    private Boolean discarded = false;

}
