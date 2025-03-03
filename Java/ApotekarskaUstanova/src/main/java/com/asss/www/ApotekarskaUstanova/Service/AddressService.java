package com.asss.www.ApotekarskaUstanova.Service;

import com.asss.www.ApotekarskaUstanova.Dao.AddressRepository;
import com.asss.www.ApotekarskaUstanova.Entity.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;

    public Address addAddress(Address address) {
        Address newAddress = new Address();
        newAddress.setTown(address.getTown());
        newAddress.setAddress(address.getAddress());
        newAddress.setNumber(address.getNumber());
        newAddress.setAptNumber(address.getAptNumber());

        return addressRepository.save(newAddress); // Vraćamo sačuvanu adresu
    }

}
