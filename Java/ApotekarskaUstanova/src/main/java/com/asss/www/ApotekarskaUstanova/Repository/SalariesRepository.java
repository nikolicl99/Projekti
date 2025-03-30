package com.asss.www.ApotekarskaUstanova.Repository;

import com.asss.www.ApotekarskaUstanova.Entity.Salaries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SalariesRepository extends JpaRepository<Salaries, Integer> {
        @Query("SELECT DISTINCT CONCAT(s.month, '-', s.year) FROM Salaries s")
        List<String> findUniqueMonthsAndYears();

        @Query("SELECT s FROM Salaries s WHERE s.month = :month AND s.year = :year")
        List<Salaries> findByMonthAndYear(@Param("month") String month, @Param("year") int year);
}