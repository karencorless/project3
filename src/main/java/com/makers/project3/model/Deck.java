package com.makers.project3.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Deck {
    private String name;
    private String uniqueStatName;
    private String thumbnail;
}
