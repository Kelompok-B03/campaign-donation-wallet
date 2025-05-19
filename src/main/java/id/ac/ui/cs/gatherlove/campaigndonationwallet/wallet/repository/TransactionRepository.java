package id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.repository;

import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.model.Transaction;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.model.Transaction.TransactionType;
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

    List<Transaction> findByWalletIdAndDeletedFalseOrderByTimestampDesc(Long walletId);

    Page<Transaction> findByWalletIdAndDeletedFalseOrderByTimestampDesc(Long walletId, Pageable pageable);

    List<Transaction> findTop5ByWalletIdAndDeletedFalseOrderByTimestampDesc(Long walletId);

    List<Transaction> findByWalletIdAndTypeAndDeletedFalseOrderByTimestampDesc(Long walletId, TransactionType type);

    Optional<Transaction> findByIdAndDeletedFalse(Long id);

    Optional<Transaction> findByIdAndWalletIdAndDeletedFalse(Long id, Long walletId);

    List<Transaction> findByCampaignIdAndDeletedFalseOrderByTimestampDesc(Long campaignId);

    @Modifying
    @Query("UPDATE Transaction t SET t.deleted = true WHERE t.id = :id AND t.walletId = :walletId AND t.type = :type")
    int softDeleteTransaction(@Param("id") Long id, @Param("walletId") Long walletId, @Param("type") TransactionType type);
    
    boolean existsByCampaignIdAndTypeAndDeletedFalse(Long campaignId, TransactionType type);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.campaignId = :campaignId AND t.type = :type AND t.deleted = false")
    Double sumAmountByCampaignIdAndType(@Param("campaignId") Long campaignId, @Param("type") TransactionType type);
}