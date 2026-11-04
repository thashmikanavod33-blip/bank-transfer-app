package com.example.bank_transfer_app.controller;

import com.example.bank_transfer_app.dto.TransferRequest;
import com.example.bank_transfer_app.model.BankTransaction;
import com.example.bank_transfer_app.service.BankService;
import com.example.bank_transfer_app.service.TransactionAuditService;
import com.example.bank_transfer_app.exception.AccountNotFoundException;
import com.example.bank_transfer_app.exception.InsufficientBalanceException;
import com.example.bank_transfer_app.exception.InvalidTransferException;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController


public class BankController {

    private final BankService bankService;
    private final TransactionAuditService auditService;

    public BankController(BankService bankService,
                          TransactionAuditService auditService) {
        this.bankService = bankService;
        this.auditService = auditService;
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(
            @Valid @RequestBody TransferRequest request) {

        try {
            BankTransaction transaction =
                    bankService.transfer(request);

            return ResponseEntity.ok(transaction);

        } catch (AccountNotFoundException |
                 InsufficientBalanceException |
                 InvalidTransferException ex) {

            auditService.saveFailedTransaction(
                    request.getFromAccount(),
                    request.getToAccount(),
                    request.getAmount(),
                    ex.getMessage()
            );

            return ResponseEntity.badRequest()
                    .body(ex.getMessage());
        }
    }
}

