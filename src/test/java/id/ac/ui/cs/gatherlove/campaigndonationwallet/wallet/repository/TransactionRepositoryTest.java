package id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.repository;

import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.model.Transaction;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.model.Transaction.TransactionType;

import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.repository.TransactionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    @DisplayName("Should find transactions by walletId and deleted false")
    void testFindByWalletIdAndDeletedFalse() {
        Transaction transaction = Transaction.builder()
                .walletId(1L)
                .amount(BigDecimal.TEN)
                .type(TransactionType.TOP_UP)
                .deleted(false)
                .build();
        transactionRepository.save(transaction);

        List<Transaction> result = transactionRepository.findByWalletIdAndDeletedFalseOrderByTimestampDesc(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAmount()).isEqualTo(BigDecimal.TEN);
    }
}