package com.makers.project3.model;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;


public class PlayerCardTest {
    private PlayerCard testPlayerCard = new PlayerCard(1L, 1L, 1L, Boolean.FALSE);

    //    Tests that player card model has an associated ID
    @Test
    public void playerCardHasID() {
        assertThat(testPlayerCard.getId(), equalTo(1L));
    }

    //    Tests that player card model saved the associated user ID correctly
    @Test
    public void playerCardHasUserID() {
        assertThat(testPlayerCard.getPlayerId(), equalTo(1L));
    }

    //    Tests that player card model saved the associated card ID correctly
    @Test
    public void playerCardHasCardID() {
        assertThat(testPlayerCard.getCardId(), equalTo(1L));
    }

    //    Tests that player card model saved the discard status correctly
    @Test
    public void playerCardHasDiscardValue() {
        assertThat(testPlayerCard.getDiscarded(), equalTo(Boolean.FALSE));
    }

}
