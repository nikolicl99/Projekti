package com.asss.www.ApotekarskaUstanova.Security;

import com.asss.www.ApotekarskaUstanova.Dao.EmployeeRepository;
import com.asss.www.ApotekarskaUstanova.Entity.Employees;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Employees user = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        return new CustomUserDetails(
                (long) user.getId(),
                user.getEmail(),
                user.getPassword(),
                new ArrayList<>() // Ovde ide lista uloga ako ih koristiš
        );
    }

}




