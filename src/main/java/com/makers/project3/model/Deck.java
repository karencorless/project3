package com.makers.project3.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class Deck {
    @Id
    private String name;
    private String uniqueStatName;
    private String thumbnail;
}
