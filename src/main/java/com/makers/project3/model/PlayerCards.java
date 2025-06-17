package com.makers.project3.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class PlayerCards {
    @Id
    private Integer playerId;
    private Integer cardId;
    private Boolean discarded;
}
