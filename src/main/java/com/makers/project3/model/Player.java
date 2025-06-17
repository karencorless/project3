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
    private Long playerUserId;
    private Integer currentScore;
    @Column(name="current_card")
    private Long currentCardId;

}
