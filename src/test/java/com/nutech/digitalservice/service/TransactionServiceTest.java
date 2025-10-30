package com.nutech.digitalservice.service;

import com.nutech.digitalservice.dto.TransactionHistoryResponse;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionServiceTest {

    @Test
    public void testTransactionHistoryResponseStructure() {
        // Create a mock transaction history response
        TransactionHistoryResponse response = TransactionHistoryResponse.builder()
                .offset("0")
                .limit("3")
                .records(Arrays.asList())
                .build();

        // Test the structure
        assertNotNull(response);
        assertEquals("0", response.getOffset());
        assertEquals("3", response.getLimit());
        assertNotNull(response.getRecords());
        assertTrue(response.getRecords().isEmpty());

        System.out.println("TransactionHistoryResponse structure test passed!");
        System.out.println("Response: {\"offset\":\"" + response.getOffset() +
                          "\",\"limit\":\"" + response.getLimit() +
                          "\",\"records\":" + response.getRecords() + "}");
    }
}