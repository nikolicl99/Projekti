package com.asss.www.ApotekarskaUstanova.Service;

import com.asss.www.ApotekarskaUstanova.Repository.CardRepository;
import com.asss.www.ApotekarskaUstanova.Entity.Card;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CardService {

    @Autowired
    private CardRepository cardRepository;

    public boolean validateCard(String cardNumber, String pin) {
        Optional<Card> card = cardRepository.findByCardNumber(cardNumber);
        return card.isPresent() && card.get().getPin().equals(pin);
    }
}
