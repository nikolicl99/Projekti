package com.asss.www.ApotekarskaUstanova.Controllers;

import com.asss.www.ApotekarskaUstanova.Security.CustomUserDetails;
import com.asss.www.ApotekarskaUstanova.Security.JwtResponse;
import com.asss.www.ApotekarskaUstanova.Dto.LoginRequest;
import com.asss.www.ApotekarskaUstanova.Security.JwtUtils;
import com.asss.www.ApotekarskaUstanova.Util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());

        boolean matches = PasswordUtil.verifyPassword(loginRequest.getPassword(), userDetails.getPassword());
        if (!matches) {
            throw new BadCredentialsException("Bad credentials");
        }

        CustomUserDetails user = (CustomUserDetails) userDetails;
        int userId = user.getId();
        int typeId = user.getTypeId();

        String jwtToken = jwtUtils.generateToken(userDetails, userId);

        // Sačuvaj token i ID
        JwtResponse.setToken(jwtToken);
        JwtResponse.setUserId(userId);
        JwtResponse.setTypeId(typeId);

        System.out.println("Dobijeni token: " + jwtToken);
        System.out.println("ID zaposlenog: " + userId);
        System.out.println("ID tipa zaposlenog: " + typeId);

        return ResponseEntity.ok(jwtToken);
    }




}

