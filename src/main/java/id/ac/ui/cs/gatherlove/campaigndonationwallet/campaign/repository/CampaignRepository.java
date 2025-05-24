package id.ac.ui.cs.gatherlove.campaigndonationwallet.campaign.repository;

import id.ac.ui.cs.gatherlove.campaigndonationwallet.campaign.model.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, String> {
    List<Campaign> findByFundraiserId(String fundraiserId);
    List<Campaign> findByStatus(String status);
}
