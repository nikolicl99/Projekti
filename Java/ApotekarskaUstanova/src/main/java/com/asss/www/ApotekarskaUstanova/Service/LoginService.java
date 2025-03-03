package com.asss.www.ApotekarskaUstanova.Service;

import com.asss.www.ApotekarskaUstanova.Dao.EmployeeRepository;
import com.asss.www.ApotekarskaUstanova.Entity.Employees;
import com.asss.www.ApotekarskaUstanova.Util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public boolean authenticateUser(String email, String rawPassword) {
        Optional<Employees> employee = employeeRepository.findByEmail(email);

        if (employee.isPresent()) {
            String storedHash = employee.get().getPassword();

            // BCrypt proverava salt i lozinku automatski
            return passwordEncoder.matches(rawPassword, storedHash);
        }

        return false;
    }

}
