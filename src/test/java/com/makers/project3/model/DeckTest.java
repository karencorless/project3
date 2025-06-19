package com.makers.project3.model;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;


public class DeckTest {
    private Deck testDeck = new Deck(1L, "Video Games", "Fame", "placeholder.png");

    //    Tests that saved deck has an associated ID
    @Test
    public void deckHasID() {
        assertThat(testDeck.getId(), equalTo(1L));
    }

    //    Tests that the deck model saved the name correctly
    @Test
    public void deckHasName() {
        assertThat(testDeck.getName(), equalTo("Video Games"));
    }

    //    Tests that the deck has a unique stat name
    @Test
    public void deckHasUniqueStat() {
        assertThat(testDeck.getUniqueStatName(), equalTo("Fame"));
    }

    //    Tests that the deck has an image source for the thumbnail
    @Test
    public void deckHasThumbnailImg() {
        assertThat(testDeck.getThumbnail(), equalTo("placeholder.png"));
    }
}
