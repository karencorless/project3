package com.makers.project3.model;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class PlayerTest {
    private Player testPlayer = new Player(1L, 1L, 1L, "Luck", 3, null, null);

    //    Tests that player model has an associated ID
    @Test
    public void playerHasID() {
        assertThat(testPlayer.getId(), equalTo(1L));
    }

    //    Tests that the player model saved an associated User ID
    @Test
    public void playerHasAssociatedUserID() {
        assertThat(testPlayer.getPlayerUserId(), equalTo(1L));
    }

    //    Tests that the player model has an associated active card ID
    @Test
    public void playerHasAssociatedCardID() {
        assertThat(testPlayer.getCurrentCardId(), equalTo(1L));
    }

    //    Tests that the player model has a current active stat saved
    @Test
    public void playerHasCurrentStatValue() {
        assertThat(testPlayer.getCurrentStat(), equalTo("Luck"));
    }

    //    Tests that the player model has a saved current score
    @Test
    public void playerHasCurrentScore() {
        assertThat(testPlayer.getCurrentScore(), equalTo(3));
    }

    //    Tests that the player model score value can update
    @Test
    public void playerCurrentScoreUpdatesSuccessfully() {
        testPlayer.setCurrentScore(testPlayer.getCurrentScore()+1);
        assertThat(testPlayer.getCurrentScore(), equalTo(4));
    }
}
