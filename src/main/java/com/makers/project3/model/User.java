package com.makers.project3.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String email;
    private String username;
    @Column(name = "profile_pic")
    private String imageSource;
    private Date birthday;
    @Column(name = "games_won")
    private Integer gamesWon;
    @Column(name = "total_games_played")
    private Integer gamesPlayed;
    @Column(name = "auth0_id", unique = true)
    private String auth0id;
}
