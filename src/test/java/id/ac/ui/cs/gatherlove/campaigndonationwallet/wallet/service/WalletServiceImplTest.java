package id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.service;

import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.dto.WalletDTOs.TopUpRequestDTO;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.dto.WalletDTOs.TransactionDTO;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.dto.WalletDTOs.WalletBalanceDTO;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.exception.InsufficientBalanceException;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.exception.ResourceNotFoundException;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.exception.TransactionNotAllowedException;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.model.Transaction;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.model.Transaction.TransactionType;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.model.Wallet;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.repository.TransactionRepository;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.repository.WalletRepository;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.strategy.TopUpStrategyContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.junit.jupiter.api.Assertions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class WalletServiceImplTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private WalletServiceImpl walletService;

    private UUID testUserId;

    @Mock
    private TopUpStrategyContext topUpStrategyContext;

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

        @Test
    void testCreateWallet_AlreadyExists_ShouldThrow() {
        Wallet existingWallet = Wallet.builder().id(1L).userId(testUserId).balance(BigDecimal.ZERO).build();
        when(walletRepository.findByUserId(testUserId)).thenReturn(Optional.of(existingWallet));
        Assertions.assertThrows(IllegalArgumentException.class, () -> walletService.createWallet(testUserId));
    }

    @Test
    void testGetWalletBalance_WalletNotFound_ShouldThrow() {
        when(walletRepository.findByUserId(testUserId)).thenReturn(Optional.empty());
        Assertions.assertThrows(ResourceNotFoundException.class, () -> walletService.getWalletBalance(testUserId));
    }

    @Test
    void testTopUpWallet_Success() {
        TopUpRequestDTO request = TopUpRequestDTO.builder()
                .userId(testUserId)
                .amount(BigDecimal.TEN)
                .paymentMethod(Transaction.PaymentMethod.GOPAY)
                .paymentPhone("081234567890")
                .build();

        Wallet wallet = Wallet.builder().id(1L).userId(testUserId).balance(BigDecimal.ZERO).build();
        Transaction transaction = Transaction.builder()
                .id(1L)
                .walletId(wallet.getId())
                .amount(BigDecimal.TEN)
                .type(TransactionType.TOP_UP)
                .paymentMethod(Transaction.PaymentMethod.GOPAY)
                .paymentPhone("081234567890")
                .timestamp(LocalDateTime.now())
                .deleted(false)
                .build();

        when(walletRepository.findByUserId(testUserId)).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        TransactionDTO result = walletService.topUpWallet(request);

        assertThat(result).isNotNull();
        assertThat(result.getAmount()).isEqualTo(BigDecimal.TEN);
        verify(topUpStrategyContext).executeTopUp(request);
    }

    @Test
    void testGetRecentTransactions_DefaultLimit() {
        Wallet wallet = Wallet.builder().id(1L).userId(testUserId).balance(BigDecimal.ZERO).build();
        List<Transaction> transactions = List.of(
                Transaction.builder().id(1L).walletId(wallet.getId()).type(TransactionType.TOP_UP).deleted(false).build()
        );
        when(walletRepository.findByUserId(testUserId)).thenReturn(Optional.of(wallet));
        when(transactionRepository.findTop5ByWalletIdAndDeletedFalseOrderByTimestampDesc(wallet.getId()))
                .thenReturn(transactions);

        List<TransactionDTO> result = walletService.getRecentTransactions(testUserId, 5);
        assertThat(result).hasSize(1);
    }

    @Test
    void testGetRecentTransactions_CustomLimit() {
        Wallet wallet = Wallet.builder().id(1L).userId(testUserId).balance(BigDecimal.ZERO).build();
        List<Transaction> transactions = List.of(
                Transaction.builder().id(1L).walletId(wallet.getId()).type(TransactionType.TOP_UP).deleted(false).build()
        );
        Page<Transaction> page = new PageImpl<>(transactions);
        when(walletRepository.findByUserId(testUserId)).thenReturn(Optional.of(wallet));
        when(transactionRepository.findByWalletIdAndDeletedFalseOrderByTimestampDesc(eq(wallet.getId()), any()))
                .thenReturn(page);

        List<TransactionDTO> result = walletService.getRecentTransactions(testUserId, 10);
        assertThat(result).hasSize(1);
    }

    @Test
    void testGetTransactionHistory() {
        Wallet wallet = Wallet.builder().id(1L).userId(testUserId).balance(BigDecimal.ZERO).build();
        List<Transaction> transactions = List.of(
                Transaction.builder().id(1L).walletId(wallet.getId()).type(TransactionType.TOP_UP).deleted(false).build()
        );
        Page<Transaction> page = new PageImpl<>(transactions);
        when(walletRepository.findByUserId(testUserId)).thenReturn(Optional.of(wallet));
        when(transactionRepository.findByWalletIdAndDeletedFalseOrderByTimestampDesc(eq(wallet.getId()), any(PageRequest.class)))
                .thenReturn(page);

        Page<TransactionDTO> result = walletService.getTransactionHistory(testUserId, PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void testGetTransactionsByType() {
        Wallet wallet = Wallet.builder().id(1L).userId(testUserId).balance(BigDecimal.ZERO).build();
        List<Transaction> transactions = List.of(
                Transaction.builder().id(1L).walletId(wallet.getId()).type(TransactionType.TOP_UP).deleted(false).build()
        );
        when(walletRepository.findByUserId(testUserId)).thenReturn(Optional.of(wallet));
        when(transactionRepository.findByWalletIdAndTypeAndDeletedFalseOrderByTimestampDesc(wallet.getId(), TransactionType.TOP_UP))
                .thenReturn(transactions);

        List<TransactionDTO> result = walletService.getTransactionsByType(testUserId, TransactionType.TOP_UP);
        assertThat(result).hasSize(1);
    }

    @Test
    void testDeleteTopUpTransaction_Success() {
        Wallet wallet = Wallet.builder().id(1L).userId(testUserId).balance(BigDecimal.ZERO).build();
        Transaction transaction = Transaction.builder()
                .id(1L)
                .walletId(wallet.getId())
                .type(TransactionType.TOP_UP)
                .deleted(false)
                .build();

        when(walletRepository.findByUserId(testUserId)).thenReturn(Optional.of(wallet));
        when(transactionRepository.findByIdAndWalletIdAndDeletedFalse(1L, wallet.getId()))
                .thenReturn(Optional.of(transaction));
        when(transactionRepository.softDeleteTransaction(1L, wallet.getId(), TransactionType.TOP_UP)).thenReturn(1);

        boolean result = walletService.deleteTopUpTransaction(testUserId, 1L);
        assertThat(result).isTrue();
    }

    @Test
    void testDeleteTopUpTransaction_NotFound() {
        Wallet wallet = Wallet.builder().id(1L).userId(testUserId).balance(BigDecimal.ZERO).build();
        when(walletRepository.findByUserId(testUserId)).thenReturn(Optional.of(wallet));
        when(transactionRepository.findByIdAndWalletIdAndDeletedFalse(1L, wallet.getId()))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> walletService.deleteTopUpTransaction(testUserId, 1L));
    }

    @Test
    void testDeleteTopUpTransaction_NotAllowed() {
        Wallet wallet = Wallet.builder().id(1L).userId(testUserId).balance(BigDecimal.ZERO).build();
        Transaction transaction = Transaction.builder()
                .id(1L)
                .walletId(wallet.getId())
                .type(TransactionType.DONATION)
                .deleted(false)
                .build();

        when(walletRepository.findByUserId(testUserId)).thenReturn(Optional.of(wallet));
        when(transactionRepository.findByIdAndWalletIdAndDeletedFalse(1L, wallet.getId()))
                .thenReturn(Optional.of(transaction));

        Assertions.assertThrows(TransactionNotAllowedException.class, () -> walletService.deleteTopUpTransaction(testUserId, 1L));
    }

    @Test
    void testWithdrawCampaignFunds_AlreadyWithdrawn() {
        Wallet wallet = Wallet.builder().id(1L).userId(testUserId).balance(BigDecimal.ZERO).build();
        when(walletRepository.findByUserId(testUserId)).thenReturn(Optional.of(wallet));
        when(transactionRepository.existsByCampaignIdAndTypeAndDeletedFalse("camp1", TransactionType.WITHDRAWAL)).thenReturn(true);

        Assertions.assertThrows(TransactionNotAllowedException.class, () ->
                walletService.withdrawCampaignFunds(testUserId, "camp1", BigDecimal.TEN));
    }

    @Test
    void testWithdrawCampaignFunds_Success() {
        Wallet wallet = Wallet.builder().id(1L).userId(testUserId).balance(BigDecimal.ZERO).build();
        when(walletRepository.findByUserId(testUserId)).thenReturn(Optional.of(wallet));
        when(transactionRepository.existsByCampaignIdAndTypeAndDeletedFalse("camp1", TransactionType.WITHDRAWAL)).thenReturn(false);

        Transaction transaction = Transaction.builder()
                .id(1L)
                .walletId(wallet.getId())
                .campaignId("camp1")
                .amount(BigDecimal.TEN)
                .type(TransactionType.WITHDRAWAL)
                .deleted(false)
                .timestamp(LocalDateTime.now())
                .build();

        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        TransactionDTO result = walletService.withdrawCampaignFunds(testUserId, "camp1", BigDecimal.TEN);
        assertThat(result).isNotNull();
        assertThat(result.getAmount()).isEqualTo(BigDecimal.TEN);
    }

    @Test
    void testRecordDonation_InsufficientBalance() {
        Wallet wallet = Wallet.builder().id(1L).userId(testUserId).balance(BigDecimal.ZERO).build();
        when(walletRepository.findByUserId(testUserId)).thenReturn(Optional.of(wallet));

        Assertions.assertThrows(InsufficientBalanceException.class, () ->
                walletService.recordDonation(testUserId, "camp1", BigDecimal.TEN, null));
    }

    @Test
    void testRecordDonation_Success() {
        Wallet wallet = Wallet.builder().id(1L).userId(testUserId).balance(BigDecimal.valueOf(100)).build();
        when(walletRepository.findByUserId(testUserId)).thenReturn(Optional.of(wallet));

        Transaction transaction = Transaction.builder()
                .id(1L)
                .walletId(wallet.getId())
                .campaignId("camp1")
                .amount(BigDecimal.TEN)
                .type(TransactionType.DONATION)
                .description("desc") // <-- FIX: set the description here!
                .deleted(false)
                .timestamp(LocalDateTime.now())
                .build();

        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        TransactionDTO result = walletService.recordDonation(testUserId, "camp1", BigDecimal.TEN, "desc");
        assertThat(result).isNotNull();
        assertThat(result.getAmount()).isEqualTo(BigDecimal.TEN);
        assertThat(result.getDescription()).isEqualTo("desc");
    }
}