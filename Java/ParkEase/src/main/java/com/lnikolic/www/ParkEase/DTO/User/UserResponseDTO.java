package com.lnikolic.www.ParkEase.DTO.User;

import com.lnikolic.www.ParkEase.Entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data                    // Lombok: getteri, setteri
@Builder                 // Builder pattern
@NoArgsConstructor       // Za deserijalizaciju (Jackson)
@AllArgsConstructor      // Za builder
public class UserResponseDTO {
    private Long id;
    private String name;
    private String surname;
    private String phone;
    private String email;
    private User.UserRole role;
    private User.UserStatus status;
    private Double balance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
