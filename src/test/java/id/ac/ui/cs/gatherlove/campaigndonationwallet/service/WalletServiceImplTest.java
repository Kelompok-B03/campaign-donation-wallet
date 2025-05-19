package id.ac.ui.cs.gatherlove.campaigndonationwallet.service;

import id.ac.ui.cs.gatherlove.campaigndonationwallet.dto.WalletDTOs.TopUpRequestDTO;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.dto.WalletDTOs.TransactionDTO;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.dto.WalletDTOs.WalletBalanceDTO;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.model.Transaction;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.model.Transaction.TransactionType;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.model.Wallet;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.repository.TransactionRepository;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetWalletBalance_ReturnsBalance() {
        Wallet wallet = Wallet.builder().id(1L).userId(1L).balance(BigDecimal.valueOf(500)).build();
        when(walletRepository.findByUserId(1L)).thenReturn(Optional.of(wallet));

        WalletBalanceDTO result = walletService.getWalletBalance(1L);

        assertThat(result.getBalance()).isEqualTo(BigDecimal.valueOf(500));
    }

    @Test
    void testCreateWallet_ShouldCreateAndReturnWallet() {
        Long userId = 123L;

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
