package com.makers.project3.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerCards {
    private Integer playerUserId;
    private Integer cardId;
    private Boolean discarded;
}
