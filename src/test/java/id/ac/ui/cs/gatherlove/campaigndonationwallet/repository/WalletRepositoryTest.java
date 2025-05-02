package id.ac.ui.cs.gatherlove.campaigndonationwallet.repository;

import id.ac.ui.cs.gatherlove.campaigndonationwallet.model.Wallet;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class WalletRepositoryTest {

    @Autowired
    private WalletRepository walletRepository;

    @Test
    @DisplayName("Should find wallet by userId")
    void testFindByUserId() {
        Wallet wallet = Wallet.builder()
                .userId(99L)
                .balance(BigDecimal.valueOf(1000))
                .build();
        walletRepository.save(wallet);

        Optional<Wallet> result = walletRepository.findByUserId(99L);

        assertThat(result).isPresent();
        assertThat(result.get().getBalance()).isEqualTo(BigDecimal.valueOf(1000));
    }
}