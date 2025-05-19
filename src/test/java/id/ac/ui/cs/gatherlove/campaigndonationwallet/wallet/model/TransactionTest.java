package id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.model;

import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.model.Transaction;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    @Test
    void testTransactionCreation() {
        // Arrange
        Long id = 1L;
        Long walletId = 100L;
        Long campaignId = 200L;
        BigDecimal amount = new BigDecimal("250.00");
        Transaction.TransactionType type = Transaction.TransactionType.DONATION;
        Transaction.PaymentMethod paymentMethod = Transaction.PaymentMethod.GOPAY;
        String paymentPhone = "081234567890";
        String description = "Donation to help children";
        LocalDateTime timestamp = LocalDateTime.now();
        
        // Act
        Transaction transaction = Transaction.builder()
                .id(id)
                .walletId(walletId)
                .campaignId(campaignId)
                .amount(amount)
                .type(type)
                .paymentMethod(paymentMethod)
                .paymentPhone(paymentPhone)
                .description(description)
                .timestamp(timestamp)
                .deleted(false)
                .build();
        
        // Assert
        assertEquals(id, transaction.getId());
        assertEquals(walletId, transaction.getWalletId());
        assertEquals(campaignId, transaction.getCampaignId());
        assertEquals(amount, transaction.getAmount());
        assertEquals(type, transaction.getType());
        assertEquals(paymentMethod, transaction.getPaymentMethod());
        assertEquals(paymentPhone, transaction.getPaymentPhone());
        assertEquals(description, transaction.getDescription());
        assertEquals(timestamp, transaction.getTimestamp());
        assertFalse(transaction.isDeleted());
    }
    
    @Test
    void testTransactionSettersAndGetters() {
        // Arrange
        Transaction transaction = new Transaction();
        Long id = 1L;
        Long walletId = 100L;
        Long campaignId = 200L;
        BigDecimal amount = new BigDecimal("250.00");
        Transaction.TransactionType type = Transaction.TransactionType.DONATION;
        Transaction.PaymentMethod paymentMethod = Transaction.PaymentMethod.GOPAY;
        String paymentPhone = "081234567890";
        String description = "Donation to help children";
        LocalDateTime timestamp = LocalDateTime.now();
        
        // Act
        transaction.setId(id);
        transaction.setWalletId(walletId);
        transaction.setCampaignId(campaignId);
        transaction.setAmount(amount);
        transaction.setType(type);
        transaction.setPaymentMethod(paymentMethod);
        transaction.setPaymentPhone(paymentPhone);
        transaction.setDescription(description);
        transaction.setTimestamp(timestamp);
        transaction.setDeleted(false);
        
        // Assert
        assertEquals(id, transaction.getId());
        assertEquals(walletId, transaction.getWalletId());
        assertEquals(campaignId, transaction.getCampaignId());
        assertEquals(amount, transaction.getAmount());
        assertEquals(type, transaction.getType());
        assertEquals(paymentMethod, transaction.getPaymentMethod());
        assertEquals(paymentPhone, transaction.getPaymentPhone());
        assertEquals(description, transaction.getDescription());
        assertEquals(timestamp, transaction.getTimestamp());
        assertFalse(transaction.isDeleted());
    }
    
    @Test
    void testPrePersist() {
        // Arrange
        Transaction transaction = new Transaction();
        
        // Act
        transaction.onCreate();
        
        // Assert
        assertNotNull(transaction.getTimestamp());
    }
    
    @Test
    void testTransactionTypeEnum() {
        // Assert
        assertEquals(3, Transaction.TransactionType.values().length);
        assertEquals(Transaction.TransactionType.TOP_UP, Transaction.TransactionType.valueOf("TOP_UP"));
        assertEquals(Transaction.TransactionType.DONATION, Transaction.TransactionType.valueOf("DONATION"));
        assertEquals(Transaction.TransactionType.WITHDRAWAL, Transaction.TransactionType.valueOf("WITHDRAWAL"));
    }
    
    @Test
    void testPaymentMethodEnum() {
        // Assert
        assertEquals(2, Transaction.PaymentMethod.values().length);
        assertEquals(Transaction.PaymentMethod.GOPAY, Transaction.PaymentMethod.valueOf("GOPAY"));
        assertEquals(Transaction.PaymentMethod.DANA, Transaction.PaymentMethod.valueOf("DANA"));
    }
}