package com.asss.www.ApotekarskaUstanova.Controllers;

import com.asss.www.ApotekarskaUstanova.Dto.Employee_TypeDto;
import com.asss.www.ApotekarskaUstanova.Entity.Employee_Type;
import com.asss.www.ApotekarskaUstanova.Service.EmployeeTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employee-type")
public class EmployeeTypeController {

    private final EmployeeTypeService employeeTypeService;

    @Autowired
    public EmployeeTypeController(EmployeeTypeService employeeTypeService) {
        this.employeeTypeService = employeeTypeService;
    }

    // Endpoint za dobijanje svih tipova
    @GetMapping
    public List<Employee_Type> getAllEmployeeTypes() {
        return employeeTypeService.getAllEmployeeTypes();
    }

    // Endpoint za dobijanje ID-a na osnovu naziva
    @GetMapping("/name/{typeName}")
    public ResponseEntity<Map<String, Integer>> getTypeIdByName(@PathVariable String typeName) {
        int id = employeeTypeService.getTypeIdByName(typeName);
        if (id != -1) {
            // Vraća JSON odgovor: { "id": 1 }
            return ResponseEntity.ok(Collections.singletonMap("id", id));
        } else {
            // Ako tip nije pronađen, vraća 404 Not Found
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", -1));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRole(
            @PathVariable("id") int id,
            @RequestHeader("Authorization") String token) {

        // Provera validnosti tokena
        if (!employeeTypeService.isValidToken(token)) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }

        boolean isDeleted = employeeTypeService.deleteEmployee(id);
        if (isDeleted) {
            return new ResponseEntity<>("Role successfully deleted", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Role not found", HttpStatus.NOT_FOUND);
        }
    }

    // Endpoint za dobavljanje role po ID-u
    @GetMapping("/{id}")
    public ResponseEntity<Employee_TypeDto> getRoleById(@PathVariable int id) {
        Employee_TypeDto role = employeeTypeService.getRoleById(id);
        return ResponseEntity.ok(role);
    }

    @PostMapping
    public ResponseEntity<Employee_TypeDto> createRole(@RequestBody Employee_TypeDto roleDTO) {
        Employee_TypeDto createdRole = employeeTypeService.createRole(roleDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRole);
    }

    // Endpoint za ažuriranje postojeće role
    @PutMapping("/id/{id}")
    public ResponseEntity<Employee_TypeDto> updateRole(@PathVariable int id, @RequestBody Employee_TypeDto roleDTO) {
        Employee_TypeDto updatedRole = employeeTypeService.updateRole(id, roleDTO);
        return ResponseEntity.ok(updatedRole);
    }

}
