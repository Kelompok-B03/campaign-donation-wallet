package id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.dto;

import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.dto.WalletDTOs.*;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.model.Transaction.PaymentMethod;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.model.Transaction.TransactionType;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class WalletDTOsTest {

    @Test
    void testWalletBalanceDTO() {
        // Arrange
        Long userId = 1L;
        BigDecimal balance = new BigDecimal("1000.00");
        
        // Act - Builder
        WalletBalanceDTO dto = WalletBalanceDTO.builder()
                .userId(userId)
                .balance(balance)
                .build();
        
        // Assert
        assertEquals(userId, dto.getUserId());
        assertEquals(balance, dto.getBalance());
        
        // Act - Setters
        WalletBalanceDTO dto2 = new WalletBalanceDTO();
        dto2.setUserId(2L);
        dto2.setBalance(new BigDecimal("2000.00"));
        
        // Assert
        assertEquals(2L, dto2.getUserId());
        assertEquals(new BigDecimal("2000.00"), dto2.getBalance());
        
        // Test All Args Constructor
        WalletBalanceDTO dto3 = new WalletBalanceDTO(3L, new BigDecimal("3000.00"));
        assertEquals(3L, dto3.getUserId());
        assertEquals(new BigDecimal("3000.00"), dto3.getBalance());
    }
    
    @Test
    void testTransactionDTO() {
        // Arrange
        Long id = 1L;
        Long walletId = 100L;
        Long campaignId = 200L;
        BigDecimal amount = new BigDecimal("250.00");
        TransactionType type = TransactionType.DONATION;
        PaymentMethod paymentMethod = PaymentMethod.GOPAY;
        String paymentPhone = "081234567890";
        String description = "Test transaction";
        LocalDateTime timestamp = LocalDateTime.now();
        
        // Act - Builder
        TransactionDTO dto = TransactionDTO.builder()
                .id(id)
                .walletId(walletId)
                .campaignId(campaignId)
                .amount(amount)
                .type(type)
                .paymentMethod(paymentMethod)
                .paymentPhone(paymentPhone)
                .description(description)
                .timestamp(timestamp)
                .build();
        
        // Assert
        assertEquals(id, dto.getId());
        assertEquals(walletId, dto.getWalletId());
        assertEquals(campaignId, dto.getCampaignId());
        assertEquals(amount, dto.getAmount());
        assertEquals(type, dto.getType());
        assertEquals(paymentMethod, dto.getPaymentMethod());
        assertEquals(paymentPhone, dto.getPaymentPhone());
        assertEquals(description, dto.getDescription());
        assertEquals(timestamp, dto.getTimestamp());
        
        // Test setters and getters
        TransactionDTO dto2 = new TransactionDTO();
        dto2.setId(2L);
        dto2.setWalletId(201L);
        dto2.setCampaignId(301L);
        dto2.setAmount(new BigDecimal("350.00"));
        dto2.setType(TransactionType.TOP_UP);
        dto2.setPaymentMethod(PaymentMethod.DANA);
        dto2.setPaymentPhone("089876543210");
        dto2.setDescription("Another test");
        dto2.setTimestamp(timestamp);
        
        assertEquals(2L, dto2.getId());
        assertEquals(201L, dto2.getWalletId());
        assertEquals(301L, dto2.getCampaignId());
        assertEquals(new BigDecimal("350.00"), dto2.getAmount());
        assertEquals(TransactionType.TOP_UP, dto2.getType());
        assertEquals(PaymentMethod.DANA, dto2.getPaymentMethod());
        assertEquals("089876543210", dto2.getPaymentPhone());
        assertEquals("Another test", dto2.getDescription());
        assertEquals(timestamp, dto2.getTimestamp());
    }
    
    @Test
    void testTopUpRequestDTO() {
        // Arrange
        Long userId = 1L;
        BigDecimal amount = new BigDecimal("500.00");
        PaymentMethod paymentMethod = PaymentMethod.GOPAY;
        String paymentPhone = "081234567890";
        
        // Act - Builder
        TopUpRequestDTO dto = TopUpRequestDTO.builder()
                .userId(userId)
                .amount(amount)
                .paymentMethod(paymentMethod)
                .paymentPhone(paymentPhone)
                .build();
        
        // Assert
        assertEquals(userId, dto.getUserId());
        assertEquals(amount, dto.getAmount());
        assertEquals(paymentMethod, dto.getPaymentMethod());
        assertEquals(paymentPhone, dto.getPaymentPhone());
        
        // Test setters and getters
        TopUpRequestDTO dto2 = new TopUpRequestDTO();
        dto2.setUserId(2L);
        dto2.setAmount(new BigDecimal("600.00"));
        dto2.setPaymentMethod(PaymentMethod.DANA);
        dto2.setPaymentPhone("089876543210");
        
        assertEquals(2L, dto2.getUserId());
        assertEquals(new BigDecimal("600.00"), dto2.getAmount());
        assertEquals(PaymentMethod.DANA, dto2.getPaymentMethod());
        assertEquals("089876543210", dto2.getPaymentPhone());
    }
    
    @Test
    void testWithdrawalRequestDTO() {
        // Arrange
        Long userId = 1L;
        Long campaignId = 100L;
        
        // Act - Builder
        WithdrawalRequestDTO dto = WithdrawalRequestDTO.builder()
                .userId(userId)
                .campaignId(campaignId)
                .build();
        
        // Assert
        assertEquals(userId, dto.getUserId());
        assertEquals(campaignId, dto.getCampaignId());
        
        // Test setters and getters
        WithdrawalRequestDTO dto2 = new WithdrawalRequestDTO();
        dto2.setUserId(2L);
        dto2.setCampaignId(200L);
        
        assertEquals(2L, dto2.getUserId());
        assertEquals(200L, dto2.getCampaignId());
    }
}