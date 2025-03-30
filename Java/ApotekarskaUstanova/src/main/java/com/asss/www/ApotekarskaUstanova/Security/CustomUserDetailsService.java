package com.asss.www.ApotekarskaUstanova.Security;

import com.asss.www.ApotekarskaUstanova.Repository.EmployeeRepository;
import com.asss.www.ApotekarskaUstanova.Entity.Employees;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Employees user = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
        return new CustomUserDetails(
                user.getId(),
                user.getEmployeeType().getId(),
                user.getEmail(),
                user.getPassword()
        );
    }
}
