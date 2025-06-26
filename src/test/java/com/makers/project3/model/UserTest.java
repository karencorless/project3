package com.makers.project3.model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.sql.Date;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class UserTest {

    Date birthday = new Date(1999, Calendar.DECEMBER, 31);
    private User testUser = new User(1L, "test@test.com", "username", "/uploads/profilePics/default.jpg", birthday, 4, 10, "####abcd", 1L);

    //    Tests that the user has an associated ID
    @Test
    public void userHasID() {
        assertThat(testUser.getId(), equalTo(1L));
    }

    //    Tests that the user model saved the username correctly
    @Test
    public void userHasUsername() {
        assertThat(testUser.getUsername(), equalTo("username"));
    }

    //    Tests that the user model saved the email correctly
    @Test
    public void userHasEmail() {
        assertThat(testUser.getEmail(), equalTo("test@test.com"));
    }

    //    Tests that the user model saved the profile picture source name correctly
    @Test
    public void userHasPfp() {
        assertThat(testUser.getImageSource(), equalTo("/uploads/profilePics/default.jpg"));
    }

    //    Tests that the user model saved the birthday correctly
    @Test
    public void userHasBirthday() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        assertThat(testUser.getBirthday(), equalTo(new Date(1999, Calendar.DECEMBER, 31)));
    }

    //    Tests that the user model saved the games won value correctly
    @Test
    public void userHasGamesWon() {
        assertThat(testUser.getGamesWon(), equalTo(4));
    }

    //    Tests that the user model saved the games played value correctly
    @Test
    public void userHasGamesPlayed() {
        assertThat(testUser.getGamesPlayed(), equalTo(10));
    }

    //    Tests that the user model saved the authenticator ID correctly
    @Test
    public void userHasAuthID() {
        assertThat(testUser.getAuth0id(), equalTo("####abcd"));
    }
}
