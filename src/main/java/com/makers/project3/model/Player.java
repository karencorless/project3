package com.makers.project3.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="players")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id")
    private Long playerUserId;
    @Column(name="current_card")
    private Long currentCardId = null;
    @Column(name="current_stat")
    private String currentStat = null;
    @Column(name = "current_score")
    private Integer currentScore = 0;


    public Player(Long userId) {
        this.playerUserId = userId;
    }

}
