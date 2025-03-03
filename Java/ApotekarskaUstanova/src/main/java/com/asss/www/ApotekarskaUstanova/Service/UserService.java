package com.asss.www.ApotekarskaUstanova.Service;

import com.asss.www.ApotekarskaUstanova.Entity.User;
import com.asss.www.ApotekarskaUstanova.Dao.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Method to find a user by health card number
    public Optional<User> getUserByHealthCardNumber(String healthCardNumber) {
        return userRepository.findByHealthCardNumber(healthCardNumber);
    }
}