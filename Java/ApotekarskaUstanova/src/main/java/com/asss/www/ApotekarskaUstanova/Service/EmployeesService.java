package com.asss.www.ApotekarskaUstanova.Service;

import com.asss.www.ApotekarskaUstanova.Dao.AddressRepository;
import com.asss.www.ApotekarskaUstanova.Dao.EmployeeRepository;
import com.asss.www.ApotekarskaUstanova.Dao.EmployeeTypeRepository;
import com.asss.www.ApotekarskaUstanova.Dto.EmployeeDto;
import com.asss.www.ApotekarskaUstanova.Entity.Address;
import com.asss.www.ApotekarskaUstanova.Entity.Employee_Type;
import com.asss.www.ApotekarskaUstanova.Entity.Employees;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeesService {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeTypeRepository employeeTypeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Inject PasswordEncoder

    public List<Employees> getEmployees() {
        return employeeRepository.findAllByOrderByIdAsc();
    }

    public Employees getEmployeeById(int id) {
        return employeeRepository.findByIdWithEmployeeType(id)
                .orElseThrow(() -> new RuntimeException("Zaposleni nije pronađen za ID: " + id));
    }

    public boolean addEmployee(EmployeeDto employeeDto) {
        // Pronađi tip zaposlenog prema imenu
        Employee_Type employeeType = employeeTypeRepository.findByName(employeeDto.getType())
                .orElseThrow(() -> new RuntimeException("Employee type not found"));

        // Pronađi adresu prema ID-ju
        Address address = addressRepository.findById(employeeDto.getAddress())
                .orElseThrow(() -> new RuntimeException("Address not found"));

        Employees employee = new Employees();
        employee.setName(employeeDto.getName());
        employee.setSurname(employeeDto.getSurname());
        employee.setEmail(employeeDto.getEmail());
        employee.setPassword(passwordEncoder.encode(employeeDto.getPassword())); // Koristi PasswordEncoder za bcrypt šifrovanje
        employee.setMobile(employeeDto.getMobile());
        employee.setEmployeeType(employeeType); // Poveži sa tipom zaposlenog
        employee.setAddress(address); // Poveži sa adresom
        employee.setProfileImage(employeeDto.getProfileImage());
        employee.setScaledProfileImage(employeeDto.getScaledProfileImage());

        employeeRepository.save(employee);
        return true;
    }


    // Validacija JWT tokena (ova metoda će zavisiti od vaše implementacije JWT autentifikacije)
    public boolean isValidToken(String token) {
        // Ovde implementirajte validaciju tokena (npr. pomoću JWT token parsera)
        return true;
    }

    public boolean deleteEmployee(long id) {
        Optional<Employees> employee = employeeRepository.findById(id);
        if (employee.isPresent()) {
            employeeRepository.delete(employee.get());
            return true;
        }
        return false;
    }


}
