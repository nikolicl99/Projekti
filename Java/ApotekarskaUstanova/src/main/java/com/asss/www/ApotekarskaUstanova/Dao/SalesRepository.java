package com.asss.www.ApotekarskaUstanova.Dao;


import com.asss.www.ApotekarskaUstanova.Entity.Sales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesRepository extends JpaRepository<Sales, Integer> {
}
