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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {
    
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final TopUpStrategyContext topUpStrategyContext;
    
    @Override
    public Wallet createWallet(UUID userId) {
        if (walletRepository.findByUserId(userId).isPresent()) {
            throw new IllegalArgumentException("Wallet already exists for user");
        }

        Wallet wallet = Wallet.builder()
            .userId(userId)
            .balance(BigDecimal.ZERO)
            .build();

        return walletRepository.save(wallet);
    }

    @Override
    public WalletBalanceDTO getWalletBalance(UUID userId) {
        Wallet wallet = getWalletByUserId(userId);
        return WalletBalanceDTO.builder()
                .userId(userId)
                .balance(wallet.getBalance())
                .build();
    }
    
    @Override
    @Transactional
    public TransactionDTO topUpWallet(TopUpRequestDTO request) {
        topUpStrategyContext.executeTopUp(request);

        Wallet wallet = getWalletByUserId(request.getUserId());

        Transaction transaction = Transaction.builder()
                .walletId(wallet.getId())
                .amount(request.getAmount())
                .type(TransactionType.TOP_UP)
                .paymentMethod(request.getPaymentMethod())
                .paymentPhone(request.getPaymentPhone())
                .description("Top-up via " + request.getPaymentMethod())
                .timestamp(LocalDateTime.now())
                .deleted(false)
                .build();

        wallet.setBalance(wallet.getBalance().add(request.getAmount()));
        walletRepository.save(wallet);

        Transaction savedTransaction = transactionRepository.save(transaction);

        return mapToTransactionDTO(savedTransaction);
    }
    
    @Override
    public List<TransactionDTO> getRecentTransactions(UUID userId, int limit) {
        Wallet wallet = getWalletByUserId(userId);
        
        List<Transaction> transactions;
        if (limit <= 0) {
            limit = 5; // Default to 5 if invalid limit
        }
        
        if (limit == 5) {
            transactions = transactionRepository.findTop5ByWalletIdAndDeletedFalseOrderByTimestampDesc(wallet.getId());
        } else {
            Page<Transaction> transactionPage = transactionRepository.findByWalletIdAndDeletedFalseOrderByTimestampDesc(
                    wallet.getId(), Pageable.ofSize(limit));
            transactions = transactionPage.getContent();
        }
        
        return transactions.stream()
                .map(this::mapToTransactionDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public Page<TransactionDTO> getTransactionHistory(UUID userId, Pageable pageable) {
        Wallet wallet = getWalletByUserId(userId);
        
        Page<Transaction> transactions = transactionRepository.findByWalletIdAndDeletedFalseOrderByTimestampDesc(
                wallet.getId(), pageable);
        
        return transactions.map(this::mapToTransactionDTO);
    }
    
    @Override
    public List<TransactionDTO> getTransactionsByType(UUID userId, TransactionType type) {
        Wallet wallet = getWalletByUserId(userId);
        
        List<Transaction> transactions = transactionRepository.findByWalletIdAndTypeAndDeletedFalseOrderByTimestampDesc(
                wallet.getId(), type);
        
        return transactions.stream()
                .map(this::mapToTransactionDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public boolean deleteTopUpTransaction(UUID userId, Long transactionId) {
        Wallet wallet = getWalletByUserId(userId);
        
        Transaction transaction = transactionRepository.findByIdAndWalletIdAndDeletedFalse(transactionId, wallet.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + transactionId));
        
        if (transaction.getType() != TransactionType.TOP_UP) {
            throw new TransactionNotAllowedException("Only TOP_UP transactions can be deleted");
        }
        
        walletRepository.save(wallet);

        int updated = transactionRepository.softDeleteTransaction(transactionId, wallet.getId(), TransactionType.TOP_UP);
        
        return updated > 0;
    }
    
    @Override
    @Transactional
    public TransactionDTO withdrawCampaignFunds(UUID userId, String campaignId, BigDecimal amount) {
        Wallet wallet = getWalletByUserId(userId);

        boolean alreadyWithdrawn = transactionRepository.existsByCampaignIdAndTypeAndDeletedFalse(
                campaignId, TransactionType.WITHDRAWAL);
        if (alreadyWithdrawn) {
            throw new TransactionNotAllowedException("Funds from this campaign have already been withdrawn");
        }

        Transaction transaction = Transaction.builder()
                .walletId(wallet.getId())
                .campaignId(campaignId)
                .amount(amount)
                .type(TransactionType.WITHDRAWAL)
                .description("Withdrawal from campaign #" + campaignId)
                .timestamp(LocalDateTime.now())
                .deleted(false)
                .build();

        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);

        Transaction savedTransaction = transactionRepository.save(transaction);
        
        return mapToTransactionDTO(savedTransaction);
    }
    
    @Override
    @Transactional
    public TransactionDTO recordDonation(UUID userId, String campaignId, BigDecimal amount, String description) {
        Wallet wallet = getWalletByUserId(userId);
        
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance to make donation");
        }

        Transaction transaction = Transaction.builder()
                .walletId(wallet.getId())
                .campaignId(campaignId)
                .amount(amount)
                .type(TransactionType.DONATION)
                .description(description != null ? description : "Donation to campaign #" + campaignId)
                .timestamp(LocalDateTime.now())
                .deleted(false)
                .build();

        wallet.setBalance(wallet.getBalance().subtract(amount));
        walletRepository.save(wallet);

        Transaction savedTransaction = transactionRepository.save(transaction);
        
        return mapToTransactionDTO(savedTransaction);
    }

    private Wallet getWalletByUserId(UUID userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for user: " + userId));
    }

    private TransactionDTO mapToTransactionDTO(Transaction transaction) {
        return TransactionDTO.builder()
                .id(transaction.getId())
                .walletId(transaction.getWalletId())
                .campaignId(transaction.getCampaignId())
                .amount(transaction.getAmount())
                .type(transaction.getType())
                .paymentMethod(transaction.getPaymentMethod())
                .paymentPhone(transaction.getPaymentPhone())
                .description(transaction.getDescription())
                .timestamp(transaction.getTimestamp())
                .build();
    }
}