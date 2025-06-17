package com.makers.project3.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Player {

    private Integer playerUserId;
    private Integer currentScore;
    private Card currentCard;
}
