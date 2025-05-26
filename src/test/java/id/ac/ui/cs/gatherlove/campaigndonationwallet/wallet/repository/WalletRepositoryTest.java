package id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.repository;

import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.model.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletRepositoryTest {

    @Mock
    private WalletRepository walletRepository;

    private Wallet wallet;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        wallet = Wallet.builder()
                .userId(userId)
                .balance(BigDecimal.valueOf(1000))
                .build();
    }

    @Test
    void testFindByUserId() {
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));

        Optional<Wallet> result = walletRepository.findByUserId(userId);

        assertTrue(result.isPresent());
        assertEquals(userId, result.get().getUserId());
        assertEquals(BigDecimal.valueOf(1000), result.get().getBalance());
        verify(walletRepository, times(1)).findByUserId(userId);
    }

    @Test
    void testFindByUserId_NotFound() {
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.empty());

        Optional<Wallet> result = walletRepository.findByUserId(userId);

        assertTrue(result.isEmpty());
        verify(walletRepository, times(1)).findByUserId(userId);
    }
}