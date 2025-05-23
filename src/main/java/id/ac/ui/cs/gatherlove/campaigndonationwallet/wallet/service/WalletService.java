package id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.service;

import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.dto.WalletDTOs.TopUpRequestDTO;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.dto.WalletDTOs.TransactionDTO;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.dto.WalletDTOs.WalletBalanceDTO;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.model.Transaction.TransactionType;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.model.Wallet;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface WalletService {
    
    Wallet createWallet(UUID userId);

    WalletBalanceDTO getWalletBalance(UUID userId);
    
    TransactionDTO topUpWallet(TopUpRequestDTO request);

    List<TransactionDTO> getRecentTransactions(UUID userId, int limit);

    Page<TransactionDTO> getTransactionHistory(UUID userId, Pageable pageable);

    List<TransactionDTO> getTransactionsByType(UUID userId, TransactionType type);

    boolean deleteTopUpTransaction(UUID userId, Long transactionId);

    TransactionDTO withdrawCampaignFunds(UUID userId, String campaignId, BigDecimal amount);

    TransactionDTO recordDonation(UUID userId, String campaignId, BigDecimal amount, String description);
}