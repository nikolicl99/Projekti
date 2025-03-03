package com.asss.www.ApotekarskaUstanova.Controllers;

import com.asss.www.ApotekarskaUstanova.Dto.JwtResponse;
import com.asss.www.ApotekarskaUstanova.Dto.LoginRequest;
import com.asss.www.ApotekarskaUstanova.Security.CustomUserDetails;
import com.asss.www.ApotekarskaUstanova.Security.JwtUtils;
import com.asss.www.ApotekarskaUstanova.Util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    @Autowired
    public AuthController(JwtUtils jwtUtils, UserDetailsService userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());

        boolean matches = PasswordUtil.verifyPassword(loginRequest.getPassword(), userDetails.getPassword());
        if (!matches) {
            throw new BadCredentialsException("Bad credentials");
        }

        // Kastovanje u CustomUserDetails da bismo dobili ID zaposlenog
        Long employeeId = ((CustomUserDetails) userDetails).getId();
        System.out.println("Prijavljeni ID zaposlenog: " + employeeId);

        String jwtToken = jwtUtils.generateToken(userDetails.getUsername());

        Map<String, Object> response = new HashMap<>();
        response.put("token", jwtToken);
        response.put("employeeId", employeeId);

        return ResponseEntity.ok(response);
    }

}

