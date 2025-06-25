package com.makers.project3.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pvp_games")
public class PvpGame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "player_1_id")
    private Long playerOneId;
    @Column(name = "player_2_id")
    private Long playerTwoId;
    @Column(name = "points_to_win")
    private int pointsToWin;
    @Column(columnDefinition = "varchar default 'WAITING'")
    private String status = "WAITING";
    @Column(name = "created_at", columnDefinition = "timestamp default CURRENT_TIMESTAMP", updatable = false)
    private LocalDateTime createdAt;
    @Column(name="is_player_one_turn")
    private boolean isPlayerOneTurn = true;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public PvpGame(Long playerId, int pointsToWin) {
        this.playerOneId = playerId;
        this.pointsToWin = pointsToWin;
    }

}