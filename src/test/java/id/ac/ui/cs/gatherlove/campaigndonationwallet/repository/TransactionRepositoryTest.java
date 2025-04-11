package id.ac.ui.cs.gatherlove.campaigndonationwallet.repository;

import id.ac.ui.cs.gatherlove.campaigndonationwallet.model.Transaction;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.model.Transaction.TransactionType;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.model.Transaction.PaymentMethod;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TransactionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    void testFindByWalletIdAndDeletedFalseOrderByTimestampDesc() {
        // Arrange
        Transaction transaction1 = Transaction.builder()
                .walletId(1L)
                .amount(new BigDecimal("100.00"))
                .type(TransactionType.TOP_UP)
                .paymentMethod(PaymentMethod.GOPAY)
                .timestamp(LocalDateTime.now().minusDays(1))
                .deleted(false)
                .build();
        
        Transaction transaction2 = Transaction.builder()
                .walletId(1L)
                .amount(new BigDecimal("200.00"))
                .type(TransactionType.TOP_UP)
                .paymentMethod(PaymentMethod.DANA)
                .timestamp(LocalDateTime.now())
                .deleted(false)
                .build();
        
        Transaction deletedTransaction = Transaction.builder()
                .walletId(1L)
                .amount(new BigDecimal("300.00"))
                .type(TransactionType.TOP_UP)
                .paymentMethod(PaymentMethod.GOPAY)
                .timestamp(LocalDateTime.now())
                .deleted(true)
                .build();
        
        entityManager.persist(transaction1);
        entityManager.persist(transaction2);
        entityManager.persist(deletedTransaction);
        entityManager.flush();
        
        // Act
        List<Transaction> transactions = transactionRepository.findByWalletIdAndDeletedFalseOrderByTimestampDesc(1L);
        
        // Assert
        assertEquals(2, transactions.size());
        assertEquals(transaction2.getId(), transactions.get(0).getId()); // Most recent first
        assertEquals(transaction1.getId(), transactions.get(1).getId());
    }

    @Test
    void testFindByWalletIdAndDeletedFalseOrderByTimestampDescPaginated() {
        // Arrange
        for (int i = 0; i < 20; i++) {
            Transaction transaction = Transaction.builder()
                    .walletId(1L)
                    .amount(new BigDecimal("100.00"))
                    .type(TransactionType.TOP_UP)
                    .paymentMethod(PaymentMethod.GOPAY)
                    .timestamp(LocalDateTime.now().minusHours(i))
                    .deleted(false)
                    .build();
            entityManager.persist(transaction);
        }
        entityManager.flush();
        
        // Act
        Page<Transaction> page = transactionRepository.findByWalletIdAndDeletedFalseOrderByTimestampDesc(
                1L, PageRequest.of(0, 10));
        
        // Assert
        assertEquals(10, page.getContent().size());
        assertEquals(20, page.getTotalElements());
        assertEquals(2, page.getTotalPages());
    }

    @Test
    void testFindTop5ByWalletIdAndDeletedFalseOrderByTimestampDesc() {
        // Arrange
        for (int i = 0; i < 10; i++) {
            Transaction transaction = Transaction.builder()
                    .walletId(1L)
                    .amount(new BigDecimal("100.00"))
                    .type(TransactionType.TOP_UP)
                    .paymentMethod(PaymentMethod.GOPAY)
                    .timestamp(LocalDateTime.now().minusHours(i))
                    .deleted(false)
                    .build();
            entityManager.persist(transaction);
        }
        entityManager.flush();
        
        // Act
        List<Transaction> transactions = transactionRepository.findTop5ByWalletIdAndDeletedFalseOrderByTimestampDesc(1L);
        
        // Assert
        assertEquals(5, transactions.size());
    }

    @Test
    void testFindByWalletIdAndTypeAndDeletedFalseOrderByTimestampDesc() {
        // Arrange
        Transaction topUp = Transaction.builder()
                .walletId(1L)
                .amount(new BigDecimal("100.00"))
                .type(TransactionType.TOP_UP)
                .paymentMethod(PaymentMethod.GOPAY)
                .timestamp(LocalDateTime.now())
                .deleted(false)
                .build();
        
        Transaction donation = Transaction.builder()
                .walletId(1L)
                .campaignId(1L)
                .amount(new BigDecimal("50.00"))
                .type(TransactionType.DONATION)
                .timestamp(LocalDateTime.now())
                .deleted(false)
                .build();
        
        entityManager.persist(topUp);
        entityManager.persist(donation);
        entityManager.flush();
        
        // Act
        List<Transaction> topUps = transactionRepository.findByWalletIdAndTypeAndDeletedFalseOrderByTimestampDesc(
                1L, TransactionType.TOP_UP);
        List<Transaction> donations = transactionRepository.findByWalletIdAndTypeAndDeletedFalseOrderByTimestampDesc(
                1L, TransactionType.DONATION);
        
        // Assert
        assertEquals(1, topUps.size());
        assertEquals(1, donations.size());
        assertEquals(topUp.getId(), topUps.get(0).getId());
        assertEquals(donation.getId(), donations.get(0).getId());
    }

    @Test
    void testFindByIdAndDeletedFalse() {
        // Arrange
        Transaction activeTransaction = Transaction.builder()
                .walletId(1L)
                .amount(new BigDecimal("100.00"))
                .type(TransactionType.TOP_UP)
                .timestamp(LocalDateTime.now())
                .deleted(false)
                .build();
        
        Transaction deletedTransaction = Transaction.builder()
                .walletId(1L)
                .amount(new BigDecimal("200.00"))
                .type(TransactionType.TOP_UP)
                .timestamp(LocalDateTime.now())
                .deleted(true)
                .build();
        
        entityManager.persist(activeTransaction);
        entityManager.persist(deletedTransaction);
        entityManager.flush();
        
        // Act
        Optional<Transaction> foundActive = transactionRepository.findByIdAndDeletedFalse(activeTransaction.getId());
        Optional<Transaction> foundDeleted = transactionRepository.findByIdAndDeletedFalse(deletedTransaction.getId());
        
        // Assert
        assertTrue(foundActive.isPresent());
        assertFalse(foundDeleted.isPresent());
    }

    @Test
    void testSoftDeleteTransaction() {
        // Arrange
        Transaction transaction = Transaction.builder()
                .walletId(1L)
                .amount(new BigDecimal("100.00"))
                .type(TransactionType.TOP_UP)
                .timestamp(LocalDateTime.now())
                .deleted(false)
                .build();
        
        entityManager.persist(transaction);
        entityManager.flush();
        
        // Act
        int updated = transactionRepository.softDeleteTransaction(
                transaction.getId(), transaction.getWalletId(), transaction.getType());
        
        // Assert
        assertEquals(1, updated);
        
        // Verify it's marked as deleted
        entityManager.clear(); // Clear persistence context to force reload from DB
        Transaction reloaded = entityManager.find(Transaction.class, transaction.getId());
        assertTrue(reloaded.isDeleted());
    }

    @Test
    void testExistsByCampaignIdAndTypeAndDeletedFalse() {
        // Arrange
        Transaction donation = Transaction.builder()
                .walletId(1L)
                .campaignId(1L)
                .amount(new BigDecimal("50.00"))
                .type(TransactionType.DONATION)
                .timestamp(LocalDateTime.now())
                .deleted(false)
                .build();
        
        entityManager.persist(donation);
        entityManager.flush();
        
        // Act & Assert
        assertTrue(transactionRepository.existsByCampaignIdAndTypeAndDeletedFalse(1L, TransactionType.DONATION));
        assertFalse(transactionRepository.existsByCampaignIdAndTypeAndDeletedFalse(1L, TransactionType.WITHDRAWAL));
        assertFalse(transactionRepository.existsByCampaignIdAndTypeAndDeletedFalse(2L, TransactionType.DONATION));
    }

    @Test
    void testSumAmountByCampaignIdAndType() {
        // Arrange
        Transaction donation1 = Transaction.builder()
                .walletId(1L)
                .campaignId(1L)
                .amount(new BigDecimal("50.00"))
                .type(TransactionType.DONATION)
                .timestamp(LocalDateTime.now())
                .deleted(false)
                .build();
        
        Transaction donation2 = Transaction.builder()
                .walletId(2L)
                .campaignId(1L)
                .amount(new BigDecimal("75.00"))
                .type(TransactionType.DONATION)
                .timestamp(LocalDateTime.now())
                .deleted(false)
                .build();
        
        Transaction deletedDonation = Transaction.builder()
                .walletId(3L)
                .campaignId(1L)
                .amount(new BigDecimal("100.00"))
                .type(TransactionType.DONATION)
                .timestamp(LocalDateTime.now())
                .deleted(true)
                .build();
        
        entityManager.persist(donation1);
        entityManager.persist(donation2);
        entityManager.persist(deletedDonation);
        entityManager.flush();
        
        // Act
        Double sum = transactionRepository.sumAmountByCampaignIdAndType(1L, TransactionType.DONATION);
        
        // Assert
        assertEquals(125.0, sum); // 50 + 75, excluding the deleted one
    }
}