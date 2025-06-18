package com.makers.project3.Service;

import com.makers.project3.model.User;
import com.makers.project3.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    AuthenticatedUserService authenticatedUserService;
    @Autowired
    UserRepository userRepository;


//          Get the current User's DB Long id from their Auth0 id
    public  Long getCurrentUserId() {
        String userAuthId = authenticatedUserService.getAuthenticatedAuthId();
        User currentUser = userRepository.findUserByAuth0id(userAuthId).orElse(null);
        if (currentUser != null) {
            return currentUser.getId();
        } else {
            return null;
        }
    }

}
