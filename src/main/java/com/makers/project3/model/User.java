package com.makers.project3.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
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
    @Column(name = "games_played")
    private Integer gamesPlayed;

}
