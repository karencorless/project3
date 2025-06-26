package com.makers.project3.repository;

import com.makers.project3.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    public Optional<User> findUserByAuth0id(String auth0id);
    User findUserByUsername(String username);
    int countByUsername (String username);
}
