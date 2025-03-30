package com.asss.www.ApotekarskaUstanova.Service;

import com.asss.www.ApotekarskaUstanova.Dto.SalariesDto;
import com.asss.www.ApotekarskaUstanova.Dto.SalaryDetailsDto;
import com.asss.www.ApotekarskaUstanova.Entity.Salaries;
import com.asss.www.ApotekarskaUstanova.Entity.Employees; // Dodajte import
import com.asss.www.ApotekarskaUstanova.Repository.SalariesRepository;
import com.asss.www.ApotekarskaUstanova.Repository.EmployeeRepository; // Dodajte import
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SalariesService {
    @Autowired
    private SalariesRepository salariesRepository;

    @Autowired
    private EmployeeRepository employeesRepository; // Dodajte EmployeesRepository

    public Integer addSalary(SalariesDto salariesDto) {
        Salaries salary = new Salaries();

        // Pronađite Employees objekat na osnovu employeeId
        Employees employee = employeesRepository.findById(salariesDto.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Zaposleni nije pronađen!"));

        salary.setEmployee(employee); // Postavite Employees objekat
        salary.setAmount(salariesDto.getAmount());
        salary.setPaymentDate(salariesDto.getPaymentDate());
        salary.setMonth(salariesDto.getMonth());
        salary.setYear(salariesDto.getYear());

        Salaries savedSalary = salariesRepository.save(salary);
        return savedSalary.getId();
    }

    public List<String> getUniqueMonthsAndYears() {
        return salariesRepository.findUniqueMonthsAndYears();
    }

    public List<SalaryDetailsDto> getSalariesByMonthAndYear(String monthAndYear) {
        String[] parts = monthAndYear.split("-");
        String month = parts[0];
        int year = Integer.parseInt(parts[1]);

        return salariesRepository.findByMonthAndYear(month, year).stream()
                .map(salary -> {
                    SalaryDetailsDto dto = new SalaryDetailsDto();
                    dto.setSalaryId(salary.getId());
                    dto.setEmployeeName(salary.getEmployee().getName() + " " + salary.getEmployee().getSurname());
                    dto.setTotalAmount(salary.getAmount());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}