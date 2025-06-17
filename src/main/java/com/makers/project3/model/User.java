package com.makers.project3.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
public class User {
    @Id
    private String email;
    private String username;
    private String image_source;
    private Date birthday;
    private Integer games_won;
    private Integer games_played;
}
