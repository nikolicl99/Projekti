package com.asss.www.ApotekarskaUstanova.Repository;

import com.asss.www.ApotekarskaUstanova.Entity.SalesItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SalesItemRepository extends JpaRepository<SalesItem, Integer> {
    List<SalesItem> findBySalesId(int salesId);
    Optional<SalesItem> findById(int salesId);
}
