package com.nutech.digitalservice.repository;

import com.nutech.digitalservice.entity.ServiceEntity;
import com.nutech.digitalservice.entity.Transaction;
import com.nutech.digitalservice.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class TransactionRepositoryCustomImpl implements TransactionRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Transaction> findTransactionHistoryByUser(User user, Integer limit, Integer offset) {
        StringBuilder sql = new StringBuilder(
                "SELECT t.id, t.invoice_number, t.transaction_type, t.service_code, " +
                "t.description, t.total_amount, t.created_on " +
                "FROM transactions t " +
                "WHERE t.user_id = ? " +
                "ORDER BY t.created_on DESC"
        );

        if (limit != null && limit > 0) {
            sql.append(" LIMIT ?");
        }

        if (offset != null && offset >= 0) {
            sql.append(" OFFSET ?");
        }

        try {
            jakarta.persistence.Query query = entityManager.createNativeQuery(sql.toString())
                    .setParameter(1, user.getId());

            if (limit != null && limit > 0) {
                query.setParameter(2, limit);
                if (offset != null && offset >= 0) {
                    query.setParameter(3, offset);
                }
            } else if (offset != null && offset >= 0) {
                query.setParameter(2, offset);
            }

            List<Object[]> results = query.getResultList();
            List<Transaction> transactions = new ArrayList<>();

            for (Object[] result : results) {
                Transaction transaction = new Transaction();
                transaction.setId(((Number) result[0]).longValue());
                transaction.setInvoiceNumber((String) result[1]);
                transaction.setTransactionType(Transaction.TransactionType.valueOf((String) result[2]));
                transaction.setServiceCode((String) result[3]);
                transaction.setDescription((String) result[4]);
                transaction.setTotalAmount(((Number) result[5]).longValue());
                transaction.setCreatedOn(((Timestamp) result[6]).toLocalDateTime().toInstant(java.time.ZoneOffset.UTC));
                transaction.setUser(user);
                transactions.add(transaction);
            }

            return transactions;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public Transaction insertTransactionWithRawQuery(User user, String invoiceNumber, String transactionType,
                                                    String serviceCode, String description, Long totalAmount) {
        String sql = "INSERT INTO transactions (user_id, invoice_number, transaction_type, service_code, " +
                    "description, total_amount, created_on) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                    "RETURNING id, invoice_number, transaction_type, service_code, description, total_amount, created_on";

        Object[] result = (Object[]) entityManager.createNativeQuery(sql)
                .setParameter(1, user.getId())
                .setParameter(2, invoiceNumber)
                .setParameter(3, transactionType)
                .setParameter(4, serviceCode)
                .setParameter(5, description)
                .setParameter(6, totalAmount)
                .setParameter(7, Timestamp.valueOf(LocalDateTime.now()))
                .getSingleResult();

        Transaction transaction = new Transaction();
        transaction.setId(((Number) result[0]).longValue());
        transaction.setInvoiceNumber((String) result[1]);
        transaction.setTransactionType(Transaction.TransactionType.valueOf((String) result[2]));
        transaction.setServiceCode((String) result[3]);
        transaction.setDescription((String) result[4]);
        transaction.setTotalAmount(((Number) result[5]).longValue());
        transaction.setCreatedOn(((Timestamp) result[6]).toLocalDateTime().toInstant(java.time.ZoneOffset.UTC));
        transaction.setUser(user);

        return transaction;
    }

    @Override
    public String generateInvoiceNumber() {
        String datePattern = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // Query the database to get the latest sequence number for today
        // Using REGEXP_REPLACE to extract the numeric part after the date
        String sql = "SELECT MAX(CAST(REGEXP_REPLACE(invoice_number, 'INV" + datePattern + "-', '') AS INTEGER)) " +
                    "FROM transactions " +
                    "WHERE invoice_number LIKE ?";

        try {
            Object result = entityManager.createNativeQuery(sql)
                    .setParameter(1, "INV" + datePattern + "-%")
                    .getSingleResult();

            int maxSequence = result != null ? ((Number) result).intValue() : 0;
            int nextSequence = maxSequence + 1;

            return String.format("INV%s-%06d", datePattern, nextSequence);
        } catch (Exception e) {
            // If query fails, fallback to a simple sequence starting from 1
            return String.format("INV%s-%06d", datePattern, 1);
        }
    }

    @Override
    public Long getCurrentBalance(User user) {
        String sql = "SELECT balance FROM balances WHERE user_id = ?";
        try {
            Object result = entityManager.createNativeQuery(sql)
                    .setParameter(1, user.getId())
                    .getSingleResult();
            return ((Number) result).longValue();
        } catch (Exception e) {
            // Return 0L if no balance record found or any other error
            return 0L;
        }
    }

    @Override
    public void updateBalanceWithRawQuery(User user, Long newBalance) {
        String sql = "UPDATE balances SET balance = ?, updated_at = ? WHERE user_id = ?";
        entityManager.createNativeQuery(sql)
                .setParameter(1, newBalance)
                .setParameter(2, Timestamp.valueOf(LocalDateTime.now()))
                .setParameter(3, user.getId())
                .executeUpdate();
    }

    @Override
    public Optional<String> getServiceNameByCode(String serviceCode) {
        String sql = "SELECT service_name FROM services WHERE service_code = ? AND active = true";
        try {
            String result = (String) entityManager.createNativeQuery(sql)
                    .setParameter(1, serviceCode)
                    .getSingleResult();
            return Optional.of(result);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Long> getServiceTariffByCode(String serviceCode) {
        String sql = "SELECT service_tariff FROM services WHERE service_code = ? AND active = true";
        try {
            Long result = ((Number) entityManager.createNativeQuery(sql)
                    .setParameter(1, serviceCode)
                    .getSingleResult()).longValue();
            return Optional.of(result);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}