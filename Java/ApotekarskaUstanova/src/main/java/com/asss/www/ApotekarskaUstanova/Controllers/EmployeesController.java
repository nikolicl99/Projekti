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
    public ResponseEntity<String> addEmployee(@RequestBody @Valid EmployeeDto employeeDto, @RequestHeader("Authorization") String token) {
        System.out.println("Received JSON: " + employeeDto);

        // Provera da li Base64 string nije null ili prazan
        if (EmployeeDto.getProfileImage() == null || EmployeeDto.getProfileImage().isEmpty()) {
            System.out.println("Image field is null or empty!");
            return ResponseEntity.badRequest().body("Image data is missing");
        }

        if (token == null || !employeesService.isValidToken(token)) {
            return new ResponseEntity<>("Invalid token", HttpStatus.UNAUTHORIZED);
        }

        boolean isAdded = employeesService.addEmployee(employeeDto);
        if (isAdded) {
            return new ResponseEntity<>("Employee successfully added", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Error while adding employee", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployee(
            @PathVariable("id") long id,
            @RequestHeader("Authorization") String token) {

        // Provera validnosti tokena
        if (!employeesService.isValidToken(token)) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }

        boolean isDeleted = employeesService.deleteEmployee(id);
        if (isDeleted) {
            return new ResponseEntity<>("Employee successfully deleted", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Employee not found", HttpStatus.NOT_FOUND);
        }
    }

}
