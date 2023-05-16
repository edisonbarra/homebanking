package com.mindhub.homebanking;

import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootApplication
public class HomebankingApplication {
    @Autowired
    private PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(HomebankingApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(ClientRepository clientRepository,
                                      AccountRepository accountRepository,
                                      TransactionRepository transactionRepository,
                                      LoanRepository loanRepository,
                                      ClientLoanRepository clientLoanRepository,
                                      CardRepository cardRepository) {
        return (args) -> {

            // Creación del primer cliente Melba Morel y su save en el repositorio de cliente
            Client client1 = new Client("Melba", "Morel", "melba@mindhub.com", passwordEncoder.encode("111"));
            clientRepository.save(client1);

            //Creación de dos cuentas, para asociarlas al cliente Melba Morel, y finalmente agregarlas al repositorio de cuenta
            Account account1Client1 = new Account("VIN001", LocalDateTime.now(), 5000);
            Account account2Client1 = new Account("VIN002", LocalDateTime.now().plusDays(1), 7500);
            client1.addAccounts(account1Client1);
            client1.addAccounts(account2Client1);
            accountRepository.save(account1Client1);
            accountRepository.save(account2Client1);

            Transaction transaction1Cli1Cuenta1 = new Transaction(TransactionType.DEBIT, -50000, "Transferencia a 3ro", LocalDateTime.now());
            Transaction transaction2Cli1Cuenta1 = new Transaction(TransactionType.CREDIT, 75000, "Reembolso", LocalDateTime.now().plusHours(4));

            Transaction transaction1Cli1Cuenta2 = new Transaction(TransactionType.DEBIT, -40000, "Compra electrónicos", LocalDateTime.now());
            Transaction transaction2Cli1Cuenta2 = new Transaction(TransactionType.CREDIT, 85000, "Reembolso", LocalDateTime.now().plusHours(4));

            account1Client1.addTransaction(transaction1Cli1Cuenta1);
            account1Client1.addTransaction(transaction2Cli1Cuenta1);

            account2Client1.addTransaction(transaction1Cli1Cuenta2);
            account2Client1.addTransaction(transaction2Cli1Cuenta2);


            transactionRepository.save(transaction1Cli1Cuenta1);
            transactionRepository.save(transaction2Cli1Cuenta1);

            transactionRepository.save(transaction1Cli1Cuenta2);
            transactionRepository.save(transaction2Cli1Cuenta2);


            //Creación de un segundo cliente llamado John Doe, y su save en el repositorio cliente
            Client client2 = new Client("John", "Doe", "johndoe@mindhub.com", passwordEncoder.encode("222"));
            clientRepository.save(client2);

            //Creación de dos cuentas, para asociarlas al cliente John Doe, y finalmente agregarlas al repositorio de cuenta
            Account account1Client2 = new Account("VIN010", LocalDateTime.now(), 5001);
            Account account2Client2 = new Account("VIN020", LocalDateTime.now().plusDays(1), 7502);
            client2.addAccounts(account1Client2);
            client2.addAccounts(account2Client2);
            accountRepository.save(account1Client2);
            accountRepository.save(account2Client2);

            Transaction transaction1Cli2Cuenta1 = new Transaction(TransactionType.DEBIT, -30000, "Transferencia a 3ro", LocalDateTime.now());
            Transaction transaction2Cli2Cuenta1 = new Transaction(TransactionType.CREDIT, 85000, "Reembolso", LocalDateTime.now().plusHours(4));

            Transaction transaction1Cli2Cuenta2 = new Transaction(TransactionType.DEBIT, -40380, "Compra asado", LocalDateTime.now());
            Transaction transaction2Cli2Cuenta2 = new Transaction(TransactionType.CREDIT, 85000, "Pago cuota asado", LocalDateTime.now().plusHours(4));


            account1Client2.addTransaction(transaction1Cli2Cuenta1);
            account1Client2.addTransaction(transaction2Cli2Cuenta1);

            account2Client2.addTransaction(transaction1Cli2Cuenta2);
            account2Client2.addTransaction(transaction2Cli2Cuenta2);


            transactionRepository.save(transaction1Cli2Cuenta1);
            transactionRepository.save(transaction2Cli2Cuenta1);

            transactionRepository.save(transaction1Cli2Cuenta2);
            transactionRepository.save(transaction2Cli2Cuenta2);


            //Creación de un Cliente admin para los permisos de ADMIN
            Client admin = new Client("admin", "admin", "admin@mindhub.com", passwordEncoder.encode("777"));
            clientRepository.save(admin);


            //================================== PARTE 4 - CREACION DE LOANS y CLIENTLOAN  =======================================

            //Creación de 3 prestamos y guardarlos en la H2 por medio del repositorio
            Loan hipotecario = new Loan("Hipotecario", 500000, List.of(12, 24, 36, 48, 60));
            Loan personal = new Loan("Personal", 100000, List.of(6, 12, 24));
            Loan automotriz = new Loan("Automotriz", 300000, List.of(6, 12, 24, 36));
            loanRepository.save(hipotecario);
            loanRepository.save(personal);
            loanRepository.save(automotriz);

            ClientLoan clMelbaHipotecario = new ClientLoan(400000, 60, client1, hipotecario);
            ClientLoan clMelbaPersonal = new ClientLoan(50000, 12, client1, personal);

            ClientLoan clJohnPersonal = new ClientLoan(100000, 24, client2, personal);
            ClientLoan clJohnAutomotriz = new ClientLoan(200000, 36, client2, automotriz);

            clientLoanRepository.save(clMelbaHipotecario);
            clientLoanRepository.save(clMelbaPersonal);

            clientLoanRepository.save(clJohnPersonal);
            clientLoanRepository.save(clJohnAutomotriz);


            //================================== PARTE 5 - CREACION DE CARDS  =======================================
            Card goldMelba = new Card(client1.getFullName(), CardType.DEBIT, CardColor.GOLD, "2356-1238-7239-5955 ", 235, LocalDateTime.now(), LocalDateTime.now().plusYears(5), client1);
            Card titaniumMelba = new Card(client1.getFullName(), CardType.CREDIT, CardColor.TITANIUM, "1326-1838-7139-8955 ", 395, LocalDateTime.now(), LocalDateTime.now().plusYears(5), client1);

            Card silverJohn = new Card(client2.getFullName(), CardType.CREDIT, CardColor.SILVER, "2826-8836-7538-1958 ", 174, LocalDateTime.now(), LocalDateTime.now().plusYears(5), client2);

            cardRepository.save(goldMelba);
            cardRepository.save(titaniumMelba);
            cardRepository.save(silverJohn);


        };
    }

}
