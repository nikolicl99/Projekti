package com.lnikolic.www.ParkEase.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "surname", nullable = false)
    private String surname;

    @Column(name = "phone", unique = true)
    private String phone;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.USER;

    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.ACTIVE;

    @CreationTimestamp
    @Column(name = "createdAt", updatable = false)
    private LocalDateTime createdAt; // HIBERNATE POPUNJAVA AUTOMATSKI

    @UpdateTimestamp
    @Column(name = "updatedAt")
    private LocalDateTime updatedAt; // HIBERNATE POPUNJAVA AUTOMATSKI

    @Column(name = "balance")
    private Double balance = 0.0;

    public enum UserRole {
        USER, ADMIN, MANAGER, STAFF
    }

    public enum UserStatus {
        ACTIVE, INACTIVE, SUSPENDED, DELETED
    }
}