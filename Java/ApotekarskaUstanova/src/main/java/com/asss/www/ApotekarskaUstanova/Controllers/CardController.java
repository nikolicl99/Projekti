package com.asss.www.ApotekarskaUstanova.Controllers;

import com.asss.www.ApotekarskaUstanova.Service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/cards")
public class CardController {

    @Autowired
    private CardService cardService;

    @PostMapping("/validate")
    public ResponseEntity<String> validateCard(@RequestBody Map<String, String> request) {
        String cardNumber = request.get("cardNumber");
        String pin = request.get("pin");

        if (cardNumber == null || pin == null || !cardNumber.matches("\\d{16}") || !pin.matches("\\d{4}")) {
            return ResponseEntity.badRequest().body("Neispravan unos podataka.");
        }

        if (cardService.validateCard(cardNumber, pin)) {
            return ResponseEntity.ok("Kartica validna, plaćanje uspešno.");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Neispravan broj kartice ili PIN.");
        }
    }
}
