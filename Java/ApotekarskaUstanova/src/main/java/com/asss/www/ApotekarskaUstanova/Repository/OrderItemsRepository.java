package com.asss.www.ApotekarskaUstanova.Repository;

import com.asss.www.ApotekarskaUstanova.Entity.OrderItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemsRepository extends JpaRepository<OrderItems, Integer> {
        @Query("SELECT DISTINCT oi FROM OrderItems oi WHERE oi.orderId = :orderId")
        List<OrderItems> findDistinctByOrderId(@Param("orderId") Integer orderId);
}