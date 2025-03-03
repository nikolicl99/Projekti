package com.asss.www.ApotekarskaUstanova.Dao;

import com.asss.www.ApotekarskaUstanova.Entity.SalesItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesItemRepository extends JpaRepository<SalesItem, Integer> {
}
