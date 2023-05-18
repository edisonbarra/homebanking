package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.models.TransactionType;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;


@RestController
@RequestMapping("/api")
public class TransactionController {

    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    ClientRepository clientRepository;

    @Transactional
    @RequestMapping(path = "/transactions", method = RequestMethod.POST)
    public ResponseEntity<Object> transact(
            Authentication authentication,
            @RequestParam Double amount, @RequestParam String description,
            @RequestParam String fromAccountNumber, @RequestParam String toAccountNumber) {

        if (amount.toString().isEmpty() || amount.isNaN() || description.isEmpty() || fromAccountNumber.isEmpty() || toAccountNumber.isEmpty()) {
            return new ResponseEntity<>("Missing data or invalid data", HttpStatus.FORBIDDEN);
        }
        if (fromAccountNumber.equals(toAccountNumber)) {
            return new ResponseEntity<>("Both accounts are the same number", HttpStatus.FORBIDDEN);
        }
        Account sourceAccount = accountRepository.findByNumber(fromAccountNumber);

        if (sourceAccount == null) {
            return new ResponseEntity<>("Source account doesn't exist", HttpStatus.FORBIDDEN);
        }

        if (!sourceAccount.getClient().equals(clientRepository.findByEmail(authentication.getName()))) {
            return new ResponseEntity<>("The source account doesn't belong to the authenticated client.", HttpStatus.FORBIDDEN);
        }

        Account destinationAccount = accountRepository.findByNumber(toAccountNumber);
        if (destinationAccount == null) {
            return new ResponseEntity<>("Destination account doesn't exist", HttpStatus.FORBIDDEN);
        }

        if (sourceAccount.getBalance() < amount) {
            return new ResponseEntity<>("Insufficient balance in the source account.", HttpStatus.FORBIDDEN);
        }

        Transaction transactionSrc = new Transaction(TransactionType.DEBIT
                , -amount, description + " - " + destinationAccount.getNumber(), LocalDateTime.now(),sourceAccount);
        Transaction transactionDest = new Transaction(TransactionType.CREDIT,
                amount, description + " - " + sourceAccount.getNumber(), LocalDateTime.now(),destinationAccount);
        transactionRepository.save(transactionSrc);
        transactionRepository.save(transactionDest);

        sourceAccount.setBalance(sourceAccount.getBalance()-amount);
        destinationAccount.setBalance(destinationAccount.getBalance()+amount);
        accountRepository.save(sourceAccount);
        accountRepository.save(destinationAccount);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
