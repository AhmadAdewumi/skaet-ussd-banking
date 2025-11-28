package com.skaet_assessment.repository;

import com.skaet_assessment.model.Transaction;
import com.skaet_assessment.model.Wallet;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    //-- get history by wallet
    List<Transaction> findByWalletOrderByCreatedAtDesc(Wallet wallet, Pageable pageable);
}
