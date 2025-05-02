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
    void testTopUpWallet_Success() {
        Wallet wallet = Wallet.builder().id(1L).userId(1L).balance(BigDecimal.ZERO).build();
        when(walletRepository.findByUserId(1L)).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TopUpRequestDTO request = TopUpRequestDTO.builder()
                .userId(1L)
                .amount(BigDecimal.TEN)
                .paymentMethod(Transaction.PaymentMethod.GOPAY)
                .paymentPhone("081234567890")
                .build();

        TransactionDTO result = walletService.topUpWallet(request);

        assertThat(result.getAmount()).isEqualTo(BigDecimal.TEN);
        assertThat(result.getType()).isEqualTo(TransactionType.TOP_UP);
        verify(walletRepository).save(any(Wallet.class));
        verify(transactionRepository).save(any(Transaction.class));
    }
}
