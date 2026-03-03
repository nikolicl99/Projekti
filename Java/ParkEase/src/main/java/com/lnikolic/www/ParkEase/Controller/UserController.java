package com.lnikolic.www.ParkEase.Controller;

import com.lnikolic.www.ParkEase.DTO.User.UserCreateDTO;
import com.lnikolic.www.ParkEase.DTO.User.UserResponseDTO;
import com.lnikolic.www.ParkEase.Entity.User;
import com.lnikolic.www.ParkEase.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<UserResponseDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(
            @Valid @RequestBody UserCreateDTO createDTO) {
        UserResponseDTO responseDTO = userService.createUser(createDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseDTO);
    }

}
