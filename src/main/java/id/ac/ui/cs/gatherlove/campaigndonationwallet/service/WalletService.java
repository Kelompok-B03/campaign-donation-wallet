package id.ac.ui.cs.gatherlove.campaigndonationwallet.service;

import id.ac.ui.cs.gatherlove.campaigndonationwallet.dto.WalletDTOs.TopUpRequestDTO;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.dto.WalletDTOs.TransactionDTO;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.dto.WalletDTOs.WalletBalanceDTO;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.model.Transaction.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface WalletService {
    
    // Get wallet balance by user ID
    WalletBalanceDTO getWalletBalance(Long userId);
    
    // Process a top-up transaction
    TransactionDTO topUpWallet(TopUpRequestDTO request);
    
    // Get recent transactions for a wallet
    List<TransactionDTO> getRecentTransactions(Long userId, int limit);
    
    // Get paginated transaction history for a wallet
    Page<TransactionDTO> getTransactionHistory(Long userId, Pageable pageable);
    
    // Get transactions of a specific type for a wallet
    List<TransactionDTO> getTransactionsByType(Long userId, TransactionType type);
    
    // Delete a top-up transaction record (soft delete)
    boolean deleteTopUpTransaction(Long userId, Long transactionId);
    
    // Process a withdrawal from a campaign. Campaign eligibility check is assumed to be done by the Campaign service before calling this method
    TransactionDTO withdrawCampaignFunds(Long userId, Long campaignId, BigDecimal amount);
    
    // Record a donation transaction. This is called from the Donation service when a user makes a donation
    TransactionDTO recordDonation(Long userId, Long campaignId, BigDecimal amount, String description);
}