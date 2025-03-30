package com.asss.www.ApotekarskaUstanova.Service;

import com.asss.www.ApotekarskaUstanova.Repository.AddressRepository;
import com.asss.www.ApotekarskaUstanova.Repository.EmployeeRepository;
import com.asss.www.ApotekarskaUstanova.Repository.EmployeeTypeRepository;
import com.asss.www.ApotekarskaUstanova.Dto.AddressDto;
import com.asss.www.ApotekarskaUstanova.Dto.EmployeeDto;
import com.asss.www.ApotekarskaUstanova.Dto.Employee_TypeDto;
import com.asss.www.ApotekarskaUstanova.Dto.TownDto;
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
    private PasswordEncoder passwordEncoder;

    public List<Employees> getEmployees() {
        return employeeRepository.findAllByOrderByIdAsc();
    }

    public Employees getEmployeeById(int id) {
        return employeeRepository.findByIdWithEmployeeType(id)
                .orElseThrow(() -> new RuntimeException("Zaposleni nije pronađen za ID: " + id));
    }


    public EmployeeDto addEmployee(EmployeeDto employeeDto) {
        Employees employee = convertToEntity(employeeDto);
        Employees savedEmployee = employeeRepository.save(employee);
        return convertToDto(savedEmployee);
    }

    private Employees convertToEntity(EmployeeDto employeeDto) {
        Employees employee = new Employees();
        employee.setName(employeeDto.getName());
        employee.setSurname(employeeDto.getSurname());
        employee.setEmail(employeeDto.getEmail());
        employee.setPassword(passwordEncoder.encode(employeeDto.getPassword()));
        employee.setMobile(employeeDto.getMobile());

        if (employeeDto.getEmployeeType() == null) {
            throw new IllegalArgumentException("Employee type cannot be null");
        }

        if (employeeDto.getEmployeeType().getId() == 0) {
            throw new IllegalArgumentException("Employee type ID must be provided");
        }

        Employee_Type employeeType = employeeTypeRepository.findById(employeeDto.getEmployeeType().getId())
                .orElseThrow(() -> new RuntimeException("Employee type not found for ID: " + employeeDto.getEmployeeType().getId()));

        if (employeeDto.getAddress() == null) {
            throw new IllegalArgumentException("Address cannot be null");
        }

        if (employeeDto.getAddress().getId() == 0) {
            throw new IllegalArgumentException("Address ID must be provided");
        }

        Address address = addressRepository.findById(employeeDto.getAddress().getId())
                .orElseThrow(() -> new RuntimeException("Address not found for ID: " + employeeDto.getAddress().getId()));

        employee.setEmployeeType(employeeType);
        employee.setAddress(address);
        return employee;
    }

    private EmployeeDto convertToDto(Employees employee) {
        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setId(employee.getId());
        employeeDto.setName(employee.getName());
        employeeDto.setSurname(employee.getSurname());
        employeeDto.setEmail(employee.getEmail());
        employeeDto.setMobile(employee.getMobile());

        Employee_TypeDto employeeTypeDto = new Employee_TypeDto();
        employeeTypeDto.setId(employee.getEmployeeType().getId());
        employeeTypeDto.setName(employee.getEmployeeType().getName());

        AddressDto addressDto = new AddressDto();
        addressDto.setId(employee.getAddress().getId());
        addressDto.setAddress(employee.getAddress().getAddress());
        addressDto.setNumber(employee.getAddress().getNumber());
        addressDto.setAptNumber(employee.getAddress().getAptNumber());

        if (employee.getAddress().getTown() != null) {
            TownDto townDto = new TownDto();
            townDto.setId(employee.getAddress().getTown().getId());
            townDto.setName(employee.getAddress().getTown().getName());
            addressDto.setTown(townDto);
            addressDto.setTown_id(employee.getAddress().getTown().getId());
        }

        employeeDto.setEmployeeType(employeeTypeDto);
        employeeDto.setAddress(addressDto);

        return employeeDto;
    }

    public boolean deleteEmployee(int id) {
        Optional<Employees> employee = employeeRepository.findById(id);
        if (employee.isPresent()) {
            employeeRepository.delete(employee.get());
            return true;
        }
        return false;
    }

    public void fireEmployee(int id) {
        Employees employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Zaposleni nije pronađen za ID: " + id));
        employee.setEmployed(0);
        employeeRepository.save(employee);
    }

    public void rehireEmployee(int id) {
        Employees employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Zaposleni nije pronađen za ID: " + id));
        employee.setEmployed(1);
        employeeRepository.save(employee);
    }


}
