package com.lnikolic.www.ParkEase.DTO.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDTO {

    @NotBlank(message = "Ime je obavezno")           // Validacija
    @Size(min = 2, max = 50)                         // Validacija
    private String name;

    @NotBlank(message = "Email je obavezan")
    @Email(message = "Email mora biti validan")      // Validacija email formata
    private String email;

    @NotBlank(message = "Lozinka je obavezna")
    @Size(min = 6, message = "Lozinka mora imati najmanje 6 karaktera")
    private String password;
}
