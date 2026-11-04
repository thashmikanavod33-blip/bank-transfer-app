package com.example.bank_transfer_app.config;

import com.example.bank_transfer_app.model.Customer;
import com.example.bank_transfer_app.repository.CustomerRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration

public class DataLoader {

    @Bean
    CommandLineRunner loadData(CustomerRepository repository) {

        return args -> {

            if (repository.count() == 0) {

                Customer kamal = new Customer();
                kamal.setAccountNumber("100001");
                kamal.setFullName("Kamal");
                kamal.setEmail("kamal@gmail.com");
                kamal.setBalance(new BigDecimal("50000.00"));

                Customer nimali = new Customer();
                nimali.setAccountNumber("100002");
                nimali.setFullName("Nimali");
                nimali.setEmail("nimali@gmail.com");
                nimali.setBalance(new BigDecimal("25000.00"));

                Customer saman = new Customer();
                saman.setAccountNumber("100003");
                saman.setFullName("Saman");
                saman.setEmail("saman@gmail.com");
                saman.setBalance(new BigDecimal("10000.00"));

                repository.save(kamal);
                repository.save(nimali);
                repository.save(saman);

                System.out.println("Sample Data Loaded Successfully!");
            }
        };
    }
}