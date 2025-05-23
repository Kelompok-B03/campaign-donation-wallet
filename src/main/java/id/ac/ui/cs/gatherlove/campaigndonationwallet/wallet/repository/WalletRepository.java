package id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.repository;

import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    
    Optional<Wallet> findByUserId(Long userId);
}