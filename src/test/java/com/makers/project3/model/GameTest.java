package com.makers.project3.model;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class GameTest {
    private Game testGame = new Game(1L, 1L, 2L, 5);

    //    Tests to make sure that the game model has an associated ID
    @Test
    public void gameHasID() {
        assertThat(testGame.getId(), equalTo(1L));
    }

    //    Tests that the game model has an associated ID for player 1
    @Test
    public void gameHasPlayer1ID() {
        assertThat(testGame.getPlayerOneId(), equalTo(1L));
    }

    //    Tests that the game model has an associated ID for player 2
    @Test
    public void gameHasPlayer2ID() {
        assertThat(testGame.getPlayerTwo(), equalTo(2L));
    }

    //    Tests that the game model successfully saved the points to win value
    @Test
    public void gameHasPointsToWinValue() {
        assertThat(testGame.getPointsToWin(), equalTo(5));
    }
}
