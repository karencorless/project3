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
    private String image_source;
    private Date birthday;
    private Integer games_won;
    private Integer games_played;

}
