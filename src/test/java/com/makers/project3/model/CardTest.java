package com.makers.project3.model;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import com.makers.project3.repository.DeckRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class CardTest {
    @Autowired
    DeckRepository deckRepository;

    private Card testCard = new Card(1L, 1L, "Johnny Silverhand", "Legendary rockerboy, visionary, and rebel.", "placeholder.png", 75, 40, 75, 60, 85, deckRepository.findById(1L).orElse(null));

    //    Tests that card model has an associated ID
    @Test
    public void cardHasID() {
        assertThat(testCard.getId(), equalTo(1L));
    }

    //    Tests that card model has associated deck ID
    @Test
    public void cardHasDeck() {
        assertThat(testCard.getParentDeckId(), equalTo(1L));
    }

    //    Tests that card model saved the name correctly
    @Test
    public void cardHasName() {
        assertThat(testCard.getName(), containsString("Johnny Silverhand"));
    }

    //    Tests that card model saved the flavour text correctly
    @Test
    public void cardHasFlavourText() {
        assertThat(testCard.getFlavourText(), containsString("Legendary rockerboy"));
    }

    //    Tests that card model saved the image file name correctly
    @Test
    public void cardHasImage() {
        assertThat(testCard.getImageSource(), containsString("placeholder.png"));
    }

    //    Tests that the card saved the strength stat value correctly
    @Test
    public void cardHasStrengthStat() {
        assertThat(testCard.getStrength(), equalTo(75));
    }

    //    Tests that the card saved the wisdom stat value correctly
    @Test
    public void cardHasWisdomStat() {
        assertThat(testCard.getWisdom(), equalTo(40));
    }

    //    Tests that the card saved the defence stat value correctly
    @Test
    public void cardHasDefenceStat() {
        assertThat(testCard.getDefence(), equalTo(75));
    }

    //    Tests that the card saved the luck stat value correctly
    @Test
    public void cardHasLuckStat() {
        assertThat(testCard.getLuck(), equalTo(60));
    }

    //    Tests that the card saved the custom stat value correctly
    @Test
    public void cardHasCustomStat() {
        assertThat(testCard.getCustomStat(), equalTo(85));
    }

}
