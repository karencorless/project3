package com.makers.project3.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.Date;

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
