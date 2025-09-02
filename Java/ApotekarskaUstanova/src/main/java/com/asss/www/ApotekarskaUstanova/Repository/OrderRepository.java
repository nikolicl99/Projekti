package com.asss.www.ApotekarskaUstanova.Repository;

import com.asss.www.ApotekarskaUstanova.Entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByAcquiredFalse();
}