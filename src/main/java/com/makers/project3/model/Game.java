package com.makers.project3.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="player_1_id")
    private Long playerOneId;
    @Column(name="player_2_id")
    private Long playerTwo;
    @Column(name = "points_to_win")
    private int pointsToWin;

}
