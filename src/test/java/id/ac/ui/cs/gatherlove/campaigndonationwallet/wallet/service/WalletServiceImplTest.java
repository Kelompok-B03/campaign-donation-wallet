package id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.service;

import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.dto.WalletDTOs.WalletBalanceDTO;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.model.Wallet;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.repository.TransactionRepository;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.repository.WalletRepository;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.service.WalletServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class WalletServiceImplTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private WalletServiceImpl walletService;

    private UUID testUserId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUserId = UUID.randomUUID();
    }

    @Test
    void testGetWalletBalance_ReturnsBalance() {
        Wallet wallet = Wallet.builder().id(1L).userId(testUserId).balance(BigDecimal.valueOf(500)).build();
        when(walletRepository.findByUserId(testUserId)).thenReturn(Optional.of(wallet));

        WalletBalanceDTO result = walletService.getWalletBalance(testUserId);

        assertThat(result.getBalance()).isEqualTo(BigDecimal.valueOf(500));
        assertThat(result.getUserId()).isEqualTo(testUserId);
    }

    @Test
    void testCreateWallet_ShouldCreateAndReturnWallet() {
        UUID userId = UUID.randomUUID();

        when(walletRepository.findByUserId(userId)).thenReturn(Optional.empty());

        Wallet savedWallet = Wallet.builder()
            .id(1L)
            .userId(userId)
            .balance(BigDecimal.ZERO)
            .build();

        when(walletRepository.save(any(Wallet.class))).thenReturn(savedWallet);

        Wallet result = walletService.createWallet(userId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getBalance()).isEqualByComparingTo(BigDecimal.ZERO);

        verify(walletRepository).save(any(Wallet.class));
    }
}