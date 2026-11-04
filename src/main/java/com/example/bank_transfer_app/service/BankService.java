package com.example.bank_transfer_app.service;


import com.example.bank_transfer_app.model.Customer;
import com.example.bank_transfer_app.model.BankTransaction;
import com.example.bank_transfer_app.model.TransactionStatus;
import com.example.bank_transfer_app.repository.CustomerRepository;
import com.example.bank_transfer_app.repository.BankTransactionRepository;
import com.example.bank_transfer_app.exception.AccountNotFoundException;
import com.example.bank_transfer_app.exception.InsufficientBalanceException;
import com.example.bank_transfer_app.exception.InvalidTransferException;
import com.example.bank_transfer_app.dto.TransferRequest;
import org.springframework.transaction.annotation.Propagation;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class BankService {

    private final CustomerRepository customerRepository;
    private final BankTransactionRepository transactionRepository;
    private final TransactionAuditService auditService;

    public BankService(CustomerRepository customerRepository,
                       BankTransactionRepository transactionRepository,
                       TransactionAuditService auditService) {
        this.customerRepository = customerRepository;
        this.transactionRepository = transactionRepository;
        this.auditService = auditService;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public BankTransaction transfer(TransferRequest request) {

        if (request.getFromAccount().equals(request.getToAccount())) {
            throw new InvalidTransferException(
                    "Sender and receiver accounts must be different");
        }

        BigDecimal amount = request.getAmount();

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransferException(
                    "Transfer amount must be greater than zero");
        }

        Customer sender = customerRepository
                .getByAccountNumber(request.getFromAccount())
                .orElseThrow(() -> new AccountNotFoundException(
                        "Sender account not found"));

        Customer receiver = customerRepository
                .getByAccountNumber(request.getToAccount())
                .orElseThrow(() -> new AccountNotFoundException(
                        "Receiver account not found"));

        if (sender.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException(
                    "Insufficient account balance");
        }

        int debitResult = customerRepository.debit(
                request.getFromAccount(), amount);

        if (debitResult == 0) {
            throw new InsufficientBalanceException(
                    "Unable to debit the account");
        }

        int creditResult = customerRepository.credit(
                request.getToAccount(), amount);

        if (creditResult == 0) {
            throw new AccountNotFoundException(
                    "Receiver account not found");
        }

        BankTransaction transaction = new BankTransaction();
        transaction.setFromAccount(request.getFromAccount());
        transaction.setToAccount(request.getToAccount());
        transaction.setAmount(amount);
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setDescription("Bank transfer");

        return transactionRepository.save(transaction);
    }
}

