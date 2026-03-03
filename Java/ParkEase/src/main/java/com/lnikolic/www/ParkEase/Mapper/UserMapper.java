package com.lnikolic.www.ParkEase.Mapper;

import com.lnikolic.www.ParkEase.DTO.User.UserCreateDTO;
import com.lnikolic.www.ParkEase.DTO.User.UserResponseDTO;
import com.lnikolic.www.ParkEase.DTO.User.UserUpdateDTO;
import com.lnikolic.www.ParkEase.Entity.User;
import org.mapstruct.*;
import java.util.List;

/**
 * MAPPER INTERFACE - MapStruct generiše implementaciju automatski
 * Ova interface se ne implementira ručno, već MapStruct na compile-time
 * generiše UserMapperImpl klasu koja ima sve metode implementirane.
 */
@Mapper(
        // VAŽNO: componentModel = "spring" pravi Spring Bean od mapper-a
        // Omogućava @Autowired injection u Service klase
        componentModel = "spring",

        // Ignoriši polja koja postoje u source ali ne postoje u target
        // Bez ovoga bi dobili compile grešku za nepostojeća polja
        unmappedTargetPolicy = ReportingPolicy.IGNORE,

        // Kada je polje null u source, ignoriši ga u target
        // Sprečava overwrite postojećih vrednosti sa null
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserMapper {

    // ==================== 1. ENTITY → RESPONSE DTO ====================
    /**
     * Konvertuje User ENTITY (iz baze) u UserResponseDTO (za HTTP odgovor)
     *
     * KORISTI SE KADA:
     * - Prikazujete korisnika nakon registracije
     * - Vraćate korisnika na GET zahtev
     * - Vraćate korisnika nakon logina
     *
     * VAŽNO: Ova metoda NE mapira password polje!
     * Password ostaje sakriven u Entity, ne izlazi van.
     */
    UserResponseDTO toResponseDTO(User user);


    // ==================== 2. LISTA ENTITY → LISTA DTO ====================
    /**
     * Konvertuje LISTU User entity u LISTU UserResponseDTO
     *
     * KORISTI SE KADA:
     * - Prikazujete listu svih korisnika (npr. admin panel)
     * - Vraćate rezultate pretrage korisnika
     */
    List<UserResponseDTO> toResponseDTOList(List<User> users);


    // ==================== 3. CREATE DTO → ENTITY ====================
    /**
     * Konvertuje UserCreateDTO (iz HTTP zahteva) u User ENTITY (za bazu)
     *
     * KORISTI SE KADA:
     * - Kreiranje novog korisnika (registracija)
     *
     * @Mapping anotacije:
     * - ignore = true: MapStruct će ignorisati ova polja (neće ih mapirati)
     * - constant = "VALUE": Postavlja fiksnu vrednost bez obzira na input
     */
    @Mapping(target = "id", ignore = true)           // ID generiše baza (auto-increment)
    @Mapping(target = "role", constant = "USER")     // Novi korisnik je uvek USER role
    @Mapping(target = "status", constant = "ACTIVE") // Novi korisnik je automatski aktivan
    @Mapping(target = "balance", constant = "0.0")   // Početni balance je 0
    @Mapping(target = "createdAt", ignore = true)    // Hibernate će postaviti timestamp
    @Mapping(target = "updatedAt", ignore = true)    // Hibernate će postaviti timestamp
    User toEntity(UserCreateDTO dto);


    // ==================== 4. UPDATE DTO → ENTITY (PARCIJALNO) ====================
    /**
     * Parcijalno ažurira POSTOJEĆI User entity sa vrednostima iz UserUpdateDTO
     *
     * KORISTI SE KADA:
     * - Ažuriranje korisničkih podataka (npr. profil)
     * - Parcijalni update (PATCH) - menja se samo šta je poslato
     *
     * @MappingTarget: Označava da se ažurira postojeći objekat, ne kreira novi
     *
     * VAŽNO: Ova metoda NE overwrite-uje polja koja su ignore = true
     * Na primer, ako ne pošaljete password u DTO, stari password ostaje.
     */
    @Mapping(target = "id", ignore = true)           // ID se nikad ne menja
    @Mapping(target = "role", ignore = true)         // Role se menja posebnom operacijom
    @Mapping(target = "status", ignore = true)       // Status se menja posebnom operacijom
    @Mapping(target = "balance", ignore = true)      // Balance se menja posebnom operacijom
    @Mapping(target = "createdAt", ignore = true)    // Created se nikad ne menja
    @Mapping(target = "updatedAt", ignore = true)    // Updated će Hibernate postaviti
    void updateEntityFromDTO(UserUpdateDTO dto, @MappingTarget User user);

}