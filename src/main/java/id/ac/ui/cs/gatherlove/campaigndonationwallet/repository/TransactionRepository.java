package id.ac.ui.cs.gatherlove.campaigndonationwallet.repository;

import id.ac.ui.cs.gatherlove.campaigndonationwallet.model.Transaction;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.model.Transaction.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    // Find transactions by wallet ID where deleted is false
    List<Transaction> findByWalletIdAndDeletedFalseOrderByTimestampDesc(Long walletId);
    
    // Find paginated transactions by wallet ID where deleted is false
    Page<Transaction> findByWalletIdAndDeletedFalseOrderByTimestampDesc(Long walletId, Pageable pageable);
    
    // Find the latest 5 transactions for a wallet
    List<Transaction> findTop5ByWalletIdAndDeletedFalseOrderByTimestampDesc(Long walletId);
    
    // Find transactions by wallet ID and transaction type where deleted is false
    List<Transaction> findByWalletIdAndTypeAndDeletedFalseOrderByTimestampDesc(Long walletId, TransactionType type);
    
    // Find a transaction by ID where deleted is false
    Optional<Transaction> findByIdAndDeletedFalse(Long id);
    
    // Find a transaction by ID and wallet ID where deleted is false
    Optional<Transaction> findByIdAndWalletIdAndDeletedFalse(Long id, Long walletId);
    
    // Find transactions by campaign ID where deleted is false
    List<Transaction> findByCampaignIdAndDeletedFalseOrderByTimestampDesc(Long campaignId);
    
    // Mark a transaction as deleted (soft delete)
    @Modifying
    @Query("UPDATE Transaction t SET t.deleted = true WHERE t.id = :id AND t.walletId = :walletId AND t.type = :type")
    int softDeleteTransaction(@Param("id") Long id, @Param("walletId") Long walletId, @Param("type") TransactionType type);
    
    // Check if a withdrawal already exists for a campaign
    boolean existsByCampaignIdAndTypeAndDeletedFalse(Long campaignId, TransactionType type);
    
    // Calculate the total donation amount for a campaign
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.campaignId = :campaignId AND t.type = :type AND t.deleted = false")
    Double sumAmountByCampaignIdAndType(@Param("campaignId") Long campaignId, @Param("type") TransactionType type);
}