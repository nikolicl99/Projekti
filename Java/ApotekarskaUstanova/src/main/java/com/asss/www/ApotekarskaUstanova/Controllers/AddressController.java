package com.asss.www.ApotekarskaUstanova.Controllers;

import com.asss.www.ApotekarskaUstanova.Entity.Address;
import com.asss.www.ApotekarskaUstanova.Service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collections;

@RestController
@RequestMapping("api/address")
public class AddressController {

    @Autowired
    AddressService addressService;

    @PostMapping("/add")
    public ResponseEntity<?> addAddress(@RequestBody @Valid Address address) {
        System.out.println("Primljena adresa: " + address);

        Address savedAddress = addressService.addAddress(address);
        if (savedAddress != null && savedAddress.getId() != -1) {
//            return new ResponseEntity<>("id: " + savedAddress.getId(), HttpStatus.OK);
            return ResponseEntity.ok(Collections.singletonMap("id", savedAddress.getId()));
        } else {
            return new ResponseEntity<>("Address could not be added", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
