package com.asss.www.ApotekarskaUstanova.Controllers;

import com.asss.www.ApotekarskaUstanova.Dto.SalariesDto;
import com.asss.www.ApotekarskaUstanova.Dto.SalaryDetailsDto;
import com.asss.www.ApotekarskaUstanova.Service.SalariesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salaries")
public class SalariesController {
    @Autowired
    private SalariesService salariesService;

    @PostMapping
    public ResponseEntity<Integer> addSalary(@RequestBody SalariesDto salariesDto) {
        Integer salaryId = salariesService.addSalary(salariesDto);
        return ResponseEntity.ok(salaryId);
    }

    @GetMapping("/months-and-years")
    public ResponseEntity<List<String>> getUniqueMonthsAndYears() {
        List<String> monthsAndYears = salariesService.getUniqueMonthsAndYears();
        return ResponseEntity.ok(monthsAndYears);
    }

    @GetMapping
    public ResponseEntity<List<SalaryDetailsDto>> getSalariesByMonthAndYear(@RequestParam String monthAndYear) {
        List<SalaryDetailsDto> salaries = salariesService.getSalariesByMonthAndYear(monthAndYear);
        return ResponseEntity.ok(salaries);
    }
}