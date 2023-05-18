package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.LoanApplicationDTO;
import com.mindhub.homebanking.dtos.LoanDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class LoanController {

    @Autowired
    ClientLoanRepository clientLoanRepository;
    @Autowired
    LoanRepository loanRepository;
    @Autowired
    ClientRepository clientRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    TransactionRepository transactionRepository;

    @Transactional
    @RequestMapping(value = "/loans", method = RequestMethod.POST)
    public ResponseEntity<Object> applyForLoan(Authentication authentication, @RequestBody LoanApplicationDTO loanApplicationDTO) {
        if (loanApplicationDTO.isEmpty()) {
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }
        Loan loan = loanRepository.findById(loanApplicationDTO.getLoanId());
        if (loan == null) {
            return new ResponseEntity<>("The loan doesn't exist", HttpStatus.FORBIDDEN);
        }
        if (loanApplicationDTO.getAmount() > loan.getMaxAmount()) {
            return new ResponseEntity<>("The requested loan amount exceeds the maximum limit", HttpStatus.FORBIDDEN);
        }
        if (!loan.getPayments().contains(loanApplicationDTO.getPayments())) {
            return new ResponseEntity<>("The number of payments selected is not available for the requested loan.", HttpStatus.FORBIDDEN);
        }
        if (!clientRepository.findByEmail(authentication.getName()).getAccounts().contains(accountRepository.findByNumber(loanApplicationDTO.getToAccountNumber()))) {
            return new ResponseEntity<>("the destination account doesn't belong to the authenticated client.", HttpStatus.FORBIDDEN);
        }

        //Save the association of the Client and the Loan in ClientLoan, and at the same time, add a 20% interest to the requested amount.
        Client client = clientRepository.findByEmail(authentication.getName());
        ClientLoan clientLoan = new ClientLoan(loanApplicationDTO.getAmount() * 1.2, loanApplicationDTO.getPayments(), client, loan);
        clientLoanRepository.save(clientLoan);


        //Create a transaction with the requested amount and save it.
        Transaction transaction = new Transaction(TransactionType.CREDIT, loanApplicationDTO.getAmount(), loan.getName() + " loan approved", LocalDateTime.now(), accountRepository.findByNumber(loanApplicationDTO.getToAccountNumber()));
        transactionRepository.save(transaction);

        //Update the balance of the Client who has requested the loan.
        Account account = accountRepository.findByNumber(loanApplicationDTO.getToAccountNumber());
        account.setBalance(account.getBalance() + loanApplicationDTO.getAmount());
        accountRepository.save(account);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping("/loans")
    public List<LoanDTO> getLoans() {
        return loanRepository.findAll().stream().map(LoanDTO::new).collect(toList());
    }

}
