package id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.dto;

import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.dto.WalletDTOs.*;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.model.Transaction.PaymentMethod;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.model.Transaction.TransactionType;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class WalletDTOsTest {

    @Test
    void testWalletBalanceDTO() {
        UUID userId = UUID.randomUUID();
        BigDecimal balance = new BigDecimal("1000.00");
        
        WalletBalanceDTO dto = WalletBalanceDTO.builder()
                .userId(userId)
                .balance(balance)
                .build();
        
        assertEquals(userId, dto.getUserId());
        assertEquals(balance, dto.getBalance());
        
        WalletBalanceDTO dto2 = new WalletBalanceDTO();
        UUID userId2 = UUID.randomUUID();
        dto2.setUserId(userId2);
        dto2.setBalance(new BigDecimal("2000.00"));
        
        assertEquals(userId2, dto2.getUserId());
        assertEquals(new BigDecimal("2000.00"), dto2.getBalance());
        
        UUID userId3 = UUID.randomUUID();
        WalletBalanceDTO dto3 = new WalletBalanceDTO(userId3, new BigDecimal("3000.00"));
        assertEquals(userId3, dto3.getUserId());
        assertEquals(new BigDecimal("3000.00"), dto3.getBalance());
    }
    
    @Test
    void testTransactionDTO() {
        Long id = 1L;
        Long walletId = 100L;
        String campaignId = "campaign-200";
        BigDecimal amount = new BigDecimal("250.00");
        TransactionType type = TransactionType.DONATION;
        PaymentMethod paymentMethod = PaymentMethod.GOPAY;
        String paymentPhone = "081234567890";
        String description = "Test transaction";
        LocalDateTime timestamp = LocalDateTime.now();
        
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
        
        assertEquals(id, dto.getId());
        assertEquals(walletId, dto.getWalletId());
        assertEquals(campaignId, dto.getCampaignId());
        assertEquals(amount, dto.getAmount());
        assertEquals(type, dto.getType());
        assertEquals(paymentMethod, dto.getPaymentMethod());
        assertEquals(paymentPhone, dto.getPaymentPhone());
        assertEquals(description, dto.getDescription());
        assertEquals(timestamp, dto.getTimestamp());
        
        TransactionDTO dto2 = new TransactionDTO();
        dto2.setId(2L);
        dto2.setWalletId(201L);
        dto2.setCampaignId("campaign-301");
        dto2.setAmount(new BigDecimal("350.00"));
        dto2.setType(TransactionType.TOP_UP);
        dto2.setPaymentMethod(PaymentMethod.DANA);
        dto2.setPaymentPhone("089876543210");
        dto2.setDescription("Another test");
        dto2.setTimestamp(timestamp);
        
        assertEquals(2L, dto2.getId());
        assertEquals(201L, dto2.getWalletId());
        assertEquals("campaign-301", dto2.getCampaignId());
        assertEquals(new BigDecimal("350.00"), dto2.getAmount());
        assertEquals(TransactionType.TOP_UP, dto2.getType());
        assertEquals(PaymentMethod.DANA, dto2.getPaymentMethod());
        assertEquals("089876543210", dto2.getPaymentPhone());
        assertEquals("Another test", dto2.getDescription());
        assertEquals(timestamp, dto2.getTimestamp());
    }
    
    @Test
    void testTopUpRequestDTO() {
        UUID userId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("500.00");
        PaymentMethod paymentMethod = PaymentMethod.GOPAY;
        String paymentPhone = "081234567890";
        
        TopUpRequestDTO dto = TopUpRequestDTO.builder()
                .userId(userId)
                .amount(amount)
                .paymentMethod(paymentMethod)
                .paymentPhone(paymentPhone)
                .build();
        
        assertEquals(userId, dto.getUserId());
        assertEquals(amount, dto.getAmount());
        assertEquals(paymentMethod, dto.getPaymentMethod());
        assertEquals(paymentPhone, dto.getPaymentPhone());
        
        TopUpRequestDTO dto2 = new TopUpRequestDTO();
        UUID userId2 = UUID.randomUUID();
        dto2.setUserId(userId2);
        dto2.setAmount(new BigDecimal("600.00"));
        dto2.setPaymentMethod(PaymentMethod.DANA);
        dto2.setPaymentPhone("089876543210");
        
        assertEquals(userId2, dto2.getUserId());
        assertEquals(new BigDecimal("600.00"), dto2.getAmount());
        assertEquals(PaymentMethod.DANA, dto2.getPaymentMethod());
        assertEquals("089876543210", dto2.getPaymentPhone());
    }
    
    @Test
    void testWithdrawalRequestDTO() {
        UUID userId = UUID.randomUUID();
        String campaignId = "campaign-100";
        
        WithdrawalRequestDTO dto = WithdrawalRequestDTO.builder()
                .userId(userId)
                .campaignId(campaignId)
                .build();
        
        assertEquals(userId, dto.getUserId());
        assertEquals(campaignId, dto.getCampaignId());
        
        WithdrawalRequestDTO dto2 = new WithdrawalRequestDTO();
        UUID userId2 = UUID.randomUUID();
        String campaignId2 = "campaign-200";
        dto2.setUserId(userId2);
        dto2.setCampaignId(campaignId2);
        
        assertEquals(userId2, dto2.getUserId());
        assertEquals(campaignId2, dto2.getCampaignId());
    }
}