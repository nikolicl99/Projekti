package com.asss.www.ApotekarskaUstanova.Service;

import com.asss.www.ApotekarskaUstanova.Dao.EmployeeTypeRepository;
import com.asss.www.ApotekarskaUstanova.Dto.Employee_TypeDto;
import com.asss.www.ApotekarskaUstanova.Entity.Employee_Type;
import com.asss.www.ApotekarskaUstanova.Dto.Employee_TypeDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeTypeService {

    private final EmployeeTypeRepository employeeTypeRepository;

    public EmployeeTypeService(EmployeeTypeRepository employeeTypeRepository) {
        this.employeeTypeRepository = employeeTypeRepository;
    }

    // Dohvatanje svih tipova zaposlenih
    public List<Employee_Type> getAllEmployeeTypes() {
        return employeeTypeRepository.findAll();
    }

    // Dohvatanje ID-a na osnovu naziva
    public int getTypeIdByName(String typeName) {
        Optional<Employee_Type> employeeType = employeeTypeRepository.findByName(typeName);
        return employeeType.map(Employee_Type::getId).orElse(null); // Vraća null ako nije pronađeno
    }

    // Validacija JWT tokena (ova metoda će zavisiti od vaše implementacije JWT autentifikacije)
    public boolean isValidToken(String token) {
        // Ovde implementirajte validaciju tokena (npr. pomoću JWT token parsera)
        return true;
    }

    public boolean deleteEmployee(int id) {
        Optional<Employee_Type> employeeType = employeeTypeRepository.findById(id);
        if (employeeType.isPresent()) {
            employeeTypeRepository.delete(employeeType.get());
            return true;
        }
        return false;
    }

    // Dobavljanje role po ID-u
    public Employee_TypeDto getRoleById(int id) {
        Employee_Type role = employeeTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role with ID " + id + " not found"));
        return mapToDTO(role);
    }

    // Mapiranje entiteta u DTO
    private Employee_TypeDto mapToDTO(Employee_Type role) {
        Employee_TypeDto dto = new Employee_TypeDto();
        dto.setName(role.getName());
        dto.setCanAddEmployee(role.isCanAddEmployee());
        dto.setCanEditEmployee(role.isCanEditEmployee());
        dto.setCanManageInventory(role.isCanManageInventory());
        dto.setCanViewReports(role.isCanViewReports());
        dto.setCanGenerateReports(role.isCanGenerateReports());
        dto.setCanManageRoles(role.isCanManageRoles());
        return dto;
    }

    // Mapiranje DTO-a u entitet
    private Employee_Type mapToEntity(Employee_TypeDto dto) {
        Employee_Type role = new Employee_Type();
        role.setName(dto.getName());
        role.setCanAddEmployee(dto.isCanAddEmployee());
        role.setCanEditEmployee(dto.isCanEditEmployee());
        role.setCanManageInventory(dto.isCanManageInventory());
        role.setCanViewReports(dto.isCanViewReports());
        role.setCanGenerateReports(dto.isCanGenerateReports());
        role.setCanManageRoles(dto.isCanManageRoles());
        return role;
    }

    // Kreiranje nove role
    public Employee_TypeDto createRole(Employee_TypeDto roleDTO) {
        Employee_Type role = mapToEntity(roleDTO);
        Employee_Type savedRole = employeeTypeRepository.save(role);
        return mapToDTO(savedRole);
    }

    // Ažuriranje postojeće role
    public Employee_TypeDto updateRole(int id, Employee_TypeDto roleDTO) {
        Employee_Type existingRole = employeeTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role with ID " + id + " not found"));

        existingRole.setName(roleDTO.getName());
        existingRole.setCanAddEmployee(roleDTO.isCanAddEmployee());
        existingRole.setCanEditEmployee(roleDTO.isCanEditEmployee());
        existingRole.setCanManageInventory(roleDTO.isCanManageInventory());
        existingRole.setCanViewReports(roleDTO.isCanViewReports());
        existingRole.setCanGenerateReports(roleDTO.isCanGenerateReports());
        existingRole.setCanManageRoles(roleDTO.isCanManageRoles());

        Employee_Type updatedRole = employeeTypeRepository.save(existingRole);
        return mapToDTO(updatedRole);
    }
}
