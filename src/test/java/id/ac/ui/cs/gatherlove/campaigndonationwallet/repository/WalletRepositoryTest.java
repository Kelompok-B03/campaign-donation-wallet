package id.ac.ui.cs.gatherlove.campaigndonationwallet.repository;

import id.ac.ui.cs.gatherlove.campaigndonationwallet.model.Wallet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class WalletRepositoryTest {

    @Autowired
    private WalletRepository walletRepository;

    @Test
    void testFindByUserId() {
        Wallet wallet = Wallet.builder()
                .userId(42L)
                .balance(new BigDecimal("1000.00"))
                .build();

        walletRepository.save(wallet);
        assertThat(walletRepository.findByUserId(42L)).isPresent();
    }
}
