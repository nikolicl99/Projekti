package com.lnikolic.www.ParkEase.Service;

import com.lnikolic.www.ParkEase.DAO.UserRepository;
import com.lnikolic.www.ParkEase.DTO.User.UserCreateDTO;
import com.lnikolic.www.ParkEase.DTO.User.UserResponseDTO;
import com.lnikolic.www.ParkEase.DTO.User.UserUpdateDTO;
import com.lnikolic.www.ParkEase.Entity.User;
import com.lnikolic.www.ParkEase.Mapper.UserMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// @Service označava ovu klasu kao Spring Service komponentu
// Spring će automatski detektovati i upravljati ovom klasom
// Service sloj sadrži poslovnu logiku aplikacije
@Service
// @RequiredArgsConstructor je Lombok anotacija koja generiše konstruktor
// koji injektuje sve final polja (userRepository i userMapper)
// To eliminiše potrebu za ručnim pisanjem @Autowired ili konstruktora
@RequiredArgsConstructor
public class UserService {

    // Final polje za repozitorijum - Spring će automatski injektovati instancu
    // UserRepository je interfejs koji extenda JpaRepository
    // Omogućava CRUD operacije nad User entitetima u bazi podataka
    private final UserRepository userRepository;

    // Final polje za MapStruct mapper - Spring će injektovati generisanu implementaciju
    // UserMapper je interfejs, MapStruct na compile-time generiše UserMapperImpl
    // KomponentModel = "spring" u @Mapper anotaciji pravi Spring bean od mapper-a
    @Autowired
    private final UserMapper userMapper;

    // ================ METODA 1: Dobavljanje svih korisnika ================

    /**
     * Vraća listu svih korisnika iz baze podataka
     * Lista je sortirana po ID-u u rastućem redosledu
     *
     * @return Lista UserResponseDTO objekata - pogodna za vraćanje klijentu (REST API)
     *
     * KORACI:
     * 1. Dobavi sve User ENTITETE iz baze (List<User>)
     * 2. Konvertuj List<User> u List<UserResponseDTO> koristeći mapper
     * 3. Vrati listu DTO objekata klijentu
     */
    public List<UserResponseDTO> getAllUsers() {
        // Poziv repozitorijuma da dobavi sve usere sortirane po ID
        // Ovo vraća listu ENTITETA (JPA objekata sa svim poljima)
        List<User> users = userRepository.findAllByOrderByIdAsc();

        // MapStruct mapper konvertuje List<User> u List<UserResponseDTO>
        // Ovo radi automatsko mapiranje polja sa istim imenom
        // VAŽNO: Password polje se NE mapira (ostaje null u DTO)
        return userMapper.toResponseDTOList(users);
    }

    // ================ METODA 2: Dobavljanje korisnika po ID-u ================

    /**
     * Pronalazi korisnika po ID-u i vraća ga kao DTO
     *
     * @param id - ID korisnika koji se traži
     * @return UserResponseDTO - DTO reprezentacija korisnika
     * @throws RuntimeException ako korisnik nije pronađen
     *
     * KORACI:
     * 1. Pronađi User entitet po ID-u (Optional<User>)
     * 2. Proveri da li postoji, ako ne baci izuzetak
     * 3. Konvertuj entitet u DTO koristeći mapper
     */
    public UserResponseDTO getUserById(Long id) {
        // findById vraća Optional<User> - kontejner koji može sadržati User ili biti prazan
        User user = userRepository.findById(id)
                // Ako Optional je prazan, baci izuzetak sa custom porukom
                // orElseThrow traži Supplier (lambda) koji vraća izuzetak
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Konvertuj pronađeni User entitet u UserResponseDTO
        // Mapper će kopirati sva odgovarajuća polja (osim password)
        return userMapper.toResponseDTO(user);
    }

    // ================ METODA 3: Kreiranje novog korisnika ================

