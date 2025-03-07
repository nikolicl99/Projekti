package com.asss.www.ApotekarskaUstanova.Service;

import com.asss.www.ApotekarskaUstanova.Dao.EmployeeRepository;
import com.asss.www.ApotekarskaUstanova.Dao.SalesRepository;
import com.asss.www.ApotekarskaUstanova.Dto.SalesDto;
import com.asss.www.ApotekarskaUstanova.Entity.Employees;
import com.asss.www.ApotekarskaUstanova.Entity.Sales;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SalesService {

    @Autowired
    private SalesRepository salesRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public Sales addSale(SalesDto salesDto) {
        Sales sales = new Sales();
        sales.setTotalPrice(salesDto.getTotalPrice());
        sales.setTransactionDate(salesDto.getTransaction_date());
        sales.setChange(salesDto.getReceipt_change());

        Employees cashier = employeeRepository.findById(salesDto.getCashier_id())
                .orElseThrow(() -> new RuntimeException("Cashier not found"));

        sales.setCashier(cashier);

        Sales savedSale = salesRepository.save(sales);
        salesRepository.flush(); // Forsira upis u bazu odmah

        return savedSale;
    }

}
