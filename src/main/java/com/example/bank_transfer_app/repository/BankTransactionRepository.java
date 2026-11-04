package com.example.bank_transfer_app.repository;

import com.example.bank_transfer_app.model.BankTransaction;
import com.example.bank_transfer_app.model.TransactionStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BankTransactionRepository
        extends JpaRepository<BankTransaction, Long> {

    // 1. Find transactions by sender account
    @Query("SELECT t FROM BankTransaction t WHERE t.fromAccount = :accountNumber")
    List<BankTransaction> findByFromAccount(
            @Param("accountNumber") String accountNumber);

    // 2. Find transactions by receiver account
    @Query("SELECT t FROM BankTransaction t WHERE t.toAccount = :accountNumber")
    List<BankTransaction> findByToAccount(
            @Param("accountNumber") String accountNumber);

    // 3. Find transactions by status
    @Query("SELECT t FROM BankTransaction t WHERE t.status = :status")
    List<BankTransaction> findByStatus(
            @Param("status") TransactionStatus status);

    // 4. Get all transactions ordered by date
    @Query("SELECT t FROM BankTransaction t ORDER BY t.createdAt DESC")
    List<BankTransaction> findAllOrderedByDate();
}

