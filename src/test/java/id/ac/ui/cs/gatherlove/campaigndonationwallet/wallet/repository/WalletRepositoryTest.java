package id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.repository;

import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.model.Wallet;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.repository.WalletRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class WalletRepositoryTest {

    @Autowired
    private WalletRepository walletRepository;

    @Test
    @DisplayName("Should find wallet by userId")
    void testFindByUserId() {
        UUID userId = UUID.randomUUID();
        Wallet wallet = Wallet.builder()
                .userId(userId)
                .balance(BigDecimal.valueOf(1000))
                .build();
        walletRepository.save(wallet);

        Optional<Wallet> result = walletRepository.findByUserId(userId);

        assertThat(result).isPresent();
        assertThat(result.get().getBalance()).isEqualTo(BigDecimal.valueOf(1000));
        assertThat(result.get().getUserId()).isEqualTo(userId);
    }
}