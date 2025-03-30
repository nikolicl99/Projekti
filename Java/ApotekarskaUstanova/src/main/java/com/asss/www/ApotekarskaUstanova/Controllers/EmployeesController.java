package com.asss.www.ApotekarskaUstanova.Controllers;

import com.asss.www.ApotekarskaUstanova.Dto.EmployeeDto;
import com.asss.www.ApotekarskaUstanova.Entity.Employees;
import com.asss.www.ApotekarskaUstanova.Service.EmployeesService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeesController {

    @Autowired
    EmployeesService employeesService;

    @GetMapping
    public List<Employees> getAllEmployees() {
        return employeesService.getEmployees();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employees> getZaposleni(@PathVariable int id) {
        Employees employees = employeesService.getEmployeeById(id);
        return ResponseEntity.ok(employees);
    }

    @PostMapping("/add")
    public ResponseEntity<EmployeeDto> addEmployee(@RequestBody @Valid EmployeeDto employeeDto, @RequestHeader("Authorization") String token) {
        System.out.println("Received JSON: " + employeeDto);

        EmployeeDto addEmployee = employeesService.addEmployee(employeeDto);
        return ResponseEntity.ok(addEmployee);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployee(
            @PathVariable("id") int id,
            @RequestHeader("Authorization") String token) {

        boolean isDeleted = employeesService.deleteEmployee(id);
        if (isDeleted) {
            return new ResponseEntity<>("Employee successfully deleted", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Employee not found", HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/fire/{id}")
    public ResponseEntity<String> fireEmployee(@PathVariable int id) {
        employeesService.fireEmployee(id);
        return ResponseEntity.ok("Zaposleni uspešno otpušten.");
    }

    @PutMapping("/rehire/{id}")
    public ResponseEntity<String> rehireEmployee(@PathVariable int id) {
        employeesService.rehireEmployee(id);
        return ResponseEntity.ok("Zaposleni uspešno otpušten.");
    }

}
