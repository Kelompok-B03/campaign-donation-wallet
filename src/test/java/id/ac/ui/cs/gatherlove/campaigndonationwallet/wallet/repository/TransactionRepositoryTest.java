package id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.repository;

import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.model.Transaction;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.model.Transaction.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionRepositoryTest {

    @Mock
    private TransactionRepository transactionRepository;

    private Transaction transaction;

    @BeforeEach
    void setUp() {
        transaction = Transaction.builder()
                .id(1L)
                .walletId(2L)
                .campaignId("camp123")
                .amount(BigDecimal.TEN)
                .type(TransactionType.TOP_UP)
                .deleted(false)
                .build();
    }

    @Test
    void testFindByIdAndDeletedFalse() {
        when(transactionRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(transaction));

        Optional<Transaction> result = transactionRepository.findByIdAndDeletedFalse(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(transactionRepository, times(1)).findByIdAndDeletedFalse(1L);
    }

    @Test
    void testFindByWalletIdAndDeletedFalseOrderByTimestampDesc() {
        when(transactionRepository.findByWalletIdAndDeletedFalseOrderByTimestampDesc(2L)).thenReturn(List.of(transaction));

        List<Transaction> result = transactionRepository.findByWalletIdAndDeletedFalseOrderByTimestampDesc(2L);

        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getWalletId());
        verify(transactionRepository, times(1)).findByWalletIdAndDeletedFalseOrderByTimestampDesc(2L);
    }

    @Test
    void testFindByCampaignIdAndDeletedFalseOrderByTimestampDesc() {
        when(transactionRepository.findByCampaignIdAndDeletedFalseOrderByTimestampDesc("camp123")).thenReturn(List.of(transaction));

        List<Transaction> result = transactionRepository.findByCampaignIdAndDeletedFalseOrderByTimestampDesc("camp123");

        assertEquals(1, result.size());
        assertEquals("camp123", result.get(0).getCampaignId());
        verify(transactionRepository, times(1)).findByCampaignIdAndDeletedFalseOrderByTimestampDesc("camp123");
    }

    @Test
    void testFindByWalletIdAndTypeAndDeletedFalseOrderByTimestampDesc() {
        when(transactionRepository.findByWalletIdAndTypeAndDeletedFalseOrderByTimestampDesc(2L, TransactionType.TOP_UP)).thenReturn(List.of(transaction));

        List<Transaction> result = transactionRepository.findByWalletIdAndTypeAndDeletedFalseOrderByTimestampDesc(2L, TransactionType.TOP_UP);

        assertEquals(1, result.size());
        assertEquals(TransactionType.TOP_UP, result.get(0).getType());
        verify(transactionRepository, times(1)).findByWalletIdAndTypeAndDeletedFalseOrderByTimestampDesc(2L, TransactionType.TOP_UP);
    }
}