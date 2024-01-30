package com.asss.pj.dao;

import com.asss.pj.entity.Vinarija;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VinarijaRepository extends JpaRepository<Vinarija, Integer> {

}


