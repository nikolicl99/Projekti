package com.asss.www.ApotekarskaUstanova.Service;

import com.asss.www.ApotekarskaUstanova.Repository.EmployeeRepository;
import com.asss.www.ApotekarskaUstanova.Repository.SalesRepository;
import com.asss.www.ApotekarskaUstanova.Dto.EmployeeDto;
import com.asss.www.ApotekarskaUstanova.Dto.SalesDto;
import com.asss.www.ApotekarskaUstanova.Entity.Employees;
import com.asss.www.ApotekarskaUstanova.Entity.Sales;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class SalesService {

    @Autowired
    private SalesRepository salesRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public List<Sales> getSales() {
        return salesRepository.findAll();
    }

    public Sales getSaleById(int id) {
        return salesRepository.findById(id).orElse(null);
    }

    public Sales addSale(SalesDto salesDto) {
        // Validate that employeeId is not null or invalid
        if (salesDto.getEmployeeId() == 0) {
            throw new IllegalArgumentException("Employee ID must not be null or invalid");
        }

        // Fetch the employee from the repository using the employeeId
        Employees cashier = employeeRepository.findById(salesDto.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Cashier not found"));

        // Convert Employees entity to EmployeeDto (if needed)
        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setId(cashier.getId());
        employeeDto.setName(cashier.getName());
        employeeDto.setSurname(cashier.getSurname());
        employeeDto.setEmail(cashier.getEmail());
        employeeDto.setMobile(cashier.getMobile());
        salesDto.setEmployeeDto(employeeDto);

        // Proceed with creating the Sales object
        Sales sales = new Sales();
        sales.setTotalPrice(salesDto.getTotalPrice());
        sales.setChange(salesDto.getReceiptChange());
        sales.setTransactionDate(salesDto.getTransactionDate());
        sales.setTransactionTime(salesDto.getTransactionTime());
        sales.setPaymentType(salesDto.getPaymentType());
        sales.setCashier(cashier); // Set the Employees entity

        // Save the sale to the database
        Sales savedSale = salesRepository.save(sales);
        salesRepository.flush(); // Force write to the database

        return savedSale;
    }

    public List<Sales> getSalesByDate(LocalDate date) {
        return salesRepository.findByTransactionDate(date);
    }

}
