package com.asss.www.ApotekarskaUstanova.Controllers;

import com.asss.www.ApotekarskaUstanova.Entity.User;
import com.asss.www.ApotekarskaUstanova.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Endpoint to get a user by health card number
    @GetMapping("/health-card/{healthCardNumber}")
    public ResponseEntity<User> getUserByHealthCardNumber(@PathVariable String healthCardNumber) {
        Optional<User> user = userService.getUserByHealthCardNumber(healthCardNumber);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}