package id.ac.ui.cs.gatherlove.campaigndonationwallet.repository;

import id.ac.ui.cs.gatherlove.campaigndonationwallet.model.Donation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DonationRepository extends JpaRepository<Donation, UUID> {

    // Skeleton methods (to be implemented/customized later)

    List<Donation> findByDonationId(UUID donationId);

    List<Donation> findByUserId(UUID userId);

    List<Donation> findByCampaignId(UUID campaignId);

    List<Donation> findByStateName(String stateName);
}

