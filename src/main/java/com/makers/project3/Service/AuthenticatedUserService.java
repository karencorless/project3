package com.makers.project3.Service;

import com.makers.project3.model.User;
import com.makers.project3.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class AuthenticatedUserService {

    private final UserRepository userRepository;

    public AuthenticatedUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Helper method to get the OAuth2User principal or throw if unexpected
    private OAuth2User getPrincipal() {
        Object principalObj = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principalObj instanceof OAuth2User principal)) {
            throw new RuntimeException("Unexpected principal type: " + principalObj.getClass().getName());
        }
        return principal;
    }


    public User getAuthenticatedUser() {
        String authId = getPrincipal().getName();
        User user = userRepository.findUserByAuth0id(authId)
                .orElseGet(() -> {
                    System.out.println("User not found, creating new User with authId: " + authId);
                    return createNewUser(authId);
                });
        return user;
    }

    private User createNewUser(String authId) {
        OAuth2User principal = getPrincipal();
        String email = (String) principal.getAttributes().get("email");
        String imageSource = (String) principal.getAttributes().get("picture");
        String username = (String) principal.getAttributes().get("nickname");
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setImageSource(imageSource);
        newUser.setAuth0id(authId);
        newUser.setUsername(username);
        newUser.setGamesWon(0);
        newUser.setGamesPlayed(0);
        return userRepository.save(newUser);
    }

    public String getAuthenticatedAuthId() {
        return getPrincipal().getName();
    }

    public String getAuthenticatedEmail() {
        return (String) getPrincipal().getAttributes().get("email");
    }
}
