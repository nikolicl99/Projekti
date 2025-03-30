package com.asss.www.ApotekarskaUstanova.Service;

import com.asss.www.ApotekarskaUstanova.Dto.SalaryDetailsDto;
import com.asss.www.ApotekarskaUstanova.Entity.SalaryDetails;
import com.asss.www.ApotekarskaUstanova.Repository.SalaryDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SalaryDetailsService {
    @Autowired
    private SalaryDetailsRepository salaryDetailsRepository;

    public void addSalaryDetails(SalaryDetailsDto salaryDetailsDto) {
        SalaryDetails salaryDetails = new SalaryDetails();
        salaryDetails.setSalaryId(salaryDetailsDto.getSalaryId());
        salaryDetails.setHoursWorked(salaryDetailsDto.getHoursWorked());
        salaryDetails.setOvertimeHours(salaryDetailsDto.getOvertimeHours());
        salaryDetails.setBonus(salaryDetailsDto.getBonus());
        salaryDetails.setDeductions(salaryDetailsDto.getDeductions());
        salaryDetails.setNotes(salaryDetailsDto.getNotes());

        salaryDetailsRepository.save(salaryDetails);
    }

    public SalaryDetailsDto getSalaryDetailsById(Integer salaryId) {
        return salaryDetailsRepository.findById(salaryId)
                .map(salaryDetails -> {
                    SalaryDetailsDto dto = new SalaryDetailsDto();
                    dto.setSalaryId(salaryDetails.getSalaryId());
                    dto.setHoursWorked(salaryDetails.getHoursWorked());
                    dto.setOvertimeHours(salaryDetails.getOvertimeHours());
                    dto.setBonus(salaryDetails.getBonus());
                    dto.setDeductions(salaryDetails.getDeductions());
                    dto.setNotes(salaryDetails.getNotes());
                    return dto;
                })
                .orElse(null);
    }
}