package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.CardRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.utils.CardUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Random;

@RestController
@RequestMapping("/api")
public class CardController {

    @Autowired
    CardRepository cardRepository;
    @Autowired
    ClientRepository clientRepository;



    @RequestMapping(path = "/clients/current/cards", method = RequestMethod.POST)
    public ResponseEntity<Object> createAccount(
            Authentication authentication,
            @RequestParam CardType cardType, @RequestParam CardColor cardColor){
        String userEmail = authentication.getName();
        Client client= clientRepository.findByEmail(userEmail);

        if (client.getCards().stream().filter(card -> card.getType().equals(cardType)).count() >= 3) {
            return new ResponseEntity<>("Maximum amount of card type exceeded",HttpStatus.FORBIDDEN);
        }
        Card card = new Card(client.getFullName(),
                cardType,cardColor,
                CardUtils.generateCardNumber(),
                CardUtils.generateCvv(),
                LocalDateTime.now(),
                LocalDateTime.now().plusYears(5),client);
        cardRepository.save(card);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}
