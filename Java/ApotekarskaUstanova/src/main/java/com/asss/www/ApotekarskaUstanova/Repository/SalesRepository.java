package com.asss.www.ApotekarskaUstanova.Repository;


import com.asss.www.ApotekarskaUstanova.Entity.Sales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SalesRepository extends JpaRepository<Sales, Integer> {
    List<Sales> findAll();
    Optional<Sales> findById(int id);
    List<Sales> findByTransactionDate(LocalDate transactionDate);
}