    /**
     * Kreira novog korisnika u sistemu
     *
     * @param createDTO - DTO sa podacima za kreiranje korisnika (iz HTTP request body)
     * @return UserResponseDTO - DTO sa kreiranim korisnikom (bez password polja)
     *
     * KORACI:
     * 1. Validira DTO (automatki sa @Valid) - proverava @NotNull, @Email, itd.
     * 2. Proveri da li email već postoji u bazi (unique constraint)
     * 3. Konvertuj DTO u Entity koristeći mapper
     * 4. Sačuvaj Entity u bazu
     * 5. Konvertuj sačuvani Entity nazad u ResponseDTO
     * 6. Vrati ResponseDTO klijentu
     */
    @Transactional  // Ova anotacija pravi transakciju nad bazom
    // Sva čitanja i pisanja u ovoj metodi su u jednoj transakciji
    // Ako se desi izuzetak, sve promene se rollback-uju
    public UserResponseDTO createUser(@Valid UserCreateDTO createDTO) {
        // ===== KORAK 1: PROVERA UNIKATNOSTI EMAIL-A =====
        // Proveri da li već postoji korisnik sa ovim email-om
        // Ovo je dodatna biznis logika pored JPA @UniqueConstraint
        if (userRepository.existsByEmail(createDTO.getEmail())) {
            throw new RuntimeException("Email already exists: " + createDTO.getEmail());
        }

        // ===== KORAK 2: KONVERZIJA DTO → ENTITY =====
        // MapStruct mapper kreira novi User entitet iz DTO-a
        // MAPIRANJA KOJA SE DEŠAVAJU:
        // - Polja sa istim imenom (firstName, lastName, email) se kopiraju
        // - Password se kopira (DTO.password → Entity.password)
        // - ID se NE postavlja (ignore = true) - baza će generisati
        // - Role se postavlja na "USER" (constant = "USER")
        // - Status se postavlja na "ACTIVE" (constant = "ACTIVE")
        // - Balance se postavlja na 0.0 (constant = "0.0")
        User user = userMapper.toEntity(createDTO);

        // ===== KORAK 3: SAČUVAVANJE U BAZU =====
        // repository.save() radi INSERT u bazu
        // Hibernate automatski generiše ID (auto-increment)
        // Hibernate postavlja createdAt i updatedAt timestamp-ove
        // @PrePersist anotacija u User entitetu će se pozvati
        User savedUser = userRepository.save(user);

        // ===== KORAK 4: KONVERZIJA ENTITY → RESPONSE DTO =====
        // Konvertuj sačuvani entitet nazad u DTO za vraćanje klijentu
        // OVO JE VAŽNO: nikad ne vraćajte Entity direktno iz REST API-ja!
        // ResponseDTO NE sadrži password, role može biti drugačije formatirano, itd.
        return userMapper.toResponseDTO(savedUser);
    }

    // ================ METODA 4: Ažuriranje postojećeg korisnika ================

    /**
     * Ažurira postojećeg korisnika (PATCH semantika)
     *
     * @param id - ID korisnika koji se ažurira
     * @param updateDTO - DTO sa poljima koja treba ažurirati
     * @return UserResponseDTO - DTO sa ažuriranim korisnikom
     *
     * VAŽNO: Ovo je PATCH a ne PUT!
     * - Menjaju se SAMO polja koja su poslata u DTO
     * - Polja koja nisu poslata ostaju nepromenjena
     * - Mapper koristi NullValuePropertyMappingStrategy.IGNORE
     */
    @Transactional
    public UserResponseDTO updateUser(Long id, @Valid UserUpdateDTO updateDTO) {
        // ===== KORAK 1: DOBAVI POSTOJEĆI ENTITET =====
        // Pronađi korisnika koji se ažurira
        // Ako ne postoji, baci izuzetak
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // ===== KORAK 2: PARCIJALNO AŽURIRANJE ENTITETA =====
        // userMapper.updateEntityFromDTO() radi PARCIJALNI update
        // ŠTA SE DEŠAVA:
        // - Samo polja koja NISU null u DTO se kopiraju u Entity
        // - Polja koja SU null u DTO se ignorišu (ne overwrite-uju postojeće)
        // - ID, role, status, balance se ignorišu (ignore = true u mapperu)
        // - @MappingTarget znači da se ažurira postojeći objekat
        userMapper.updateEntityFromDTO(updateDTO, user);

        // ===== KORAK 3: SAČUVAJ PROMENE =====
        // repository.save() ovde radi UPDATE (jer entitet već ima ID)
        // Hibernate će automatski ažurirati updatedAt polje
        // @PreUpdate anotacija u User entitetu će se pozvati
        User updatedUser = userRepository.save(user);

        // ===== KORAK 4: KONVERZIJA U RESPONSE DTO =====
        return userMapper.toResponseDTO(updatedUser);
    }

    // ================ METODA 5: Brisanje korisnika ================

    /**
     * Briše korisnika iz baze podataka
     *
     * @param id - ID korisnika koji se briše
     * @throws RuntimeException ako korisnik nije pronađen
     *
     * VAŽNO: Ovo je fizičko brisanje (DELETE FROM users)
     * Za soft delete, koristiti status = "DELETED"
     */
    @Transactional
    public void deleteUser(Long id) {
        // ===== KORAK 1: PROVERA POSTOJANJA =====
        // Proveri da li korisnik postoji pre brisanja
        // Ovo nije obavezno (deleteById ne baca izuzetak ako ne postoji)
        // ali je dobra praksa za bolje error poruke
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }

        // ===== KORAK 2: BRISANJE =====
        // repository.deleteById() radi DELETE FROM users WHERE id = ?
        userRepository.deleteById(id);
    }

    // ================ METODA 6: Interna metoda za dobavljanje Entity ================

    /**
     * Interna metoda za dobavljanje User ENTITETA po ID-u
     * Koristi se u drugim servisima koji treba da rade operacije nad User entitetom
     *
     * @param id - ID korisnika
     * @return User entitet (sa svim poljima uključujući password)
     *
     * VAŽNO: Ova metoda vraća Entity, ne DTO!
     * Koristiti samo unutar aplikacije, nikad za REST API odgovor
     */
    public User getUserEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }
}