package id.ac.ui.cs.gatherlove.campaigndonationwallet.donation.repository;

import id.ac.ui.cs.gatherlove.campaigndonationwallet.donation.model.Donation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DonationRepository extends JpaRepository<Donation, UUID> {

    Donation findByDonationId(UUID donationId);

    List<Donation> findByUserId(UUID userId);

    List<Donation> findByCampaignId(String campaignId);

    List<Donation> findByStateName(String stateName);
}

