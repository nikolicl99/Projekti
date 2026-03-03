package com.lnikolic.www.ParkEase.DAO;

import com.lnikolic.www.ParkEase.Entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findAllByOrderByIdAsc();

    boolean existsByEmail(@NotBlank(message = "Email je obavezan") @Email(message = "Email mora biti validan") String email);
}
