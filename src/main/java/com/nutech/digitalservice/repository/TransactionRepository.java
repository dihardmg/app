package com.nutech.digitalservice.repository;

import com.nutech.digitalservice.entity.Transaction;
import com.nutech.digitalservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserOrderByCreatedOnDesc(User user);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.user = ?1")
    Long countByUser(User user);
}