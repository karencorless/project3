package com.makers.project3.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id")
    private Long playerUserId;
    @Column(name="current_card")
    private Long currentCardId;
    @Column(name="current_stat")
    private Long currentStat;
    @Column(name = "current_score")
    private Integer currentScore;
    private Card currentCard;
}
