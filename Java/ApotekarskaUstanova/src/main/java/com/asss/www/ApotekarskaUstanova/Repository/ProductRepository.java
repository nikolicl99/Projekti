package com.asss.www.ApotekarskaUstanova.Repository;

import com.asss.www.ApotekarskaUstanova.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByName(String name);

    @Query("SELECT pb FROM Product pb WHERE pb.name LIKE %:name%")
    List<Product> findByProductName(@Param("name") String name);

    List<Product> findByNameStartingWith(String name);

    @Query("SELECT DISTINCT p.dosage FROM Product p WHERE p.name = :name")
    List<Integer> findDosagesByName(@Param("name") String name);

    Optional<Product> findById(int id);

    @Query("SELECT COALESCE(SUM(pb.quantityRemaining), 0) FROM ProductBatch pb WHERE pb.product.id = :productId")
    int sumRemainingQuantityByProductId(@Param("productId") int productId);
}
