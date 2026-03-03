package com.lnikolic.www.ParkEase.DTO.User;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {

    @Size(min = 2, max = 50, message = "Ime mora imati između 2 i 50 karaktera")
    private String name;

    @Size(min = 2, max = 50, message = "Prezime mora imati između 2 i 50 karaktera")
    private String surname;

    @Size(min = 9, max = 15, message = "Telefon mora imati između 9 i 15 cifara")
    @Pattern(regexp = "^[0-9+\\s-]*$", message = "Telefon sadrži nedozvoljene karaktere")
    private String phone;

    @Email(message = "Email mora biti validan")
    private String email;

    @Size(min = 6, message = "Lozinka mora imati najmanje 6 karaktera")
    private String password;
}
