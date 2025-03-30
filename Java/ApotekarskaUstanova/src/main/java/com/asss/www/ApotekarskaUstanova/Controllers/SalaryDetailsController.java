package com.asss.www.ApotekarskaUstanova.Controllers;

import com.asss.www.ApotekarskaUstanova.Dto.SalaryDetailsDto;
import com.asss.www.ApotekarskaUstanova.Service.SalaryDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/salary-details")
public class SalaryDetailsController {
    @Autowired
    private SalaryDetailsService salaryDetailsService;

    @PostMapping
    public ResponseEntity<Void> addSalaryDetails(@RequestBody SalaryDetailsDto salaryDetailsDto) {
        salaryDetailsService.addSalaryDetails(salaryDetailsDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{salaryId}")
    public ResponseEntity<SalaryDetailsDto> getSalaryDetails(@PathVariable Integer salaryId) {
        SalaryDetailsDto salaryDetails = salaryDetailsService.getSalaryDetailsById(salaryId);
        if (salaryDetails != null) {
            return ResponseEntity.ok(salaryDetails);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}