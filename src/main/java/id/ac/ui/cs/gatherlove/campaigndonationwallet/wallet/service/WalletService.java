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

public interface WalletService {
    
    Wallet createWallet(Long userId);

    WalletBalanceDTO getWalletBalance(Long userId);
    
    TransactionDTO topUpWallet(TopUpRequestDTO request);

    List<TransactionDTO> getRecentTransactions(Long userId, int limit);

    Page<TransactionDTO> getTransactionHistory(Long userId, Pageable pageable);

    List<TransactionDTO> getTransactionsByType(Long userId, TransactionType type);

    boolean deleteTopUpTransaction(Long userId, Long transactionId);

    TransactionDTO withdrawCampaignFunds(Long userId, Long campaignId, BigDecimal amount);

    TransactionDTO recordDonation(Long userId, Long campaignId, BigDecimal amount, String description);
}