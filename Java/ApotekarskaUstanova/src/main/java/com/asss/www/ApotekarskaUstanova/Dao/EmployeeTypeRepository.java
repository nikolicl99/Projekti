package com.asss.www.ApotekarskaUstanova.Dao;

import com.asss.www.ApotekarskaUstanova.Entity.Employee_Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EmployeeTypeRepository extends JpaRepository<Employee_Type, Integer> {
    @Query("SELECT e FROM Employee_Type e WHERE e.name = :name")
    Optional<Employee_Type> findByName(@Param("name") String name);

    Optional<Employee_Type> findById(int id);
}
