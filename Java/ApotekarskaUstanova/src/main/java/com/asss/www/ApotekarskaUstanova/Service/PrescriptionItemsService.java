package com.asss.www.ApotekarskaUstanova.Service;

import com.asss.www.ApotekarskaUstanova.Dao.PrescriptionItemsRepository;
import com.asss.www.ApotekarskaUstanova.Entity.Prescription_Items;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PrescriptionItemsService {

    @Autowired
    private PrescriptionItemsRepository prescriptionItemRepository;

    public List<Prescription_Items> getItemsByPrescriptionId(int prescriptionId) {
        return prescriptionItemRepository.findByPrescriptionId(prescriptionId);
    }
}