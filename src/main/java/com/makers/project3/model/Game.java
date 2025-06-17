package com.makers.project3.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Game {
    @Id
    private Integer id;

    @ManyToOne
    private Player playerOne;

    @ManyToOne
    private Player playerTwo;
}
