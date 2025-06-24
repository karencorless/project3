package com.makers.project3.Service;

import com.makers.project3.model.User;
import com.makers.project3.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class LeaderboardService {
    @Autowired
    UserRepository userRepository;

    private static final int MIN_GAMES_PLAYED = 10;

    public LeaderboardService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getTopPlayersByWins(int limit) {
        List<User> users = filterEligibleUsers();
        users.sort(Comparator.comparingInt(User::getGamesWon).reversed());
        return limitList(users, limit);
    }

    private List<User> filterEligibleUsers() {
        Iterable<User> allUsers = userRepository.findAll();
        List<User> eligible = new ArrayList<>();
        for (User user : allUsers) {
            Integer gamesPlayed = user.getGamesPlayed();
            if (gamesPlayed != null && gamesPlayed >= MIN_GAMES_PLAYED) {
                eligible.add(user);
            }
        }
        return eligible;
    }

    private List<User> limitList(List<User> users, int limit) {
        if (users.size() > limit) {
            return users.subList(0, limit);
        } else {
            return users;
        }
    }
}
